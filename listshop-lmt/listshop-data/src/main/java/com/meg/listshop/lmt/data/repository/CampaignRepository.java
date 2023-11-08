package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.CampaignEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<CampaignEntity, Long> {


    CampaignEntity findByEmailAndCampaign(String email, String campaign);
}