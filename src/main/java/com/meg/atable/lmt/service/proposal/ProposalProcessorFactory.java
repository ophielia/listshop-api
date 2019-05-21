package com.meg.atable.lmt.service.proposal;

/**
 * Created by margaretmartin on 24/05/2018.
 */
public interface ProposalProcessorFactory {
    ProposalProcessor getProposalProcessor(ProposalRequest request);
}
