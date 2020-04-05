package com.meg.listshop.lmt.service.proposal.impl;

import com.meg.listshop.lmt.service.proposal.ProposalProcessor;
import com.meg.listshop.lmt.service.proposal.ProposalProcessorFactory;
import com.meg.listshop.lmt.service.proposal.ProposalRequest;
import com.meg.listshop.lmt.service.proposal.ProposalSearchType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by margaretmartin on 24/05/2018.
 */
@Component
public class ProposalProcessorFactoryImpl implements ProposalProcessorFactory {

    @Autowired
    @Qualifier(value = "newSearch")
    ProposalProcessor newSearchProcessor;

    @Autowired
    @Qualifier(value = "refreshSearch")
    ProposalProcessor refreshSearchProcessor;

    @Autowired
    @Qualifier(value = "fillInSearch")
    ProposalProcessor fillInSearchProcessor;

    @Override
    public ProposalProcessor getProposalProcessor(ProposalRequest request) {
        if (request.getSearchType().equals(ProposalSearchType.FillInSearch)) {
            return fillInSearchProcessor;
        } else if (request.getSearchType().equals(ProposalSearchType.RefreshSearch)) {
            return refreshSearchProcessor;
        } else {
            return newSearchProcessor;
        }
    }
}
