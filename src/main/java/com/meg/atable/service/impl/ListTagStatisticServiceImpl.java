package com.meg.atable.service.impl;

import com.meg.atable.api.model.ListType;
import com.meg.atable.data.entity.ListTagStatistic;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.repository.ListTagStatisticRepository;
import com.meg.atable.service.ListItemCollector;
import com.meg.atable.service.ListTagStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Service
public class ListTagStatisticServiceImpl implements ListTagStatisticService {


    @Autowired
    private ListTagStatisticRepository listTagStatisticRepo;

    @Override
    public void itemAddedToList(Long userId, Long tagId, ListType listType) {
        addOrRemoveItem(userId, tagId, listType, true);
    }

    @Override
    public void itemRemovedFromList(Long userId, Long tagId, ListType listType) {
        addOrRemoveItem(userId, tagId, listType, false);
    }

    @Override
    public void processStatistics(Long userId, ListItemCollector collector) {
        // get statistics for tags - hash by tagid
        Map<Long, ListTagStatistic> statLkup = listTagStatisticRepo.findByUserIdAndTagIdIn(userId, collector.getAllTagIds()).stream()
                .collect(Collectors.toMap(lts -> lts.getTagId(), lts -> lts));

        // go through list tags - return list of stats
        List<ListTagStatistic> toUpdate = collector.getItems().stream()
                .filter(item -> item.getTag() != null)
                .map(item -> {
                    TagEntity tag = item.getTag();
                    if (statLkup.containsKey(tag.getId())) {
                        // check frequency
                        if (checkFrequency(statLkup.get(tag.getId()))) {
                            item.setFrequent(true);
                        }
                        // update statistic
                        return addCounted(statLkup.get(tag.getId()));
                    } else {
                        // create new stat
                        ListTagStatistic newstat = new ListTagStatistic();
                        newstat.setUserId(userId);
                        newstat.setTagId(item.getTag().getId());
                        newstat.setAddedCount(0);
                        newstat.setRemovedCount(0);
                        newstat = addCounted(newstat);
                        return newstat;
                    }
                }).collect(Collectors.toList());

        // save list of stats
        listTagStatisticRepo.save(toUpdate);
    }

    private ListTagStatistic addCounted(ListTagStatistic statistic) {
        Integer counted = statistic.getAddedCount() == null ? 0 : statistic.getAddedCount();
        statistic.setAddedCount(counted + 1);
        return statistic;
    }

    private ListTagStatistic addRemoved(ListTagStatistic stat) {
        Integer counted = stat.getRemovedCount() == null ? 0 : stat.getRemovedCount();
        stat.setRemovedCount(counted + 1);
        return stat;
    }

    private boolean checkFrequency(ListTagStatistic listTagStatistic) {
        // return less than 3
        if (listTagStatistic.getAddedCount() < 3) {
            return false;
        }
        Integer addedCount = listTagStatistic.getAddedCount();
        Double percentage = (listTagStatistic.getRemovedCount().doubleValue() / addedCount.doubleValue()) * 100.0;
        // process 3 - 5
        if (addedCount <= 5) {
            return percentage >= 65.0;
        } else if (addedCount <= 10) {
            return percentage >= 75.0;
        }
        return percentage >= 70.0;

    }

    private void addOrRemoveItem(Long userId, Long tagId, ListType listType, boolean isAdd) {
        if (tagId == null || listType == ListType.PickUpList) {
            return;
        }
        // get statistic for tag
        ListTagStatistic statistic = listTagStatisticRepo.findByUserIdAndTagId(userId, tagId);
        // if it doesn't exist, create it
        if (statistic == null) {
            statistic = new ListTagStatistic();
            statistic.setUserId(userId);
            statistic.setTagId(tagId);
            statistic.setAddedCount(0);
            statistic.setRemovedCount(0);
        }
        // increment added or removed
        if (isAdd) {
            statistic = addCounted(statistic);
        } else {
            statistic = addRemoved(statistic);
        }
        // save statistic
        listTagStatisticRepo.save(statistic);
    }

}
