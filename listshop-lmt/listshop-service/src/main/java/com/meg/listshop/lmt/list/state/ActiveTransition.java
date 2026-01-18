package com.meg.listshop.lmt.list.state;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.ConverterService;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.conversion.ListConversionService;
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

    private final ListConversionService conversionService;

    public ActiveTransition(ListItemRepository listItemRepository,
                            ListItemDetailRepository listItemDetailRepository,
                            ListConversionService conversionService) {
        super(listItemRepository, listItemDetailRepository);

        this.conversionService = conversionService;
    }

    private static String createMatchingTag(ListItemDetailEntity listItemDetailEntity) {
        return String.format("%s%s%s", listItemDetailEntity.getLinkedDishId(), MATCH_SEPARATOR,
                listItemDetailEntity.getLinkedListId());
    }

    public ListItemEntity legacyTransitionToState(ListItemEvent listItemEvent, @NotNull ItemStateContext itemStateContext) throws ItemProcessingException {
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

    public ListItemEntity transitionToState(ListItemEvent listItemEvent, @NotNull ItemStateContext itemStateContext) throws ItemProcessingException {
        ListItemEntity item = getOrCreateItem(itemStateContext);

        // clear old states
        item.setRemovedOn(null);
        item.setCrossedOff(null);

        // fill item for each type
        ProcessingType processingType = determineProcessingType(itemStateContext);
        switch (processingType) {
            case DISH -> processAddDishItem(item, itemStateContext);
            case LIST -> processAddListItem(item, itemStateContext);
            case SIMPLE_ITEM -> processAddSimpleItem(item, itemStateContext);
            default -> throw new ItemProcessingException("Unexpected processing type");
            //TODO java 21 -  test for null here
        }
        return item;
    }

    /*
    Processes an addition of a dish item - resulting in a single added/updated item detail in the passed item.
    Result is scaled, summed and saved.
     */
    private void processAddDishItem(ListItemEntity item, @NotNull ItemStateContext itemStateContext) throws ItemProcessingException {
        DishItemEntity dishItem = itemStateContext.getDishItem();
        // find existing
        ListItemDetailEntity existing = item.getDetails().stream()
                .filter(detail -> detail.getLinkedDishId() != null)
                .filter(detail -> detail.getLinkedDishId().equals(dishItem.getDish().getId()))
                .findFirst().orElse(null);

        // convert dish item to list context or unit, if available
        ConvertibleAmount converted = null;
        try {
            converted =  conversionService.convertDishItemForList(dishItem, existing, item);
        } catch (ConversionPathException | ConversionFactorException e) {
            // we weren't able to convert this - it happens sometimes
            String message = String.format("weren't able to convert dishItem [%s] to list context. ", dishItem.getDishItemId());
            log.warn(message);
        }

        // add list item
        if (converted != null && converted.getUnit() != null) {
            addSpecifiedAmount(converted, item, existing, itemStateContext);
        } else {
            addNonSpecifiedAmount(existing, item, itemStateContext);
        }

        conversionService.sumItemDetails(item, itemStateContext);
        // save changes to item
        //MM 2236 deal with massaging item amounts (whole quantity, fractional quantity, description)

        item.setUpdatedOn(new Date());
        listItemRepository.save(item);


//MM 2236 - will need to pull unit size all through this code

    }

    private void addNonSpecifiedAmount(ListItemDetailEntity existing, ListItemEntity item, @NotNull ItemStateContext context) {
        if (existing != null) {
            existing.setCount(existing.getCount() + 1);
            return;
        }
        ListItemDetailEntity newDetail = new ListItemDetailEntity();
        newDetail.setLinkedListId(context.getTargetListId());
        newDetail.setLinkedDishId(context.getDishId());
        newDetail.setCount(1);
        // add to list item
        newDetail.setItem(item);
        item.addDetailToItem(listItemDetailRepository.save(newDetail));
    }

    private void addSpecifiedAmount(ConvertibleAmount converted, ListItemEntity item, ListItemDetailEntity existing, @NotNull ItemStateContext context) throws ItemProcessingException {
        //MM 2236 not paying attention to filling in other detail fields - particularly size
        if (existing != null) {
            if (!existing.getUnitId().equals(converted.getUnit().getId())) {
                String message = String.format("units don't match (existing: %s, converted: %s)while adding to item", existing.getUnitId(), converted.getUnit().getId());
                log.error(message);
                throw new ItemProcessingException(message);
            }
            Double newQuantity = existing.getQuantity() + converted.getQuantity();
            existing.setQuantity(newQuantity);
            return;
        }
        ListItemDetailEntity newDetail = new ListItemDetailEntity();
        newDetail.setLinkedListId(context.getTargetListId());
        newDetail.setLinkedDishId(context.getDishId());
        newDetail.setCount(1);
        newDetail.setQuantity(converted.getQuantity());
        newDetail.setRawEntry(context.getDishItem().getRawEntry());
        newDetail.setUnitId(converted.getUnit().getId());

        // add to list item
        newDetail.setItem(item);
        item.addDetailToItem(listItemDetailRepository.save(newDetail));


    }


    /*
    Processes an addition of a list item - resulting in multiple added/updated item details in the passed item.
    New / changed details are converted to list type, and ready to be summed
     */
    private void processAddListItem(ListItemEntity item, @NotNull ItemStateContext itemStateContext) {

        // add types
        //   single tag - single detail (now, no amounts, but later, possibly)
        //     result in single detail, without linked ids
        //   dish item - single detail w/wo amounts
        //     result in single detail, with dish id
        //   list item - multiple details w/wo amounts
        //     result in multiple details, each with either just list id, or list id and dish id

    }

    /*
Processes an addition of a simple item - resulting in a single added/updated item detail in the passed item.
New / changed detail is converted to list type, and ready to be summed
 */
    private void processAddSimpleItem(ListItemEntity item, @NotNull ItemStateContext itemStateContext) {

    }

    private ProcessingType determineProcessingType(@NotNull ItemStateContext context) {
        if (context.getDishItem() != null) {
            return ProcessingType.DISH;
        } else if (context.getListItem() != null) {
            return ProcessingType.LIST;
        }
        return ProcessingType.SIMPLE_ITEM;
    }

    private void addItemDetails(ListItemEntity item, @NotNull ItemStateContext context) throws JsonProcessingException {
        // get detail candidates - returns multiple for item (which may have more than one detail)
        // otherwise, returns one - either new or existing
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
//MM this can be optimized - separate more handling of different types - list, dish or bytag
        List<ListItemDetailEntity> candidates = new ArrayList<>();

        if (context.getListItem() != null && context.getListItem().getDetails() != null
                && !context.getListItem().getDetails().isEmpty()) {
            // adding a list item - need to add each detail
            for (ListItemDetailEntity toCopy : context.getListItem().getDetails()) {
                ListItemDetailEntity candidate = copyItemDetail(toCopy, context.getListId());
                candidate.setLinkedListId(context.getListId());
                candidate.setCount(1);
                candidates.add(candidate);
            }
            return candidates;
        } else if (context.getListItem() != null &&
                (context.getListItem().getDetails() != null ||
                        context.getListItem().getDetails().isEmpty())) {
            ListItemDetailEntity newDetail = new ListItemDetailEntity();
            newDetail.setLinkedListId(context.getListItem().getListId());
            newDetail.setLinkedDishId(context.getDishId());
            newDetail.setItem(context.getListItem());
            candidates.add(newDetail);
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

    private enum ProcessingType {
        SIMPLE_ITEM,
        DISH,
        LIST
    }
}
