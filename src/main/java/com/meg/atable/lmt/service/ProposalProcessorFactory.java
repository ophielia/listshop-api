package com.meg.atable.lmt.service;

import com.meg.atable.lmt.service.impl.ProposalRequest;

/**
 * Created by margaretmartin on 24/05/2018.
 */
public interface ProposalProcessorFactory {
    ProposalProcessor getProposalProcessor(ProposalRequest request);
}
