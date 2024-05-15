/**
 * Created by margaretmartin on 13/05/2017.
 */
package com.meg.listshop.lmt.service.food.impl;

import com.meg.listshop.lmt.data.repository.AmountRepository;
import com.meg.listshop.lmt.service.food.AmountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;


@Service
@Transactional
public class AmountServiceImpl implements AmountService {

    private static final Logger LOG = LoggerFactory.getLogger(AmountServiceImpl.class);

    private final AmountRepository amountRepository;

    @Value("#{'${conversionservice.token.combine:extra,xtra,firmly,loosely}'.split(',')}")
    private Set<String> TAKES_NEXT_TOKEN;

    @Autowired
    public AmountServiceImpl(AmountRepository amountRepository) {
        this.amountRepository = amountRepository;
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
}
