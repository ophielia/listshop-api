package com.meg.listshop.lmt.list.state;


import com.meg.listshop.lmt.api.exception.ProcessingException;
import com.meg.listshop.lmt.data.entity.DishItemEntity;
import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.repository.ListItemDetailRepository;
import com.meg.listshop.lmt.data.repository.ListItemRepository;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public abstract class AbstractTransition implements StateTransition {

    protected ListItemRepository listItemRepository;
    protected ListItemDetailRepository listItemDetailRepository;

    public AbstractTransition(ListItemRepository listItemRepository, ListItemDetailRepository listItemDetailRepository) {
        this.listItemRepository = listItemRepository;
        this.listItemDetailRepository = listItemDetailRepository;
    }

    protected ListItemEntity getOrCreateItem(ItemStateContext context) throws ProcessingException {
        if (context.getTargetItem() != null) {
            return context.getTargetItem();
        }
        // no item here - we'll need to create one
        if (context.getTargetTag() == null) {
            throw new ProcessingException("No tag present when adding list item");
        }
        ListItemEntity item = new ListItemEntity();
        item.setListId(context.getTargetListId());
        item.setTag(context.getTargetTag());
        item.setAddedOn(new Date());
        return listItemRepository.save(item);
    }


}
