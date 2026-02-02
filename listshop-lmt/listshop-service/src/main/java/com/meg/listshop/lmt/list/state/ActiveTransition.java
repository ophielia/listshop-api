package com.meg.listshop.lmt.list.state;

import com.meg.listshop.common.CommonUtils;
import com.meg.listshop.conversion.exceptions.ConversionAddException;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.conversion.ListConversionService;
import com.meg.listshop.lmt.data.entity.DishItemEntity;
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

import java.util.Date;

@Component
@Qualifier("activeTransition")
@Transactional
public class ActiveTransition extends AbstractTransition {

    private static final Logger log = LoggerFactory.getLogger(ActiveTransition.class);

    private final ListConversionService conversionService;

    public ActiveTransition(ListItemRepository listItemRepository, ListItemDetailRepository listItemDetailRepository, ListConversionService conversionService) {
        super(listItemRepository, listItemDetailRepository);

        this.conversionService = conversionService;
    }

    public ListItemEntity transitionToState(ListItemEvent listItemEvent, @NotNull ItemStateContext itemStateContext) throws ItemProcessingException {
        ListItemEntity item = getOrCreateItem(itemStateContext);

        // clear old states
        item.setRemovedOn(null);
        item.setCrossedOff(null);

        // fill item for each type
        ProcessingType processingType = determineProcessingType(itemStateContext);
        switch (processingType) {
            case DISH -> processAddDishItem(item, itemStateContext);
            case LIST -> processAddListItem(item, itemStateContext);
            case SIMPLE_ITEM -> processAddSimpleItem(item, itemStateContext);
            default -> throw new ItemProcessingException("Unexpected processing type");
            //TODO java 21 -  test for null here
        }
        return item;
    }

    /*
    Processes an addition of a dish item - resulting in a single added/updated item detail in the passed item.
    Result is scaled, summed and saved.
     */
    private void processAddDishItem(ListItemEntity item, @NotNull ItemStateContext itemStateContext) throws ItemProcessingException {
        DishItemEntity dishItem = itemStateContext.getDishItem();
        Long listSearchId = CommonUtils.elvis(itemStateContext.getListId(), item.getListId());
        // find existing
        ListItemDetailEntity existing = item.getDetails().stream().filter(detail -> DetailFilter.bothNullOrMatch(detail.getLinkedListId(), listSearchId)).filter(detail -> DetailFilter.bothNullOrMatch(detail.getLinkedDishId(), dishItem.getDish().getId())).findFirst().orElse(null);
        // convert dish item to list context or unit, if available
        ConvertibleAmount converted = null;
        try {
            converted = conversionService.convertDishItemForList(dishItem, existing, item, itemStateContext.getUserDomain());
        } catch (ConversionPathException | ConversionFactorException e) {
            // we weren't able to convert this - it happens sometimes
            String message = String.format("weren't able to convert dishItem [%s] to list context. ", dishItem.getDishItemId());
            log.warn(message);
        }

        // add list item
        if (converted != null && converted.getUnit() != null) {
            addSpecifiedAmountForDish(converted, item, existing, itemStateContext);
        } else {
            addNonSpecifiedAmount(existing, item, itemStateContext);
        }

        conversionService.sumItemDetails(item, itemStateContext);
        // save changes to item
        item.setUpdatedOn(new Date());
        listItemRepository.save(item);
    }

    /*
    Processes an addition of a list item - resulting in multiple added/updated item details in the passed item.
    Result is scaled, summed and saved.
     */
    private void processAddListItem(ListItemEntity item, @NotNull ItemStateContext itemStateContext) throws ItemProcessingException {
        ListItemEntity newListItem = itemStateContext.getListItem();

        for (ListItemDetailEntity newDetail : newListItem.getDetails()) {
            doAddListItemDetail(item, newDetail, itemStateContext);
        }

        conversionService.sumItemDetails(item, itemStateContext);
        // save changes to item
        item.setUpdatedOn(new Date());
        listItemRepository.save(item);
    }

