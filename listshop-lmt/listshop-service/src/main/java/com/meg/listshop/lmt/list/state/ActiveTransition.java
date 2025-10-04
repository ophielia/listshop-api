package com.meg.listshop.lmt.list.state;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.data.entity.DishItemEntity;
import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.repository.ListItemDetailRepository;
import com.meg.listshop.lmt.data.repository.ListItemRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Qualifier("activeTransition")
@Transactional
public class ActiveTransition extends AbstractTransition {

    private static final String MATCH_SEPARATOR = "*";
    private static final Logger log = LoggerFactory.getLogger(ActiveTransition.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ActiveTransition(ListItemRepository listItemRepository, ListItemDetailRepository listItemDetailRepository) {
        super(listItemRepository, listItemDetailRepository);
    }

    private static String createMatchingTag(ListItemDetailEntity listItemDetailEntity) {
        return String.format("%s%s%s", listItemDetailEntity.getLinkedDishId(), MATCH_SEPARATOR,
                listItemDetailEntity.getLinkedListId());
    }

    public ListItemEntity transitionToState(ListItemEvent listItemEvent, @NotNull ItemStateContext itemStateContext) throws ItemProcessingException {
        ListItemEntity item = getOrCreateItem(itemStateContext);

        // clear old states
        item.setRemovedOn(null);
        item.setCrossedOff(null);

        // add details
        try {
            addItemDetails(item, itemStateContext);
        } catch (JsonProcessingException e) {
            log.error("Unable to add item details for list id [{}]", itemStateContext.getTargetListId());
            throw new ItemProcessingException("Unable to add item details for list [" + itemStateContext.getTargetListId() + "]", e);
        }

        // set update date
        item.setUpdatedOn(new Date());
        listItemRepository.save(item);
        return item;
    }

    private void addItemDetails(ListItemEntity item, @NotNull ItemStateContext context) throws JsonProcessingException {
        // get detail candidates
        List<ListItemDetailEntity> candidates = createDetailMatchingCandidates(context);

        // make matching matrix
        Map<String, ListItemDetailEntity> existingMap = item.getDetails().stream()
                .collect(Collectors.toMap(ActiveTransition::createMatchingTag, Function.identity()));

        // match candidates
        for (ListItemDetailEntity candidate : candidates) {
            String matchTag = createMatchingTag(candidate);
            if (existingMap.containsKey(matchTag) &&
                    !quantityMismatch(existingMap.get(matchTag), candidate)) {

                ListItemDetailEntity existing = existingMap.get(matchTag);
                var newCount = existing.getCount() + 1;
                existing.setCount(newCount);
            } else {
                // add candidate to list
                candidate.setItem(item);
                item.addDetailToItem(listItemDetailRepository.save(candidate));
            }
        }
    }

    private boolean quantityMismatch(ListItemDetailEntity existing, ListItemDetailEntity candidate) {
        boolean existingHasQuantity = existing.getQuantity() != null;
        boolean candidateHasQuantity = candidate.getQuantity() != null;
        return existingHasQuantity != candidateHasQuantity;
    }

    private List<ListItemDetailEntity> createDetailMatchingCandidates(@NotNull ItemStateContext context) throws JsonProcessingException {

        List<ListItemDetailEntity> candidates = new ArrayList<>();

        if (context.getListItem() != null) {
            // adding a list item - need to add each detail
            for (ListItemDetailEntity toCopy : context.getListItem().getDetails()) {
                ListItemDetailEntity candidate = copyItemDetail(toCopy, context.getListId());
                candidate.setLinkedListId(context.getListId());
                candidate.setCount(1);
                candidates.add(candidate);
            }
            return candidates;
        }
        ListItemDetailEntity newDetail = new ListItemDetailEntity();
        newDetail.setLinkedListId(context.getTargetListId());
        newDetail.setLinkedDishId(context.getDishId());
        newDetail.setCount(1);
        DishItemEntity dishItem = context.getDishItem();
        if (dishItem != null) {
            newDetail.setOriginalFractionalQuantity(dishItem.getFractionalQuantity());
            newDetail.setOriginalQuantity(dishItem.getQuantity());
            newDetail.setOriginalUnitId(dishItem.getUnitId());
            newDetail.setOriginalWholeQuantity(dishItem.getWholeQuantity());
            newDetail.setMarker(dishItem.getMarker());
            newDetail.setUnitSize(dishItem.getUnitSize());
            newDetail.setRawEntry(dishItem.getRawEntry());
        }
        candidates.add(newDetail);

        return candidates;


    }

    private ListItemDetailEntity copyItemDetail(ListItemDetailEntity toCopy, Long listId) throws JsonProcessingException {
        String detailJson = objectMapper.writeValueAsString(toCopy);
        ListItemDetailEntity detail = objectMapper.readValue(detailJson, ListItemDetailEntity.class);
        detail.setItemDetailId(null);
        detail.setLinkedListId(listId);
        return detail;
    }

}
