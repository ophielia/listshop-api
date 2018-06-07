package com.meg.atable.service;

import com.meg.atable.api.exception.ObjectNotFoundException;
import com.meg.atable.api.exception.ObjectNotYoursException;
import com.meg.atable.api.exception.ProposalProcessingException;
import com.meg.atable.data.entity.ProposalEntity;

/**
 * Created by margaretmartin on 30/10/2017.
 */
public interface ProposalGeneratorService {

    ProposalEntity generateProposal(String userName, Long targetId) throws ObjectNotYoursException, ObjectNotFoundException;

    ProposalEntity refreshProposal(String userName, Long proposalId) throws ProposalProcessingException;

    ProposalEntity addToProposalSlot(String userName, Long proposalId, Integer slotNr) throws ProposalProcessingException;

    ProposalEntity proposalForMealPlan(String userName, Long mealPlanId, Long targetId) throws ProposalProcessingException;

    ProposalEntity fillInformationForProposal(String userName, ProposalEntity proposalEntity);

}
