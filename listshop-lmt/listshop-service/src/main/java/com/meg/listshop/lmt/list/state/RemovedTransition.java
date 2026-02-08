package com.meg.listshop.lmt.list.state;

import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.api.model.v2.SpecificationType;
import com.meg.listshop.lmt.conversion.ListConversionService;
import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.repository.ListItemDetailRepository;
import com.meg.listshop.lmt.data.repository.ListItemRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Qualifier("removedTransition")
@Transactional
public class RemovedTransition extends AbstractTransition {

    private static final Logger log = LoggerFactory.getLogger(RemovedTransition.class);

    private final ListConversionService conversionService;

    public RemovedTransition(ListItemRepository listItemRepository,
                             ListItemDetailRepository listItemDetailRepository,
                             ListConversionService conversionService) {
        super(listItemRepository, listItemDetailRepository);
        this.conversionService = conversionService;
    }

    public ListItemEntity transitionToState(ListItemEvent listItemEvent, ItemStateContext itemStateContext) throws ItemProcessingException {
        validateContextForRemove(itemStateContext);
        ListItemEntity item = itemStateContext.getTargetItem();
        prepareItemForRemoval(item);

        ProcessingType processingType = determineProcessingType(itemStateContext);
        item = switch (processingType) {
            case DISH -> processRemoveDishItem(item, itemStateContext);
            case LIST -> processRemoveListItem(item, itemStateContext);
            case SIMPLE_ITEM -> processRemoveSimpleItem(item);
            default -> throw new ItemProcessingException("Unexpected processing type");
            //TODO java 21 -  test for null here
        };

        return item;
    }

    private ListItemEntity processRemoveSimpleItem(ListItemEntity item) {
        // simple item (tag) is removed physically from db, with all its items
        return removeItemPhysically(item);
    }

    private ListItemEntity processRemoveListItem(ListItemEntity item, ItemStateContext context) throws ItemProcessingException {
        Long listIdToRemove = context.getListId();
        // item is specified
        boolean isUnspecifiedAtBegin = item.getSpecificationType() != null && item.getSpecificationType() == SpecificationType.NONE;

        // gather all items belonging to list
        List<ListItemDetailEntity> toRemove = item.getDetails().stream()
                .filter(d -> DetailFilter.bothNotNullAndMatch(d.getLinkedListId(), listIdToRemove))
                .toList();


        // if we're removing all details, than just remove the entire item
        if (toRemove.size() == item.getDetails().size()) {
            return removeItemPhysically(item);
        }

        // remove these items
        removeDetails(item, toRemove);

        // re-sum item if necessary
        if (!isUnspecifiedAtBegin) {
            conversionService.sumItemDetails(item, context);
        }
        // save changes to item and return
        // save changes to item
        item.setUpdatedOn(new Date());
        return listItemRepository.save(item);
    }

    private void removeDetails(ListItemEntity item, List<ListItemDetailEntity> listToRemove) {
        for (ListItemDetailEntity detail : listToRemove) {
            item.getDetails().remove(detail);
            detail.setItem(null);
        }
        listItemDetailRepository.deleteAll(listToRemove);
    }

    private ListItemEntity processRemoveDishItem(ListItemEntity item, ItemStateContext context) throws ItemProcessingException {
        Long dishIdToRemove = context.getDishId();
        // item is specified
        boolean isUnspecifiedAtBegin = item.getSpecificationType() != null && item.getSpecificationType() == SpecificationType.NONE;

        // gather all items belonging to list
        List<ListItemDetailEntity> toRemove = item.getDetails().stream()
                .filter(d -> DetailFilter.bothNotNullAndMatch(d.getLinkedDishId(), dishIdToRemove))
                .toList();

        // if we're removing all details, than just remove the entire item
        if (toRemove.size() == item.getDetails().size()) {
            return removeItemPhysically(item);
        }

        // remove these items
        removeDetails(item, toRemove);

        // re-sum item if necessary
        if (!isUnspecifiedAtBegin) {
            conversionService.sumItemDetails(item, context);
        }
        // save changes to item and return
        // save changes to item
        item.setUpdatedOn(new Date());
        return listItemRepository.save(item);
    }

    private ListItemEntity removeItemPhysically(ListItemEntity itemToRemove) {
        listItemDetailRepository.deleteAll(itemToRemove.getDetails());
        listItemRepository.delete(itemToRemove);
        return null;
    }

    private void validateContextForRemove(ItemStateContext itemStateContext) throws ItemProcessingException {
        // we want to make sure that we have a list and an item here
        // otherwise difficult to remove
        if (itemStateContext.getTargetListId() == null ||
                itemStateContext.getTargetItem() == null) {
            String message = String.format("Target list id[%s] or item  [%s] null while removing item.", itemStateContext.getTargetListId(), itemStateContext.getTargetItem());
            throw new ItemProcessingException(message);
        }
    }

    private void prepareItemForRemoval(ListItemEntity item) {
        // item has a non null list of details
        // otherwise difficult to remove
        if (item.getDetails() == null) {
            String message = String.format("Target item [%s] doesn't have details.", item.getId());
            log.warn(message);
            item.setDetails(new ArrayList<>());
        }
    }
}
