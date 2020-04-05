package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.Application;
import com.meg.listshop.lmt.api.model.ApproachType;
import com.meg.listshop.lmt.service.AttemptGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class AttemptGeneratorTest {


    @Test
    public void testGenerateWheel() throws Exception {
        Map<Integer, Integer> indexToSlotNumber = getDummyIndex();
        List<Integer[]> proposals = AttemptGenerator.getProposalOrders(ApproachType.WHEEL, 3, 3, indexToSlotNumber);

        Assert.assertEquals(3, proposals.size());
        Integer[] spot1 = {0, 1, 2};
        Integer[] spot2 = {1, 2, 0};
        Integer[] spot3 = {2, 0, 1};
        evaluate(spot1, proposals.get(0));
        evaluate(spot2, proposals.get(1));
        evaluate(spot3, proposals.get(2));
    }

    private Map<Integer, Integer> getDummyIndex() {
        Map<Integer, Integer> indexToSlotNumber = new HashMap<>();
        indexToSlotNumber.put(0,0);
        indexToSlotNumber.put(1,1);
        indexToSlotNumber.put(2,2);
        indexToSlotNumber.put(3,3);
        indexToSlotNumber.put(4,4);
        return indexToSlotNumber;
    }

    @Test
    public void testGenerateWheel_Five() throws Exception {
        Map<Integer, Integer> indexToSlotNumber = getDummyIndex();
        List<Integer[]> proposals = AttemptGenerator.getProposalOrders(ApproachType.WHEEL, 5, 5, indexToSlotNumber);

        Assert.assertEquals(5, proposals.size());
        Integer[] spot1 = {0, 1, 2, 3, 4};
        Integer[] spot2 = {1, 2, 3, 4, 0};
        Integer[] spot3 = {2, 3, 4, 0, 1};
        Integer[] spot4 = {3, 4, 0, 1, 2};
        Integer[] spot5 = {4, 0, 1, 2, 3,};
        evaluate(spot1, proposals.get(0));
        evaluate(spot2, proposals.get(1));
        evaluate(spot3, proposals.get(2));
        evaluate(spot4, proposals.get(3));
        evaluate(spot5, proposals.get(4));
    }

    @Test
    public void testGenerateWheelSorted() throws Exception {
        Map<Integer, Integer> indexToSlotNumber = getDummyIndex();
        List<Integer[]> proposals = AttemptGenerator.getProposalOrders(ApproachType.SORTED_WHEEL, 3, 3, indexToSlotNumber);

        Assert.assertEquals(3, proposals.size());
        Integer[] spot1 = {0, 1, 2};
        Integer[] spot2 = {1, 0, 2};
        Integer[] spot3 = {2, 0, 1};
        evaluate(spot1, proposals.get(0));
        evaluate(spot2, proposals.get(1));
        evaluate(spot3, proposals.get(2));
    }

    @Test
    public void testGenerateWheelSorted_Five() throws Exception {
        Map<Integer, Integer> indexToSlotNumber = getDummyIndex();
        List<Integer[]> proposals = AttemptGenerator.getProposalOrders(ApproachType.SORTED_WHEEL, 5, 5, indexToSlotNumber);

        Assert.assertEquals(5, proposals.size());
        Integer[] spot1 = {0, 1, 2, 3, 4};
        Integer[] spot2 = {1, 0, 2, 3, 4};
        Integer[] spot3 = {2, 0, 1, 3, 4};
        Integer[] spot4 = {3, 0, 1, 2, 4};
        Integer[] spot5 = {4, 0, 1, 2, 3};
        evaluate(spot1, proposals.get(0));
        evaluate(spot2, proposals.get(1));
        evaluate(spot3, proposals.get(2));
        evaluate(spot4, proposals.get(3));
        evaluate(spot5, proposals.get(4));
    }

    @Test
    public void testGenerateWheelReverseSorted() throws Exception {
        Map<Integer, Integer> indexToSlotNumber = getDummyIndex();
        List<Integer[]> proposals = AttemptGenerator.getProposalOrders(ApproachType.REV_SORTED_WHEEL, 3, 3, indexToSlotNumber);

        Assert.assertEquals(3, proposals.size());
        Integer[] spot1 = {0, 1, 2};
        Integer[] spot2 = {1, 0, 2};
        Integer[] spot3 = {2, 1, 0};
        evaluate(spot1, proposals.get(0));
        evaluate(spot2, proposals.get(1));
        evaluate(spot3, proposals.get(2));
    }

    @Test
    public void testGenerateReverseWheelSorted_Five() throws Exception {
        Map<Integer, Integer> indexToSlotNumber = getDummyIndex();
        List<Integer[]> proposals = AttemptGenerator.getProposalOrders(ApproachType.REV_SORTED_WHEEL, 5, 5, indexToSlotNumber);

        Assert.assertEquals(5, proposals.size());
        Integer[] spot1 = {0, 1, 2, 3, 4};
        Integer[] spot2 = {1, 0, 2, 3, 4};
        Integer[] spot3 = {2, 1, 0, 3, 4};
        Integer[] spot4 = {3, 2, 1, 0, 4};
        Integer[] spot5 = {4, 3, 2, 1, 0};
        evaluate(spot1, proposals.get(0));
        evaluate(spot2, proposals.get(1));
        evaluate(spot3, proposals.get(2));
        evaluate(spot4, proposals.get(3));
        evaluate(spot5, proposals.get(4));
    }

    @Test
    public void testGenerateWheel_MixedFive() throws Exception {
        Map<Integer, Integer> indexToSlotNumber = getDummyIndex();
        List<Integer[]> proposals = AttemptGenerator.getProposalOrders(ApproachType.WHEEL_MIXED, 5, 6, indexToSlotNumber);

        Assert.assertEquals(6, proposals.size());
        Integer[] spot1 = {0, 1, 2, 3, 4};
        Integer[] spot2 = {1, 0, 2, 3, 4};
        Integer[] spot3 = {2, 0, 1, 3, 4};
        Integer[] spot4 = {3, 0, 1, 2, 4};
        Integer[] spot5 = {4, 0, 1, 2, 3};
        Integer[] spot6 = {2, 1, 0, 3, 4};
        evaluate(spot1, proposals.get(0));
        evaluate(spot2, proposals.get(1));
        evaluate(spot3, proposals.get(2));
        evaluate(spot4, proposals.get(3));
        evaluate(spot5, proposals.get(4));
        evaluate(spot6, proposals.get(5));
    }

    private void evaluate(Integer[] spot1, Integer[] toTest) {
        for (int i = 0; i < spot1.length; i++) {
            Assert.assertEquals(spot1[i], toTest[i]);
        }

    }


}