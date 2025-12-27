package com.meg.listshop.lmt.list.state;

import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ListItemStateMachine {

    private HashMap<ListItemEvent, StateTransition> stateTransitionMap;

    public ListItemStateMachine(ActiveTransition activeTransition, CrossedOffTransition crossedOffTransition, RemovedTransition removedTransition) {
        stateTransitionMap = new HashMap<>();
        stateTransitionMap.put(ListItemEvent.ADD_ITEM, activeTransition);
        stateTransitionMap.put(ListItemEvent.CROSS_OFF_ITEM, crossedOffTransition);
        stateTransitionMap.put(ListItemEvent.REACTIVATE_ITEM, activeTransition);
        stateTransitionMap.put(ListItemEvent.REMOVE_ITEM, removedTransition);
    }


    public ListItemEntity handleEvent(ListItemEvent listItemEvent, ItemStateContext itemStateContext) throws ItemProcessingException {
        // get transition for event
        StateTransition stateTransition = stateTransitionMap.get(listItemEvent);

        // call transition
        return stateTransition.transitionToState(listItemEvent, itemStateContext);
    }
}
