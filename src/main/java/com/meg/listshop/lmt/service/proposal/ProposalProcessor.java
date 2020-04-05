package com.meg.listshop.lmt.service.proposal;

import com.meg.listshop.lmt.api.exception.ProposalProcessingException;

/**
 * Created by margaretmartin on 24/05/2018.
 */
public interface ProposalProcessor {
    ProcessResult processProposal(ProposalRequest request) throws ProposalProcessingException;
}
