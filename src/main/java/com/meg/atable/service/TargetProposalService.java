package com.meg.atable.service;

import com.meg.atable.api.model.GenerateType;
import com.meg.atable.api.model.ListType;
import com.meg.atable.data.entity.ItemEntity;
import com.meg.atable.data.entity.ShoppingListEntity;
import com.meg.atable.data.entity.TargetEntity;
import com.meg.atable.data.entity.TargetProposalEntity;

import java.security.Principal;
import java.util.List;

/**
 * Created by margaretmartin on 30/10/2017.
 */
public interface TargetProposalService {

    TargetProposalEntity getTargetProposalById(String name, Long proposalId);

    TargetProposalEntity fillInformationForProposal(TargetProposalEntity proposalEntity);

    void refreshTargetProposal(String name, Long proposalId);

    void selectDishInSlot(Principal principal, Long proposalId, Long slotId, Long dishId);

    void clearDishFromSlot(Principal principal, Long proposalId, Long slotId, Long dishId);

    void refreshTargetProposalSlot(String name, Long proposalId, Long slotId);

    TargetProposalEntity createTargetProposal(String name, Long targetId);
}
