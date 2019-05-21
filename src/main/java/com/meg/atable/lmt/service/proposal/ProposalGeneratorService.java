package com.meg.atable.lmt.service.proposal;

import com.meg.atable.lmt.api.exception.ObjectNotFoundException;
import com.meg.atable.lmt.api.exception.ObjectNotYoursException;
import com.meg.atable.lmt.api.exception.ProposalProcessingException;
import com.meg.atable.lmt.data.entity.ProposalEntity;

/**
 * Created by margaretmartin on 30/10/2017.
 */
public interface ProposalGeneratorService {

    ProposalEntity generateProposal(String userName, Long targetId) throws ObjectNotYoursException, ObjectNotFoundException, ProposalProcessingException;

    ProposalEntity refreshProposal(String userName, Long proposalId) throws ProposalProcessingException;

    ProposalEntity addToProposalSlot(String userName, Long proposalId, Integer slotNr) throws ProposalProcessingException;

    ProposalEntity proposalForMealPlan(String userName, Long mealPlanId, Long targetId) throws ProposalProcessingException;

    ProposalEntity fillInformationForProposal(String userName, ProposalEntity proposalEntity);

}
