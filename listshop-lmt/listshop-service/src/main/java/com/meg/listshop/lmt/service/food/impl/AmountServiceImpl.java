/**
 * Created by margaretmartin on 13/05/2017.
 */
package com.meg.listshop.lmt.service.food.impl;

import com.meg.listshop.auth.service.UserPropertyService;
import com.meg.listshop.conversion.data.pojo.DomainType;
import com.meg.listshop.lmt.data.pojos.SuggestionDTO;
import com.meg.listshop.lmt.data.repository.AmountRepository;
import com.meg.listshop.lmt.data.repository.UnitSearchCriteria;
import com.meg.listshop.lmt.service.food.AmountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@Transactional
public class AmountServiceImpl implements AmountService {

    private static final Logger LOG = LoggerFactory.getLogger(AmountServiceImpl.class);

    private final AmountRepository amountRepository;
    private final UserPropertyService userPropertyService;


    @Value("#{'${conversionservice.token.combine:extra,xtra,firmly,loosely}'.split(',')}")
    private Set<String> TAKES_NEXT_TOKEN;

    @Autowired
    public AmountServiceImpl(AmountRepository amountRepository,
                             UserPropertyService userPropertyService) {
        this.amountRepository = amountRepository;
        this.userPropertyService = userPropertyService;
    }

    @Override
    public List<String> pullMarkersForModifers(List<String> modifierTokens, Long conversionId) {
        return amountRepository.mapMarkersForConversionId(conversionId, modifierTokens);
    }

    @Override
    public List<String> pullUnitSizesForModifiers(List<String> modifierTokens, Long conversionId) {
        return amountRepository.mapUnitSizesForConversionId(conversionId, modifierTokens);
    }


    private List<SuggestionDTO> getUnitSuggestionList(Long userId, Long tagId, Boolean isLiquid, DomainType domain) {
        List<SuggestionDTO> suggestions = new ArrayList<>();

        // resolve domain - passed, from user, or null
        DomainType searchDomain = domain;
        if (searchDomain == null && userId != null) {
            searchDomain = userPropertyService.getUserPreferredDomain(userId);
        }
        // get unit ids for parameters
        Set<Long> unitIds = new HashSet<>();
        Set<Long> tagUnitIds = new HashSet<>();

        // get unit ids for tag, if available
        if (tagId != null) {
            tagUnitIds = amountRepository.getUnitIdsForTag(tagId);
            if (!tagUnitIds.isEmpty()) {
            unitIds.addAll(tagUnitIds);
            suggestions.addAll(amountRepository.getUnitSuggestionsByIds(tagUnitIds));
            }
        }

        // get unit ids for isLiquid and domain
        UnitSearchCriteria searchCriteria = new UnitSearchCriteria(isLiquid,searchDomain);
        Set<Long> nonTagUnitIds = amountRepository.getUnitIdsForCriteria(searchCriteria);
        nonTagUnitIds.removeAll(tagUnitIds);
        suggestions.addAll(amountRepository.getUnitSuggestionsByIds(nonTagUnitIds));


        return suggestions;
    }

    @Override
    public List<SuggestionDTO> getTextSuggestions(Long userId, Long tagId, Boolean isLiquid, DomainType domainType) {
        LOG.debug("Get text suggestion list for userId [{}], tagId [{}], isLiquid [{}], domainType [{}]", userId, tagId, isLiquid, domainType);
        List<SuggestionDTO> suggestions = new ArrayList<>();
        // get unit text markers
        suggestions.addAll(getUnitSuggestionList(userId,tagId,isLiquid,domainType));
        // get all non-unit text markers
        suggestions.addAll(amountRepository.getNonUnitSuggestions());
        return suggestions;
    }



}
