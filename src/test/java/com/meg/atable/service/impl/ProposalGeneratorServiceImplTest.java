package com.meg.atable.service.impl;

import com.meg.atable.Application;
import com.meg.atable.data.entity.ContextApproachEntity;
import com.meg.atable.data.entity.ProposalEntity;
import com.meg.atable.data.entity.TargetEntity;
import com.meg.atable.data.entity.TargetSlotEntity;
import com.meg.atable.service.*;
import com.meg.atable.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * Created by margaretmartin on 03/06/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test")
@Transactional
public class ProposalGeneratorServiceImplTest {

    @MockBean
    @Qualifier(value="newSearch")
    ProposalProcessor newSearchProcessor;

    @Autowired
    ProposalGeneratorService proposalGeneratorServiceImpl;

    @Autowired
    private TargetProposalService proposalService;

    @Autowired
    private TargetService targetService;


    @Before
    public void setUp() {

    }

    @Test
    public void generateProposal() throws Exception {
        // make / save base target
        TargetEntity targetEntity = buildBaseTestTarget();
        // make test fixtures
        ProposalRequest request = new ProposalRequest();
        request.setSearchType(ProposalSearchType.NewSearch);
        ProcessResult processResult = new ProcessResult(new ArrayList< ContextApproachEntity >());

        Mockito.when(newSearchProcessor.processProposal(request)).thenReturn(processResult);


        // test call
        ProposalEntity proposalEntity = proposalGeneratorServiceImpl.generateProposal(TestConstants.USER_1_NAME, targetEntity.getTargetId());

        // retrieve result
        Assert.assertNotNull(proposalEntity);
        ProposalEntity result = proposalService.getProposalById(TestConstants.USER_1_NAME, proposalEntity.getId());

        // result exists
        // result has same number of slots as target
        // slots contain 5 dishes each

        // has approaches and is refreshable
    }

    private TargetEntity buildBaseTestTarget() {
        TargetEntity target = targetService.createTarget(TestConstants.USER_1_NAME,new TargetEntity());

        TargetSlotEntity slot1 = new TargetSlotEntity();
        TargetSlotEntity slot2 = new TargetSlotEntity();
        TargetSlotEntity slot3 = new TargetSlotEntity();
        TargetSlotEntity slot4 = new TargetSlotEntity();
        slot1.setSlotDishTagId(TestConstants.TAG_MAIN_DISH);
        slot2.setSlotDishTagId(TestConstants.TAG_MAIN_DISH);
        slot3.setSlotDishTagId(TestConstants.TAG_SIDE_DISH);
        slot4.setSlotDishTagId(TestConstants.TAG_MAIN_DISH);


        targetService.addSlotToTarget(TestConstants.USER_1_NAME,target.getTargetId(),slot1);
        targetService.addSlotToTarget(TestConstants.USER_1_NAME,target.getTargetId(),slot2);
        targetService.addSlotToTarget(TestConstants.USER_1_NAME,target.getTargetId(),slot3);
        targetService.addSlotToTarget(TestConstants.USER_1_NAME,target.getTargetId(),slot4);

        // now, add tags
        targetService.addTagToTarget(TestConstants.USER_1_NAME,target.getTargetId(),TestConstants.TAG_YUMMY);
        targetService.addTagToTarget(TestConstants.USER_1_NAME,target.getTargetId(),TestConstants.TAG_CARROTS);

        target = targetService.getTargetById(TestConstants.USER_1_NAME,target.getTargetId());
        TargetSlotEntity slot = target.getSlots().get(0);
        targetService.addTagToTargetSlot(TestConstants.USER_1_NAME,target.getTargetId(),slot.getId(),TestConstants.TAG_CROCKPOT);
         slot = target.getSlots().get(1);
        targetService.addTagToTargetSlot(TestConstants.USER_1_NAME,target.getTargetId(),slot.getId(),TestConstants.TAG_SOUP);
         slot = target.getSlots().get(2);
        targetService.addTagToTargetSlot(TestConstants.USER_1_NAME,target.getTargetId(),slot.getId(),TestConstants.TAG_MEAT);
         slot = target.getSlots().get(3);
        targetService.addTagToTargetSlot(TestConstants.USER_1_NAME,target.getTargetId(),slot.getId(),TestConstants.TAG_PASTA);


        return  targetService.getTargetById(TestConstants.USER_1_NAME,target.getTargetId());

    }

}