package com.meg.listshop.lmt.data.repository;


import com.meg.listshop.lmt.data.entity.ProposalContextEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposalContextRepository extends JpaRepository<ProposalContextEntity, Long> {


    ProposalContextEntity findByProposalId(Long proposalId);

    ProposalContextEntity findByTargetId(Long targetId);



}