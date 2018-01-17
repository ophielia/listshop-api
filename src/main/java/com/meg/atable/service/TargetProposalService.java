package com.meg.atable.service;

import com.meg.atable.api.model.SortDirection;
import com.meg.atable.data.entity.TargetProposalEntity;

import java.security.Principal;

/**
 * Created by margaretmartin on 30/10/2017.
 */
public interface TargetProposalService {

    TargetProposalEntity getTargetProposalById(String name, Long proposalId);

    TargetProposalEntity fillInformationForProposal(TargetProposalEntity proposalEntity);

    void refreshTargetProposal(String name, Long proposalId, SortDirection sortDirection);

    void selectDishInSlot(Principal principal, Long proposalId, Long slotId, Long dishId);

    void clearDishFromSlot(Principal principal, Long proposalId, Long slotId, Long dishId);

    void showMoreProposalSlotOptions(String name, Long proposalId, Long slotId);

    TargetProposalEntity createTargetProposal(String name, Long targetId);
}
