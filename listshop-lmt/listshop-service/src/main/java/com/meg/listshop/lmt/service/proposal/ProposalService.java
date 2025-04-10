package com.meg.listshop.lmt.service.proposal;

import com.meg.listshop.lmt.data.entity.ProposalEntity;
import org.springframework.security.core.Authentication;

/**
 * Created by margaretmartin on 30/10/2017.
 */
public interface ProposalService {

    ProposalEntity getProposalById(String name, Long proposalId);

    ProposalEntity getTargetProposalById(String name, Long proposalId);

    void selectDishInSlot(String userName,  Long proposalId, Long slotId, Long dishId);

    void clearDishFromSlot(Authentication authentication, Long proposalId, Long slotId, Long dishId);
}
