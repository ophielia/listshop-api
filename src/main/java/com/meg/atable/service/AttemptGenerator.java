package com.meg.atable.service;

import com.meg.atable.api.model.ApproachType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 01/01/2018.
 */
public class AttemptGenerator {

    private static final Integer[] baseList = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};


    public static List<Integer[]> getProposalOrders(ApproachType approachType, int slotcount, int proposalcount) {
        switch (approachType) {
            case WHEEL:
                return generateWheelApproaches(slotcount, proposalcount);
            case WHEEL_MIXED:
                return generateWheelApproaches_Mixed(slotcount, proposalcount);
            case SORTED_WHEEL:
                return generateSortedWheelApproaches(slotcount, proposalcount);
            case REV_SORTED_WHEEL:
                return generateReverseSortedWheelApproaches(slotcount, proposalcount);
        }
        return null;
    }

    private static List<Integer[]> generateReverseSortedWheelApproaches(int slotcount, int proposalcount) {
        List<Integer[]> proposals = new ArrayList<>();
        for (int i = 0; i < proposalcount; i++) {
            Integer[] proposal = new Integer[slotcount];
            proposal[0] = baseList[i];

            int k = 1;
            if (i > 0) {
                // add the elements before
                for (int j = i - 1; j >= 0; j--) {
                    proposal[k] = baseList[j];
                    k++;
                }
            }
            if (i < slotcount) {
                // add the elements after
                for (int j = i + 1; j < slotcount; j++) {
                    proposal[k] = baseList[j];
                    k++;
                }
            }
            proposals.add(proposal);

        }

        return proposals;
    }

    private static List<Integer[]> generateWheelApproaches_Mixed(int slotcount, int proposalcount) {
        List<Integer[]> proposals = generateSortedWheelApproaches(slotcount, slotcount);
        List<String> keys = proposals.stream().map(i -> toKey(i)).collect(Collectors.toList());
        List<Integer[]> additionalproposals = generateReverseSortedWheelApproaches(slotcount, slotcount);
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

    private static List<Integer[]> generateWheelApproaches(int slotcount, int proposalcount) {
        List<Integer[]> proposals = new ArrayList<>();
        for (int i = 0; i < slotcount; i++) {
            Integer[] proposal = new Integer[slotcount];
            for (int j = i; j < i + slotcount; j++) {
                int srcidx = j < slotcount ? j : j - slotcount;
                proposal[j - i] = baseList[srcidx];
            }
            proposals.add(proposal);

        }

        return proposals;
    }

    private static List<Integer[]> generateSortedWheelApproaches(int slotcount, int proposalcount) {


        List<Integer[]> proposals = new ArrayList<>();
        for (int i = 0; i < proposalcount; i++) {
            Integer[] proposal = new Integer[slotcount];
            proposal[0] = baseList[i];
            int k = 1;
            for (int j = 0; j < slotcount; j++) {
                if (j == i) {
                    continue;
                }

                proposal[k] = baseList[j];
                k++;
            }
            proposals.add(proposal);

        }

        return proposals;

    }


}