    /*
Processes an addition of a list item - resulting in multiple added/updated item details in the passed item.
Result is scaled, summed and saved.
 */
    private void doAddListItemDetail(ListItemEntity addedTo, ListItemDetailEntity toAdd, @NotNull ItemStateContext itemStateContext) {
        ListItemEntity newListItem = itemStateContext.getListItem();
        Long listSearchId = CommonUtils.elvis(itemStateContext.getListId(), newListItem.getListId());
        // find existing
        ListItemDetailEntity existing = addedTo.getDetails().stream().filter(detail -> DetailFilter.bothNullOrMatch(detail.getLinkedListId(), listSearchId)).filter(detail -> DetailFilter.bothNullOrMatch(detail.getLinkedDishId(), toAdd.getLinkedDishId())).findFirst().orElse(null);

        // convert dish item to list context or unit, if available
        ConvertibleAmount converted = null;
        try {
            converted = conversionService.convertListItemDetailForList(toAdd, existing, addedTo, itemStateContext.getUserDomain());
        } catch (ConversionPathException | ConversionFactorException e) {
            // we weren't able to convert this - it happens sometimes
            String message = String.format("weren't able to convert dishItem [%s] to list context. ", newListItem.getListId());
            log.warn(message);
        }

        // add list item
        if (converted != null && converted.getUnit() != null) {
            addSpecifiedAmountForListItem(converted, addedTo, existing, toAdd, itemStateContext);
        } else {
            addNonSpecifiedAmount(existing, addedTo, itemStateContext);
        }
    }

    /*
        Processes an addition of a simple item - resulting in a single added/updated item detail in the passed item.
        New / changed detail is converted to list type, and ready to be summed
    */
    private void processAddSimpleItem(ListItemEntity item, @NotNull ItemStateContext itemStateContext) throws ItemProcessingException {
        TagEntity tag = itemStateContext.getTag();
        Long listSearchId = CommonUtils.elvis(itemStateContext.getTargetListId(), itemStateContext.getListId());
        // find existing
        ListItemDetailEntity existing = item.getDetails().stream().filter(detail -> DetailFilter.bothNullOrMatch(detail.getLinkedListId(), listSearchId)).filter(detail -> DetailFilter.bothNullOrMatch(detail.getLinkedDishId(), null)).findFirst().orElse(null);
        // convert dish item to list context or unit, if available
        ConvertibleAmount converted = null;
        try {
            converted = conversionService.convertTagForList(tag, itemStateContext.getTagAmount(), existing, item, itemStateContext.getUserDomain());
        } catch (ConversionPathException | ConversionFactorException e) {
            // we weren't able to convert this - it happens sometimes
            String message = String.format("weren't able to convert tag [%s] to list context. ", tag.getId());
            log.warn(message);
        }

        // add list item
        if (converted != null && converted.getUnit() != null) {
            addSpecifiedAmountForTag(converted, item, listSearchId, existing, itemStateContext);
        } else {
            addNonSpecifiedAmount(existing, item, itemStateContext);
        }

        conversionService.sumItemDetails(item, itemStateContext);
        // save changes to item
        item.setUpdatedOn(new Date());
        listItemRepository.save(item);

    }

    private void addNonSpecifiedAmount(ListItemDetailEntity existing, ListItemEntity item, @NotNull ItemStateContext context) {
        if (existing != null) {
            existing.setCount(existing.getCount() + 1);
            return;
        }
        Long detailListId = context.getTargetListId();
        if (context.getListItem() != null) {
            detailListId = context.getListItem().getListId();
        }
        ListItemDetailEntity newDetail = new ListItemDetailEntity();
        newDetail.setLinkedListId(detailListId);
        newDetail.setLinkedDishId(context.getDishId());
        newDetail.setCount(1);
        // add to list item
        newDetail.setItem(item);
        item.addDetailToItem(listItemDetailRepository.save(newDetail));
    }

    private void addSpecifiedAmountForListItem(ConvertibleAmount converted, ListItemEntity item, ListItemDetailEntity existing, ListItemDetailEntity addFrom, @NotNull ItemStateContext context) {
        Long listId = addFrom.getLinkedListId();
        Long dishId = addFrom.getLinkedDishId();
        String rawEntry = addFrom.getRawEntry();
        genericAddSpecifiedAmount(converted, item, existing, addFrom.isContainsUnspecified(), rawEntry, dishId, listId, context);
    }

