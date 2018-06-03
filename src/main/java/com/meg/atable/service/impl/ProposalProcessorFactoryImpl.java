package com.meg.atable.service.impl;

import com.meg.atable.service.ProposalProcessor;
import com.meg.atable.service.ProposalProcessorFactory;
import com.meg.atable.service.ProposalSearchType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by margaretmartin on 24/05/2018.
 */
@Component
public class ProposalProcessorFactoryImpl implements ProposalProcessorFactory {

    @Autowired
    @Qualifier(value="newSearch")
    ProposalProcessor newSearchProcessor;

    @Autowired
    @Qualifier(value="refreshSearch")
    ProposalProcessor refreshSearchProcessor;

    @Override
    public ProposalProcessor getProposalProcessor(ProposalRequest request) {
        if (request.getSearchType().equals(ProposalSearchType.FillInSearch) ||
                request.getSearchType().equals(ProposalSearchType.RefreshSearch)) {

            return refreshSearchProcessor;
        } else {
            return newSearchProcessor;
        }
    }
}
