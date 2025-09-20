package com.meg.listshop.lmt.list.state;

import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.repository.ListItemDetailRepository;
import com.meg.listshop.lmt.data.repository.ListItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Qualifier("removedTransition")
@Transactional
public class RemovedTransition extends AbstractTransition{

    public RemovedTransition(ListItemRepository listItemRepository, ListItemDetailRepository listItemDetailRepository) {
        super(listItemRepository, listItemDetailRepository);
    }

    public ListItemEntity transitionToState(ListItemEvent listItemEvent, ItemStateContext itemStateContext) throws ItemProcessingException  {
        validateContextForRemove(itemStateContext);
        // if context is removing by tag, we remove the entire item, and all details
        if (itemStateContext.getTag() != null) {
            Date updated = new Date();
            // remove entire item - still will do this logically, not physically
            ListItemEntity itemToRemove = itemStateContext.getTargetItem();
            itemToRemove.setUpdatedOn(updated);
            itemToRemove.setRemovedOn(updated);
            return listItemRepository.save(itemToRemove);
        }
        // just remove detail of dish or list
        //MM TODO
        return null;
    }

    private void validateContextForRemove(ItemStateContext itemStateContext) throws ItemProcessingException {
        // we want to make sure that we have a list and an item here
        // otherwise difficult to remove
        if (itemStateContext.getTargetListId() == null ||
        itemStateContext.getTargetItem() == null ) {
            String message = String.format("Target list id[%s] or item  [%s] null while removing item.",itemStateContext.getTargetListId(), itemStateContext.getTargetItem());
            throw new ItemProcessingException(message);
        }
    }
}
