package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.lmt.api.model.ApproachType;
import com.meg.listshop.lmt.service.AttemptGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class AttemptGeneratorTest {

    @Test
    void testGenerateWheel() {
        Map<Integer, Integer> indexToSlotNumber = getDummyIndex();
        List<Integer[]> proposals = AttemptGenerator.getProposalOrders(ApproachType.WHEEL, 3, 3, indexToSlotNumber);

        Assertions.assertEquals(3, proposals.size());
        Integer[] spot1 = {0, 1, 2};
        Integer[] spot2 = {1, 2, 0};
        Integer[] spot3 = {2, 0, 1};
        evaluate(spot1, proposals.get(0));
        evaluate(spot2, proposals.get(1));
        evaluate(spot3, proposals.get(2));
    }

    private Map<Integer, Integer> getDummyIndex() {
        Map<Integer, Integer> indexToSlotNumber = new HashMap<>();
        indexToSlotNumber.put(0, 0);
        indexToSlotNumber.put(1, 1);
        indexToSlotNumber.put(2, 2);
        indexToSlotNumber.put(3, 3);
        indexToSlotNumber.put(4, 4);
        return indexToSlotNumber;
    }

    @Test
    void testGenerateWheel_Five() {
        Map<Integer, Integer> indexToSlotNumber = getDummyIndex();
        List<Integer[]> proposals = AttemptGenerator.getProposalOrders(ApproachType.WHEEL, 5, 5, indexToSlotNumber);

        Assertions.assertEquals(5, proposals.size());
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
    void testGenerateWheelSorted() {
        Map<Integer, Integer> indexToSlotNumber = getDummyIndex();
        List<Integer[]> proposals = AttemptGenerator.getProposalOrders(ApproachType.SORTED_WHEEL, 3, 3, indexToSlotNumber);

        Assertions.assertEquals(3, proposals.size());
        Integer[] spot1 = {0, 1, 2};
        Integer[] spot2 = {1, 0, 2};
        Integer[] spot3 = {2, 0, 1};
        evaluate(spot1, proposals.get(0));
        evaluate(spot2, proposals.get(1));
        evaluate(spot3, proposals.get(2));
    }

    @Test
    void testGenerateWheelSorted_Five() {
        Map<Integer, Integer> indexToSlotNumber = getDummyIndex();
        List<Integer[]> proposals = AttemptGenerator.getProposalOrders(ApproachType.SORTED_WHEEL, 5, 5, indexToSlotNumber);

        Assertions.assertEquals(5, proposals.size());
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
    void testGenerateWheelReverseSorted() {
        Map<Integer, Integer> indexToSlotNumber = getDummyIndex();
        List<Integer[]> proposals = AttemptGenerator.getProposalOrders(ApproachType.REV_SORTED_WHEEL, 3, 3, indexToSlotNumber);

        Assertions.assertEquals(3, proposals.size());
        Integer[] spot1 = {0, 1, 2};
        Integer[] spot2 = {1, 0, 2};
        Integer[] spot3 = {2, 1, 0};
        evaluate(spot1, proposals.get(0));
        evaluate(spot2, proposals.get(1));
        evaluate(spot3, proposals.get(2));
    }

    @Test
    void testGenerateReverseWheelSorted_Five() {
        Map<Integer, Integer> indexToSlotNumber = getDummyIndex();
        List<Integer[]> proposals = AttemptGenerator.getProposalOrders(ApproachType.REV_SORTED_WHEEL, 5, 5, indexToSlotNumber);

        Assertions.assertEquals(5, proposals.size());
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
    void testGenerateWheel_MixedFive() {
        Map<Integer, Integer> indexToSlotNumber = getDummyIndex();
        List<Integer[]> proposals = AttemptGenerator.getProposalOrders(ApproachType.WHEEL_MIXED, 5, 6, indexToSlotNumber);

        Assertions.assertEquals(6, proposals.size());
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
            Assertions.assertEquals(spot1[i], toTest[i]);
        }

    }


}
