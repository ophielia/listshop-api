/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.list.state;

import com.meg.listshop.common.CommonUtils;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.common.data.repository.UnitRepository;
import com.meg.listshop.conversion.exceptions.ConversionAddException;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.conversion.EntityConvertibleAmount;
import com.meg.listshop.lmt.conversion.ListConversionService;
import com.meg.listshop.lmt.conversion.QuantityElements;
import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.repository.ListItemDetailRepository;
import com.meg.listshop.lmt.data.repository.ListItemRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.meg.listshop.common.FractionUtils.splitQuantityIntoElements;

@Component
@Qualifier("removeLinkTransition")
@Transactional
public class RemoveLinkTransition extends AbstractTransition {

    private static final Logger log = LoggerFactory.getLogger(RemoveLinkTransition.class);

    private final ListConversionService conversionService;
    private final UnitRepository unitRepository;


    public RemoveLinkTransition(ListItemRepository listItemRepository,
                                ListItemDetailRepository listItemDetailRepository,
                                ListConversionService conversionService,
                                UnitRepository unitRepository) {
        super(listItemRepository, listItemDetailRepository);

        this.conversionService = conversionService;
        this.unitRepository = unitRepository;
    }

    public ListItemEntity transitionToState(ListItemEvent listItemEvent, @NotNull ItemStateContext itemStateContext) throws ItemProcessingException {
        ListItemEntity item = getOrCreateItem(itemStateContext);

        // get list link to remove
        Long removeLinkId = itemStateContext.getRemoveLinkId();
        removeLinksForListId(itemStateContext, removeLinkId);

        item.setUpdatedOn(new Date());
        item.setLastChanged(new Date());
        return listItemRepository.save(item);
    }

    /*
    Removes links for list id.  All links will be updated to belong to the current list of the item, rather than
    the list id to be removed.
    If a detail already exists with the current list id / dish, the detail will be merged with the existing detail.
    We look at two cases in the details
          1 - details which have only the linked_list => linked list is updated to current list id
          2 - details which have the linked_list _and_ a linked_dish_id => linked list is updated to current list id
    For both of these cases, if a detail already exists with the same (new) linked list id and dish id, we
    merge the existing with the edited detail.

    This operation is non-destructive in  therms of the amount.  The amount at the beginning of the operation
    will not change.  However, the number of details belonging to the item may change.
     */
    private void removeLinksForListId(@NotNull ItemStateContext context,
                                      Long removeLinkListId) {
        ListItemEntity item = context.getTargetItem();
        Long currentListId = item.getListId();

        List<ListItemDetailEntity> toHandle = new ArrayList<>();
        for (ListItemDetailEntity detail : item.getDetails()) {
            if (detail.getLinkedListId() != null && detail.getLinkedListId().equals(removeLinkListId)) {
                toHandle.add(detail);
            }
        }

        if (toHandle.isEmpty()) {
            return;
        }

        List<ListItemDetailEntity> detailsToRemove = new ArrayList<>();
        toHandle.forEach(detail ->
                removeLink(detail, item.getDetails(), currentListId, item, detailsToRemove, context));

        if (!detailsToRemove.isEmpty()) {
            item.getDetails().removeAll(detailsToRemove);
        }
    }

    private void removeLink(ListItemDetailEntity detail, List<ListItemDetailEntity> details, Long currentListId,
                            ListItemEntity item, List<ListItemDetailEntity> detailsToRemove,
                            ItemStateContext context) {
        ListItemDetailEntity existing = details.stream()
                .filter(d -> DetailFilter.bothNullOrMatch(d.getLinkedListId(), currentListId))
                .filter(d -> DetailFilter.bothNullOrMatch(d.getLinkedDishId(), detail.getLinkedDishId()))
                .findFirst().orElse(null);
        if (existing == null) {
            // nothing existing - just update the linked list id, and return
            detail.setLinkedListId(currentListId);
            return;
        }
        mergeDetails(detail, existing, item, context);
        detailsToRemove.add(detail);
    }

