package com.meg.atable.lmt.service.proposal;

import com.meg.atable.lmt.api.exception.ProposalProcessingException;

/**
 * Created by margaretmartin on 24/05/2018.
 */
public interface ProposalProcessor {
    ProcessResult processProposal(ProposalRequest request) throws ProposalProcessingException;
}
