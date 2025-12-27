package com.meg.listshop.lmt.list.state;


import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.data.entity.ListItemEntity;

public interface StateTransition {

    ListItemEntity transitionToState(ListItemEvent listItemEvent, ItemStateContext itemStateContext) throws ItemProcessingException;
}
