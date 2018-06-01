package com.meg.atable.service;

import com.meg.atable.api.model.ApproachType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 01/01/2018.
 */
public class AttemptGenerator {

    private static final Integer[] baseList = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

    public static List<Integer[]> getProposalOrders(ApproachType approachType, int slotcount, int proposalcount) {

        return getProposalOrders(approachType,slotcount,proposalcount,null);
    }

    public static List<Integer[]> getProposalOrders(ApproachType approachType, int slotcount, int proposalcount, Map<Integer, Integer> indexToSlotNumber) {
        switch (approachType) {
            case WHEEL:
                return generateWheelApproaches(slotcount, proposalcount, indexToSlotNumber);
            case WHEEL_MIXED:
                return generateWheelApproaches_Mixed(slotcount, proposalcount, indexToSlotNumber);
            case SORTED_WHEEL:
                return generateSortedWheelApproaches(slotcount, proposalcount, indexToSlotNumber);
            case REV_SORTED_WHEEL:
                return generateReverseSortedWheelApproaches(slotcount, proposalcount,indexToSlotNumber);
        }
        return null;
    }

    private static List<Integer[]> generateReverseSortedWheelApproaches(int slotcount, int proposalcount, Map<Integer,Integer> indexToSlotNumber) {
        List<Integer[]> proposals = new ArrayList<>();
        for (int i = 0; i < proposalcount; i++) {
            Integer[] proposal = new Integer[slotcount];
            Integer lkup = baseList[i];
            proposal[0] =indexToSlotNumber.get(lkup);

            int k = 1;
            if (i > 0) {
                // add the elements before
                for (int j = i - 1; j >= 0; j--) {
                    Integer lkup2 = baseList[j];
                    proposal[k] = indexToSlotNumber.get(lkup2);
                    k++;
                }
            }
            if (i < slotcount) {
                // add the elements after
                for (int j = i + 1; j < slotcount; j++) {
                    Integer lkup3 = baseList[j];
                    proposal[k] = indexToSlotNumber.get(lkup3);
                    k++;
                }
            }
            proposals.add(proposal);

        }

        return proposals;
    }

    private static List<Integer[]> generateWheelApproaches_Mixed(int slotcount, int proposalcount, Map<Integer,Integer> indexToSlotNumber) {
        List<Integer[]> proposals = generateSortedWheelApproaches(slotcount, slotcount, indexToSlotNumber);
        List<String> keys = proposals.stream().map(i -> toKey(i)).collect(Collectors.toList());
        List<Integer[]> additionalproposals = generateReverseSortedWheelApproaches(slotcount, slotcount, indexToSlotNumber);
        for (int i = 1; i < additionalproposals.size() && proposals.size() < proposalcount; i++) {
            String newkey = toKey(additionalproposals.get(i));
            if (!keys.contains(newkey)) {
                proposals.add(additionalproposals.get(i));
                keys.add(newkey);
            }
        }
        return proposals;
    }

    private static String toKey(Integer[] arrayints) {
        StringBuffer key = new StringBuffer();
        Arrays.stream(arrayints).forEach(i -> key.append(i));
        return key.toString();
    }

    private static List<Integer[]> generateWheelApproaches(int slotcount, int proposalcount, Map<Integer,Integer> indexToSlotNumber) {
        List<Integer[]> proposals = new ArrayList<>();
        for (int i = 0; i < slotcount; i++) {
            Integer[] proposal = new Integer[slotcount];
            for (int j = i; j < i + slotcount; j++) {
                int srcidx = j < slotcount ? j : j - slotcount;
                Integer lookup = baseList[srcidx];
                proposal[j - i] = indexToSlotNumber.get(lookup);
            }
            proposals.add(proposal);

        }

        return proposals;
    }

    private static List<Integer[]> generateSortedWheelApproaches(int slotcount, int proposalcount, Map<Integer,Integer> indexToSlotNumber) {


        List<Integer[]> proposals = new ArrayList<>();
        for (int i = 0; i < proposalcount; i++) {
            Integer[] proposal = new Integer[slotcount];
            proposal[0] = baseList[i];
            int k = 1;
            for (int j = 0; j < slotcount; j++) {
                if (j == i) {
                    continue;
                }
                Integer lookup = baseList[j];
                proposal[k] = indexToSlotNumber.get(lookup);
                k++;
            }
            proposals.add(proposal);

        }

        return proposals;

    }


}
