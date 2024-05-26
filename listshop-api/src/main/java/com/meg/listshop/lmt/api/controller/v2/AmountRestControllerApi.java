package com.meg.listshop.lmt.api.controller.v2;

import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.model.SuggestionListResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/amount")
public interface AmountRestControllerApi {


    @GetMapping(value = "/suggestions/{tagId}")
    ResponseEntity<SuggestionListResource> retrieveTextSuggestionsForTag(Authentication authentication, @PathVariable("tagId") String tagId,
                                                                         @RequestParam(name = "liquid", required = false) Boolean isLiquid,
                                                                         @RequestParam(name = "domain", required = false) String domain) throws BadParameterException;

    @GetMapping(value = "/suggestions")
    ResponseEntity<SuggestionListResource> retrieveSuggestions(@RequestParam(name = "liquid", required = false) Boolean isLiquid,
                                                               @RequestParam(name = "domain", required = false) String domain);


}
