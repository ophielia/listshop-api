package com.meg.listshop.lmt.list.state;

import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.repository.ListItemDetailRepository;
import com.meg.listshop.lmt.data.repository.ListItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Qualifier("removedTransition")
@Transactional
public class RemovedTransition extends AbstractTransition {

    public RemovedTransition(ListItemRepository listItemRepository, ListItemDetailRepository listItemDetailRepository) {
        super(listItemRepository, listItemDetailRepository);
    }

    public ListItemEntity transitionToState(ListItemEvent listItemEvent, ItemStateContext itemStateContext) throws ItemProcessingException {
        validateContextForRemove(itemStateContext);
        ListItemEntity targetItem = itemStateContext.getTargetItem();
        validateItemForRemove(targetItem);
        // if context is removing by tag, we remove the entire item, and all details
        if (itemStateContext.getTag() != null) {
            // remove entire item - still will do this logically, not physically
            removeItemLogically(targetItem);
            return listItemRepository.save(targetItem);
        }
        // just remove detail of dish or list
        removeDishOrListDetails(targetItem, itemStateContext.getDishId(), itemStateContext.getListId());

        if (RemovedTransition.getDetailCount(targetItem) == 0) {
            removeItemLogically(targetItem);
        }

        return listItemRepository.save(targetItem);
    }

    private void removeDishOrListDetails(ListItemEntity targetItem, Long dishId, Long listId) {
        List<ListItemDetailEntity> toRemove = new ArrayList<>();
        List<ListItemDetailEntity> toKeep = new ArrayList<>();
        targetItem.getDetails().forEach(detail -> {
            if (listId != null && listId.equals(detail.getLinkedListId()) ||
                    dishId != null && dishId.equals(detail.getLinkedDishId())) {
                detail.setItem(null);
                toRemove.add(detail);
            } else {
                toKeep.add(detail);
            }
        });
        listItemDetailRepository.deleteAll(toRemove);
        targetItem.setDetails(toKeep);
        toRemove.addAll(targetItem.getDetails());
    }

    private void removeItemLogically(ListItemEntity itemToRemove) {
        Date updated = new Date();
        itemToRemove.setUpdatedOn(updated);
        itemToRemove.setRemovedOn(updated);

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

    private void validateItemForRemove(ListItemEntity item) throws ItemProcessingException {
        // item has a non null list of details
        // otherwise difficult to remove
        if (item.getDetails() == null) {
            String message = String.format("Target item [%s] doesn't have details.", item.getId());
            throw new ItemProcessingException(message);
        }
    }
}