    private void mergeDetails(ListItemDetailEntity detail, ListItemDetailEntity existing, ListItemEntity item,
                              ItemStateContext context) {
        boolean existingHasAmount = hasAmount(existing);
        boolean detailHasAmount = hasAmount(detail);

        if (!existingHasAmount && !detailHasAmount) {
            // neither have amounts
            mergeNonSpecifiedAmount(existing);
            return;
        }

        if (existingHasAmount && detailHasAmount) {
            // both have amounts
            doMergeWithAmounts(detail, existing, item, context);
        } else {
            doAddMixedDetail(detail, existing);
        }
    }


    private boolean hasAmount(ListItemDetailEntity detail) {
        return detail.getQuantity() != null;
    }

    private void mergeNonSpecifiedAmount(ListItemDetailEntity mergeInto) {
        mergeInto.setCount(mergeInto.getCount() + 1);
        mergeInto.setContainsUnspecified(true);
    }

    private void doMergeWithAmounts(ListItemDetailEntity detailToMerge, ListItemDetailEntity mergeInto, ListItemEntity item, ItemStateContext context) {
        UnitEntity unit = unitRepository.findById(detailToMerge.getUnitId()).orElse(null);
        TagEntity tag = item.getTag();

        EntityConvertibleAmount toMerge = new EntityConvertibleAmount(detailToMerge, unit, tag);
        if (mergeInto.getUnitId().equals(toMerge.getUnit().getId())) {
            // if simple add is possible (units equal) do it

            Double newQuantity = mergeInto.getQuantity() + toMerge.getQuantity();
            setQuantityInDetail(mergeInto, newQuantity);

            conversionService.recalculateDisplay(mergeInto, toMerge.getUnit());
            Integer count = CommonUtils.elvis(mergeInto.getCount(), 1);
            mergeInto.setCount(count + 1);
            return;
        }
        // otherwise, try add
        ConvertibleAmount convertedExisting = null;
        try {
            convertedExisting = conversionService.addToListItemDetail(toMerge, mergeInto, context);
        } catch (ConversionPathException | ConversionAddException | ConversionFactorException e) {
            // we weren't able to convert this - it happens sometimes
            String message = String.format("weren't able to add to existing detail [%s]. ", mergeInto.getItemDetailId());
            log.warn(message);
        }

        if (convertedExisting != null) {
            setQuantityInDetail(mergeInto, convertedExisting.getQuantity());
            mergeInto.setUnitId(convertedExisting.getUnit().getId());
            mergeInto.setUnitSize(convertedExisting.getUnitSize());
            mergeInto.setMarker(convertedExisting.getMarker());
            Integer count = CommonUtils.elvis(mergeInto.getCount(), 1);
            mergeInto.setCount(count + 1);
            conversionService.recalculateDisplay(mergeInto, convertedExisting.getUnit());
            return;
        }
        // otherwise, add mixed amount
        doAddMixedDetail(detailToMerge, mergeInto);

    }

    private void setQuantityInDetail(ListItemDetailEntity existing, Double newQuantity) {
        QuantityElements elements = splitQuantityIntoElements(newQuantity);
        existing.setFractionalQuantity(elements.fractionType());
        existing.setWholeQuantity(elements.wholeNumber());
        existing.setQuantity(elements.quantity());
    }

    private void doAddMixedDetail(ListItemDetailEntity toRemove, ListItemDetailEntity existing) {
        if (existing.getUnitId() == null) {
            UnitEntity unit = unitRepository.findById(toRemove.getUnitId()).orElse(null);
            // converted must have amount info - copy it into existing
            setQuantityInDetail(existing, toRemove.getQuantity());
            existing.setUnitId(toRemove.getUnitId());
            existing.setMarker(toRemove.getMarker());
            existing.setUnitSize(toRemove.getUnitSize());
            conversionService.recalculateDisplay(existing, unit);
        }
        // update count, and set unspecified
        Integer count = CommonUtils.elvis(existing.getCount(), 1);
        existing.setCount(count + 1);
        existing.setContainsUnspecified(true);

    }


}
