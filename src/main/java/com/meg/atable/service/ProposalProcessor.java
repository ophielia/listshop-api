package com.meg.atable.service;

import com.meg.atable.api.exception.ProposalProcessingException;
import com.meg.atable.service.impl.ProposalRequest;

/**
 * Created by margaretmartin on 24/05/2018.
 */
public interface ProposalProcessor {
    ProcessResult processProposal(ProposalRequest request) throws ProposalProcessingException;
}
