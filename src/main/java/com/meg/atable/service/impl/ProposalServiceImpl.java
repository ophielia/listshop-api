package com.meg.atable.service.impl;

import com.meg.atable.api.exception.ObjectNotFoundException;
import com.meg.atable.api.exception.ObjectNotYoursException;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.DishSlotEntity;
import com.meg.atable.data.entity.ProposalEntity;
import com.meg.atable.data.entity.ProposalSlotEntity;
import com.meg.atable.data.repository.ProposalRepository;
import com.meg.atable.service.ProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
@Transactional
public class ProposalServiceImpl implements ProposalService {


    @Autowired
    private UserService userService;

    @Autowired
    private ProposalRepository targetProposalRepository;

    @Autowired
    private ProposalRepository proposalRepository;

    @Override
    public ProposalEntity getProposalById(String name, Long proposalId) {
        UserAccountEntity user = userService.getUserByUserName(name);
        Optional<ProposalEntity> proposalOpt = proposalRepository.findById(proposalId);
        if (!proposalOpt.isPresent()) {
            final String msg = "No proposal found by id for user [" + name + "] and proposal [" + proposalId + "]";
            throw new ObjectNotFoundException(msg, proposalId, "Proposal");
        }
        ProposalEntity proposal = proposalOpt.get();
        if (proposal.getUserId() == null || !proposal.getUserId().equals(user.getId())) {
            final String msg = "Proposal found, but doesn't belong to user [" + name + "] proposalId [" + proposalId + "]";
            throw new ObjectNotYoursException(msg, "Proposal", proposalId, user.getUsername());
        }

        proposal.getSlots().sort(Comparator.comparing(ProposalSlotEntity::getSlotNumber));
        return proposal;
    }

    @Override
    public ProposalEntity getTargetProposalById(String name, Long proposalId) {
        UserAccountEntity user = userService.getUserByUserName(name);
        Optional<ProposalEntity> proposalOpt = targetProposalRepository.findById(proposalId);
        if (!proposalOpt.isPresent()) {
            return null;
        }
        ProposalEntity proposal = proposalOpt.get();
        if (proposal.getUserId() == null || !proposal.getUserId().equals(user.getId())) {
            return null;
        }

        proposal.getSlots().sort(Comparator.comparing(ProposalSlotEntity::getSlotNumber));
        return proposal;
    }

    @Override
    public void selectDishInSlot(String userName, Long proposalId, Long slotId, Long dishId) {
        ProposalEntity proposalEntity = getTargetProposalById(userName, proposalId);
        if (proposalEntity == null ||
                proposalEntity.getSlots() == null) {
            return;
        }
        ProposalSlotEntity slotEntity = getSlotFromProposal(proposalEntity, slotId);

        if (slotEntity == null) {
            return;
        }

        List<DishSlotEntity> dishes = slotEntity.getDishSlots();
        if (dishes == null) {
            return;
        }
        long dishidcheck = dishId.longValue();
        slotEntity.setPickedDishId(dishId);

        targetProposalRepository.save(proposalEntity);
    }

    private ProposalSlotEntity getSlotFromProposal(ProposalEntity proposalEntity, Long slotId) {
        if (proposalEntity == null ||
                proposalEntity.getSlots() == null) {
            return null;
        }
        long slotidcheck = slotId.longValue();

        return proposalEntity.getSlots().stream()
                .filter(s -> s.getId().longValue() == slotidcheck)
                .findFirst().get();
    }

    @Override
    public void clearDishFromSlot(Principal principal, Long proposalId, Long slotId, Long dishId) {
        ProposalEntity proposalEntity = getProposalById(principal.getName(), proposalId);
        if (proposalEntity == null ||
                proposalEntity.getSlots() == null) {
            return;
        }
        ProposalSlotEntity slotEntity = getSlotFromProposal(proposalEntity, slotId);

        if (slotEntity == null) {
            return;
        }
        slotEntity.setPickedDishId(null);
        targetProposalRepository.save(proposalEntity);
    }

}
