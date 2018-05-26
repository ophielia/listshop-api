package com.meg.atable.data.repository;

import com.meg.atable.data.entity.ProposalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by margaretmartin on 22/05/2018.
 */
public interface ProposalRepository extends JpaRepository<ProposalEntity, Long> {

    ProposalEntity findProposalByUserIdAndId(Long userId, Long proposalId);

}
