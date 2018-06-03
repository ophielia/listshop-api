package com.meg.atable.service.impl;

import com.meg.atable.service.ProcessResult;
import com.meg.atable.service.ProposalProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by margaretmartin on 24/05/2018.
 */
@Component
@Qualifier("refreshSearch")
public class RefreshProposalProcessorImpl implements ProposalProcessor {
    @Override
    public ProcessResult processProposal(ProposalRequest request) {
        return null;
    }
}