    private void addSpecifiedAmountForDish(ConvertibleAmount converted, ListItemEntity item, ListItemDetailEntity existing, @NotNull ItemStateContext context) {
        String rawEntry = "";
        if (context.getDishItem() != null) {
            rawEntry = context.getDishItem().getRawEntry();
        }
        Long dishId = context.getDishItem().getDish().getId();
        Long linkedListId = CommonUtils.elvis(context.getListId(), item.getListId());
        genericAddSpecifiedAmount(converted, item, existing, false, rawEntry, dishId, linkedListId, context);
    }

    private void addSpecifiedAmountForTag(ConvertibleAmount converted, ListItemEntity item, Long listId, ListItemDetailEntity existing, @NotNull ItemStateContext context) {
        genericAddSpecifiedAmount(converted, item, existing, false, null, null, listId, context);
    }

    private void genericAddSpecifiedAmount(ConvertibleAmount converted, ListItemEntity item, ListItemDetailEntity existing, boolean containsUnspecified, String rawEntry, Long linkedDishId, Long linkedListId, @NotNull ItemStateContext context) {

        if (existing != null) {
            doAddToExisting(converted, existing, context);
            return;
        }

        ListItemDetailEntity newDetail = new ListItemDetailEntity();
        newDetail.setLinkedListId(linkedListId);
        newDetail.setLinkedDishId(linkedDishId);
        newDetail.setCount(1);
        newDetail.setQuantity(converted.getQuantity());
        newDetail.setRawEntry(rawEntry);
        newDetail.setUnitSize(converted.getUnitSize());
        newDetail.setMarker(converted.getMarker());
        newDetail.setUnitId(converted.getUnit().getId());
        newDetail.setContainsUnspecified(containsUnspecified);
        // add to list item
        newDetail.setItem(item);
        item.addDetailToItem(listItemDetailRepository.save(newDetail));


    }

    private void doAddToExisting(ConvertibleAmount converted, ListItemDetailEntity existing, @NotNull ItemStateContext context) {
        if (existing.getUnitId() == null) {
            // existing doesn't have amount, but converted to add does - this is adding a mixed amount
            doAddMixedDetail(converted, existing);
            return;
        } else if (existing.getUnitId().equals(converted.getUnit().getId())) {
            // if simple add is possible (units equal) do it

            Double newQuantity = existing.getQuantity() + converted.getQuantity();
            existing.setQuantity(newQuantity);
            Integer count = CommonUtils.elvis(existing.getCount(), 1);
            existing.setCount(count + 1);
            return;
        }
        // otherwise, try add
        ConvertibleAmount convertedExisting = null;
        try {
            convertedExisting = conversionService.addToListItemDetail(converted, existing, context);
        } catch (ConversionPathException | ConversionAddException | ConversionFactorException e) {
            // we weren't able to convert this - it happens sometimes
            String message = String.format("weren't able to add to existing detail [%s]. ", existing.getItemDetailId());
            log.warn(message);
        }

        if (convertedExisting != null) {
            existing.setQuantity(convertedExisting.getQuantity());
            existing.setUnitId(convertedExisting.getUnit().getId());
            existing.setUnitSize(convertedExisting.getUnitSize());
            existing.setMarker(convertedExisting.getMarker());
            Integer count = CommonUtils.elvis(existing.getCount(), 1);
            existing.setCount(count + 1);

            return;
        }
        // otherwise, add mixed amount
        doAddMixedDetail(converted, existing);

    }

    private void doAddMixedDetail(ConvertibleAmount converted, ListItemDetailEntity existing) {
        if (existing.getUnitId() == null) {
            // converted must have amount info - copy it into existing
            existing.setQuantity(converted.getQuantity());
            existing.setUnitId(converted.getUnit().getId());
            existing.setMarker(converted.getMarker());
            existing.setUnitSize(converted.getUnitSize());
            existing.setUserSize(converted.getUserSize());

        }
        // update count, and set unspecified
        Integer count = CommonUtils.elvis(existing.getCount(), 1);
        existing.setCount(count + 1);
        existing.setContainsUnspecified(true);

    }


}
