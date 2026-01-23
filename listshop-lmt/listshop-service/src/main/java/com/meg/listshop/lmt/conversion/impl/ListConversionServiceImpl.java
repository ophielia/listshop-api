package com.meg.listshop.lmt.conversion.impl;

import com.meg.listshop.common.CommonUtils;
import com.meg.listshop.common.FractionUtils;
import com.meg.listshop.common.RoundingUtils;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.common.data.repository.UnitRepository;
import com.meg.listshop.conversion.data.pojo.*;
import com.meg.listshop.conversion.exceptions.ConversionAddException;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.ConverterService;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.api.model.FractionType;
import com.meg.listshop.lmt.conversion.*;
import com.meg.listshop.lmt.data.entity.DishItemEntity;
import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.list.state.ItemStateContext;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ListConversionServiceImpl implements ListConversionService {

    private static final Logger log = LoggerFactory.getLogger(ListConversionServiceImpl.class);
    private final UnitRepository unitRepo;
    private final ConverterService converterService;

    @Autowired
    public ListConversionServiceImpl(UnitRepository unitRepo, ConverterService converterService) {
        this.unitRepo = unitRepo;
        this.converterService = converterService;
    }

    @Override
    public ConvertibleAmount convertDishItemForList(DishItemEntity dishItem, ListItemDetailEntity existing, ListItemEntity item) throws ConversionPathException, ConversionFactorException {
        if (dishItem == null || dishItem.getUnitId() == null) {
            // nothing to convert - return
            return null;
        }
        UnitEntity toConvertUnit = getUnit(dishItem.getUnitId());
        if (toConvertUnit == null) {
            return null;
        }
        ConvertibleAmount toConvert = new EntityConvertibleAmount(dishItem, toConvertUnit, item.getTag());

        return convertDetail(toConvert, existing, item);
    }

    @Override
    public void sumItemDetails(ListItemEntity item, ItemStateContext context) throws ItemProcessingException {
        // gather details by unit
        Map<String, SummaryConvertibleAmount> unitSummary = new HashMap<>();
        List<ListItemDetailEntity> unspecified = new ArrayList<>();
        for (ListItemDetailEntity detail : item.getDetails()) {
            if (detail.getUnitId() != null) {
                String key = createKey(detail.getUnitId(), detail.getUnitSize());
                if (!unitSummary.containsKey(key)) {
                    UnitEntity unit = getUnit(detail.getUnitId());
                    unitSummary.put(key, new SummaryConvertibleAmount(unit, detail.getUnitSize()));
                }
                unitSummary.get(key).add(detail);
            } else {
                unspecified.add(detail);
            }
        }

        // bow out, if no conversion to a different unit is necessary
        if (unitSummary.size() == 0) {
            // no units found
            setInItem(null, item, unspecified);
            return;
        } else if (unitSummary.size() == 1) {
            SummaryConvertibleAmount summary = unitSummary.entrySet().stream().findFirst()
                    .map(entry -> entry.getValue())
                    .orElse(null);
            //MM 2236 once the dust has settled, rename this to AddScaleRequest
            AddRequest addRequest = new AddRequest(ConversionTargetType.List, summary.getUnit(), summary.getUnitSize());
            ConvertibleAmount summed = null;
            try {
                summed = converterService.scale(summary, addRequest);
            } catch (ConversionFactorException e) {
                log.warn("unable to scale summary amount, unit[{}]", summary.getUnit());
            }
            summary.getDetails().stream().forEach(detail -> detail.setUnspecified(false));
            setInItem(summed, item, unspecified);
            return;
        }

        // get base key - units which all others will be added to
        String baseKey = determineBaseKey(item, unitSummary);
        SummaryConvertibleAmount baseAmount = unitSummary.get(baseKey);
        List<SummaryConvertibleAmount> amountsToConvert = unitSummary.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(baseKey))
                .map(entry -> entry.getValue())
                .collect(Collectors.toList());

        ConvertibleAmount summary = new SimpleAmount(baseAmount.getQuantity(), baseAmount.getUnit());
        for (SummaryConvertibleAmount toConvert : amountsToConvert) {
            AddRequest addRequest = new AddRequest(ConversionTargetType.List, toConvert.getUnit(), toConvert.getUnitSize());
            try {
                summary = converterService.add(toConvert, summary, addRequest);
                toConvert.getDetails().forEach( detail -> detail.setUnspecified(false));
            } catch (ConversionPathException | ConversionFactorException | ConversionAddException e) {
                //MM 2236 - fix exception, and add all detail in toConvert to unspecified
                unspecified.addAll(toConvert.getDetails());
                throw new RuntimeException(e);
            }
        }

        // mark base amount as specified
        baseAmount.getDetails().forEach( detail -> detail.setUnspecified(false));
        setInItem(summary, item, unspecified);
    }

    @Override
    public QuantityElements splitQuantityIntoElements(Double amount) {
        if (amount == null) {
            return new QuantityElements(0.0, 0, null);
        }
        double rounded = RoundingUtils.roundToNearestFraction(amount);
        double fractionalPart = rounded - Math.floor(rounded);

        FractionType fractionType = FractionUtils.getFractionTypeForDecimal(BigDecimal.valueOf(fractionalPart));
        int wholeNumberPart = (int) rounded;

        return new QuantityElements(rounded, wholeNumberPart, fractionType);
    }


    @Override
    public ConvertibleAmount convertListItemDetailForList(ListItemDetailEntity detailToAdd, ListItemDetailEntity existingDetail, ListItemEntity parentItem) throws ConversionPathException, ConversionFactorException {
        if (detailToAdd == null || detailToAdd.getUnitId() == null) {
            // nothing to convert - return
            return null;
        }
        UnitEntity toConvertUnit = getUnit(detailToAdd.getUnitId());
        if (toConvertUnit == null) {
            return null;
        }
        ConvertibleAmount toConvert = new EntityConvertibleAmount(detailToAdd, toConvertUnit, parentItem.getTag());

        return convertDetail(toConvert, existingDetail, parentItem);
    }

    @Override
    public  ConvertibleAmount convertTagForList(TagEntity tag, BasicAmount tagAmount, ListItemDetailEntity existing, ListItemEntity item) throws ConversionPathException, ConversionFactorException {
        if (tagAmount == null || tagAmount.getUnitId() == null) {
            // nothing to convert - return
            return null;
        }
        UnitEntity toConvertUnit = getUnit(tagAmount.getUnitId());
        if (toConvertUnit == null) {
            return null;
        }
        ConvertibleAmount toConvert = new EntityConvertibleAmount(tagAmount, toConvertUnit, tag);

        return convertDetail(toConvert, existing, item);
    }

    @Override
    public ConvertibleAmount addToListItemDetail(ConvertibleAmount converted, ListItemDetailEntity existing, @NotNull ItemStateContext context) throws ConversionPathException, ConversionAddException, ConversionFactorException {
        UnitEntity existingUnit = getUnit(existing.getUnitId());
        EntityConvertibleAmount addTo = new EntityConvertibleAmount(existing, existingUnit, context.getTag());
        AddRequest addRequest = new AddRequest(ConversionTargetType.List, existingUnit, existing.getUnitSize());
        ConvertibleAmount added = null;
        added = converterService.add(converted, addTo, addRequest);

        return added;
    }

    private void setInItem(ConvertibleAmount amount, ListItemEntity item, List<ListItemDetailEntity> unspecified) {
        // set unspecified as unspecified
        unspecified.forEach(detail -> detail.setUnspecified(true));
        // ignore, if no amount
        if (amount == null) {
            return;
        }
        item.setRawQuantity(amount.getQuantity());
        QuantityElements elements = splitQuantityIntoElements(RoundingUtils.roundUpToNearestFraction(amount.getQuantity()));
        item.setRoundedQuantity(elements.quantity());
        item.setFractionalQuantity(elements.fractionType());
        item.setWholeQuantity(elements.wholeNumber());
        item.setUnit(amount.getUnit());
        item.setUnitSize(amount.getUnitSize());
        //MM 2236 - set unspecified for item
    }


    private String determineBaseKey(ListItemEntity item, Map<String, SummaryConvertibleAmount> unitSummary) throws ItemProcessingException {
        // first look for key matching unit id and size in item
        String itemKey = createKey(item.getUnitId(), item.getUnitSize());
        if (unitSummary.containsKey(itemKey)) {
            return itemKey;
        }
        // if this isn't available in the map, just look for the unit alone
        String unitOnlyKey = createKey(item.getUnitId(), null);
        if (unitSummary.containsKey(unitOnlyKey)) {
            return unitOnlyKey;
        }
        // if nothing is still available, return first entry in map
        Optional<String> defaultKey = unitSummary.keySet().stream().findFirst();
        if (!defaultKey.isPresent()) {
            String message = String.format("no key can be found while summing item [%s]", item.getId());
            log.error(message);
            throw new ItemProcessingException(message);
        }
        return defaultKey.get();
    }


    private String createKey(Long unitId, String unitSize) {
        // key is made up of unit_id plus underscore plus size
        String keyUnitId = CommonUtils.elvis(unitId,0L) + "";
        String keyUnitSize = CommonUtils.elvis(unitSize,"");
        return String.format("%s_%s", keyUnitId, keyUnitSize);
    }

    private ConvertibleAmount convertDetail(ConvertibleAmount toConvert, ListItemDetailEntity existing, ListItemEntity item) throws ConversionPathException, ConversionFactorException {
        UnitEntity targetUnit = determineTargetUnit(existing, item);
        if (targetUnit != null) {
            // convert directly to unit
            return converterService.convert(toConvert, targetUnit);
        } else {
            // convert to list context
            //MM also - need user preference for domain here
            ConversionRequest context = new ConversionRequest(ConversionTargetType.List, DomainType.US);
            return converterService.convert(toConvert, context);
        }
    }


    private UnitEntity determineTargetUnit(ListItemDetailEntity existing, ListItemEntity item) {
        // return unit for existing item if available
        if (existing != null && existing.getUnitId()!=null) {
            return getUnit(existing.getUnitId());
        }
        // otherwise, return unit for item
        if (item != null && item.getUnit() != null) {
            return item.getUnit();
        }

        return null;
    }

    private UnitEntity getUnit(Long unitId) {
        if (unitId == null) {
            return null;
        }
        //MM TODO 2236 - possible caching here
        return unitRepo.findById(unitId).orElse(null);
    }
}
