package com.meg.listshop.lmt.list.state;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.repository.ListItemDetailRepository;
import com.meg.listshop.lmt.data.repository.ListItemRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Qualifier("crossedOffTransition")
@Transactional
public class CrossedOffTransition  extends AbstractTransition {

    public CrossedOffTransition(ListItemRepository listItemRepository, ListItemDetailRepository listItemDetailRepository) {
        super(listItemRepository, listItemDetailRepository);
    }

    public ListItemEntity transitionToState(ListItemEvent listItemEvent, ItemStateContext itemStateContext) {
        ListItemEntity item = getOrCreateItem(itemStateContext);

        Date crossedOffDate = itemStateContext.isCrossedOff() ? new Date() : null;
        // set crossed off
        item.setCrossedOff(crossedOffDate);

        // set update date
        item.setUpdatedOn(new Date());
        listItemRepository.save(item);
        return item;
    }
}
