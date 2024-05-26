package com.meg.listshop.lmt.api.web.controller.v2;

import com.meg.listshop.auth.service.impl.JwtUser;
import com.meg.listshop.conversion.data.pojo.DomainType;
import com.meg.listshop.lmt.api.controller.v2.AmountRestControllerApi;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.model.ModelMapper;
import com.meg.listshop.lmt.api.model.SuggestionListResource;
import com.meg.listshop.lmt.data.pojos.SuggestionDTO;
import com.meg.listshop.lmt.service.food.AmountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@CrossOrigin
public class AmountRestController implements AmountRestControllerApi {

    private static final Logger logger = LoggerFactory.getLogger(AmountRestController.class);

    private final AmountService amountService;

    @Autowired
    public AmountRestController(AmountService amountService) {
        this.amountService = amountService;
    }

    @Override
    public ResponseEntity<SuggestionListResource> retrieveTextSuggestionsForTag(Authentication authentication, String tagId, Boolean isLiquid, String domain) throws BadParameterException {
        //@GetMapping(value = "/suggestions/{tagId}")
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        String message = String.format("retrieving text suggestions for user [%S], tag [%S], liquid [%S] and domain [%S]",
                 userDetails.getId(), tagId, isLiquid, domain);
        logger.info(message);

        Long longTagId = null;
        if (tagId != null) {
         try {
             longTagId = Long.valueOf(tagId);
         } catch (NumberFormatException e) {
             var exceptionMessage = String.format("tagId [%S] cannot be converted to Long",tagId);
             throw new BadParameterException(exceptionMessage,e);
         }
        }

        DomainType domainType = null;
        if (domain != null) {
            domainType = DomainType.valueOf(domain);
            if (domainType == null) {
                var exceptionMessage = String.format("domain [%S] cannot be converted to DomainType",domain);
                throw new BadParameterException(exceptionMessage);
            }
        }

        List<SuggestionDTO> suggestionDTOS = amountService.getTextSuggestions(userDetails.getId(),longTagId,isLiquid, domainType);

        SuggestionListResource listResource = new SuggestionListResource(suggestionDTOS.stream()
                .map(ModelMapper::toModel)
                .collect(Collectors.toList()));
        return ResponseEntity.ok(listResource);
    }

    @Override
    public ResponseEntity<SuggestionListResource> retrieveSuggestions(Boolean isLiquid, String domain) {
        return null;
    }
}
