package com.meg.listshop.lmt.service.proposal;

/**
 * Created by margaretmartin on 24/05/2018.
 */
public interface ProposalProcessorFactory {
    ProposalProcessor getProposalProcessor(ProposalRequest request);
}
