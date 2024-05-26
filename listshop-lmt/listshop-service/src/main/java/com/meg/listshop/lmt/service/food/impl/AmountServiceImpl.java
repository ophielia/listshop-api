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
    public List<String> pullModifierTokens(String rawModifiers) {
        List<String> result = new ArrayList<>();
        if (rawModifiers == null) {
            return result;
        }
        if (rawModifiers.contains(",")) {
            rawModifiers = rawModifiers.replace(",", "");
        }
        StringTokenizer tokens = new StringTokenizer(rawModifiers, " ");
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (TAKES_NEXT_TOKEN.contains(token) && tokens.hasMoreTokens()) {
                String nextToken = tokens.nextToken();
                result.add(String.join(" ", token, nextToken));
            } else {
                result.add(token);
            }
        }
        return result;
    }

    @Override
    public List<String> pullMarkersForModifers(List<String> modifierTokens, Long conversionId) {
        return amountRepository.mapMarkersForConversionId(conversionId, modifierTokens);
    }

    @Override
    public List<String> pullUnitSizesForModifiers(List<String> modifierTokens, Long conversionId) {
        return amountRepository.mapUnitSizesForConversionId(conversionId, modifierTokens);
    }


    private Set<Long> getUnitIds(Long userId, Long tagId, Boolean isLiquid, DomainType domain) {
        // resolve domain - passed, from user, or null
        DomainType searchDomain = domain;
        if (domain == null && userId != null) {
            searchDomain = userPropertyService.getUserPreferredDomain(userId);
        }
        Set<Long> unitIds = new HashSet<>();

        // get unit ids for tag, if available
        if (tagId != null) {
            Set<Long> unitIdsForTag = amountRepository.getUnitIdsForTag(tagId);
            unitIds.addAll(unitIdsForTag);
        }
        // get unit ids for isLiquid and domain
        UnitSearchCriteria searchCriteria = new UnitSearchCriteria(isLiquid,searchDomain);
        Set<Long> nonTagUnitIds = amountRepository.getUnitIdsForCriteria(searchCriteria);
        return new HashSet<Long>(nonTagUnitIds);
    }

    private List<SuggestionDTO> getUnitSuggestionList(Long userId, Long tagId, Boolean isLiquid, DomainType domain) {
        // resolve domain - passed, from user, or null
        DomainType searchDomain = domain;
        if (searchDomain == null && userId != null) {
            searchDomain = userPropertyService.getUserPreferredDomain(userId);
        }
        // get unit ids for parameters
        Set<Long> unitIds = getUnitIds(userId, tagId, isLiquid, searchDomain);

        // get suggestions (from mapped_modifiers, type unit) (this needs to be added)
        return amountRepository.getUnitSuggestionsByIds(unitIds);
    }

    @Override
    public List<SuggestionDTO> getTextSuggestions(Long userId, Long tagId, Boolean isLiquid, DomainType domainType) {
        LOG.debug("Get text suggestion list for userId [{}], tagId [{}], isLiquid [{}], domainType [{}]", userId, tagId, isLiquid, domainType);
        List<SuggestionDTO> suggestions = new ArrayList<>();
        // get all non-unit text markers
        suggestions.addAll(amountRepository.getNonUnitSuggestions());
        // get unit text markers
        suggestions.addAll(getUnitSuggestionList(userId,tagId,isLiquid,domainType));

        return suggestions;
    }



}
