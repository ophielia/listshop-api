package com.meg.atable.service;

import com.meg.atable.api.model.GenerateType;
import com.meg.atable.api.model.ListType;
import com.meg.atable.data.entity.ItemEntity;
import com.meg.atable.data.entity.ShoppingListEntity;
import com.meg.atable.data.entity.TargetEntity;
import com.meg.atable.data.entity.TargetProposalEntity;

import java.util.List;

/**
 * Created by margaretmartin on 30/10/2017.
 */
public interface TargetProposalService {

    TargetProposalEntity createTargetProposal(TargetEntity target);
}
