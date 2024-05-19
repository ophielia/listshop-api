package com.meg.listshop.lmt.api.controller.v2;

import com.meg.listshop.lmt.api.model.AmountTextListResource;
import com.meg.listshop.lmt.api.model.TagListResource;
import com.meg.listshop.lmt.api.model.UnitDisplayListResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/amount")
public interface AmountRestControllerApi {


    // returns a list of markers and unit sizes
    @GetMapping(value = "/modifiers")
    ResponseEntity<AmountTextListResource> retrieveModifierList();

    // returns a list of unit displays for given tag
    @GetMapping(value = "/{tagId}/unit")
    ResponseEntity<UnitDisplayListResource> retrieveUnitList(@PathVariable("tagId") String tagId,
                                                             @PathVariable(name = "liquid", required = false) Boolean isLiquid,
                                                             @PathVariable(name = "domain", required = false) String domain);

    // returns a list of units for domain and liquid / not liquid (optional)
    @GetMapping(value = "/unit")
    ResponseEntity<TagListResource> retrieveUnitList(@PathVariable(name = "liquid", required = false) Boolean isLiquid,
                                                     @PathVariable(name = "domain", required = false) String domain);

}
