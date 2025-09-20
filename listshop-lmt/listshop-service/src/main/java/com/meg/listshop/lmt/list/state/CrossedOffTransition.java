package com.meg.listshop.lmt.list.state;

import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.repository.ListItemDetailRepository;
import com.meg.listshop.lmt.data.repository.ListItemRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("crossedOffTransition")
@Transactional
public class CrossedOffTransition  extends AbstractTransition {

    public CrossedOffTransition(ListItemRepository listItemRepository, ListItemDetailRepository listItemDetailRepository) {
        super(listItemRepository, listItemDetailRepository);
    }

    public ListItemEntity transitionToState(ListItemEvent listItemEvent, ItemStateContext itemStateContext) {
        return null;
    }
}
