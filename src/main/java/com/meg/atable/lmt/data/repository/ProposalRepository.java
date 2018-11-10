package com.meg.atable.lmt.data.repository;

import com.meg.atable.lmt.data.entity.ProposalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by margaretmartin on 22/05/2018.
 */
public interface ProposalRepository extends JpaRepository<ProposalEntity, Long> {

    ProposalEntity findProposalByUserIdAndProposalId(Long userId, Long proposalId);

}
