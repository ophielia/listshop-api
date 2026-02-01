package com.meg.listshop.lmt.list;

import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.service.AbstractItemCollector;
import com.meg.listshop.lmt.service.CollectedItem;
import com.meg.listshop.lmt.service.CollectorContext;

import java.util.List;

/**
 * Created by margaretmartin on 02/11/2017.
 */
public class ListItemCollector extends AbstractItemCollector {

    public ListItemCollector(Long listId, List<ListItemEntity> items) {
        super(listId, items);
    }


    // list collector
    public void removeItemByTagId(Long tagId, CollectorContext context) {
        if (!getTagCollectedMap().containsKey(tagId)) {
            return;
        }
        CollectedItem update = getTagCollectedMap().get(tagId);

        update.remove(context);

        getTagCollectedMap().put(tagId, update);
    }


}
