package com.meg.listshop.lmt.list.state;

import com.meg.listshop.auth.data.entity.UserPropertyEntity;
import com.meg.listshop.auth.service.UserPropertyKey;
import com.meg.listshop.auth.service.UserPropertyService;
import com.meg.listshop.conversion.data.pojo.DomainType;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;

@Component
public class ListItemStateMachine {

    private EnumMap<ListItemEvent, StateTransition> stateTransitionMap;

    private UserPropertyService userPropertyService;

    @Autowired
    public ListItemStateMachine(ActiveTransition activeTransition,
                                CrossedOffTransition crossedOffTransition,
                                RemovedTransition removedTransition,
                                UserPropertyService userPropertyService) {
        stateTransitionMap = new EnumMap<>(ListItemEvent.class);
        stateTransitionMap.put(ListItemEvent.ADD_ITEM, activeTransition);
        stateTransitionMap.put(ListItemEvent.CROSS_OFF_ITEM, crossedOffTransition);
        stateTransitionMap.put(ListItemEvent.REACTIVATE_ITEM, activeTransition);
        stateTransitionMap.put(ListItemEvent.REMOVE_ITEM, removedTransition);
        this.userPropertyService = userPropertyService;
    }


    public ListItemEntity handleEvent(ListItemEvent listItemEvent, ItemStateContext itemStateContext, Long userId) throws ItemProcessingException {
        DomainType userDomainType = getDomainForUser(userId);
        itemStateContext.setUserDomain(userDomainType);
        // get transition for event
        StateTransition stateTransition = stateTransitionMap.get(listItemEvent);

        // call transition
        return stateTransition.transitionToState(listItemEvent, itemStateContext);
    }

    private DomainType getDomainForUser(Long userId) {

        UserPropertyEntity userProperty = null;
        try {
            userProperty = userPropertyService.getPropertyForUserById(userId, UserPropertyKey.PreferredDomain.getDisplayName());
        } catch (BadParameterException e) {
            throw new RuntimeException(e);
        }
        if (userProperty == null || userProperty.getValue() == null) {
            // return default
            return DomainType.US;
        }
        return DomainType.findByName(userProperty.getValue());
    }
}
