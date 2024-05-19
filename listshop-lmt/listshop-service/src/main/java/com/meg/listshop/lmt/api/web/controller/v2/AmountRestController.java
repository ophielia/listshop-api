package com.meg.listshop.lmt.api.web.controller.v2;

import com.meg.listshop.lmt.api.controller.v2.AmountRestControllerApi;
import com.meg.listshop.lmt.api.model.AmountTextListResource;
import com.meg.listshop.lmt.api.model.TagListResource;
import com.meg.listshop.lmt.api.model.UnitDisplayListResource;
import com.meg.listshop.lmt.service.food.AmountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

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
    public ResponseEntity<AmountTextListResource> retrieveModifierList() {
//        @GetMapping(value = "/modifiers")
//        ResponseEntity<List<String>> retrieveModifierList();
        List<String> allModifiers = amountService.getAllModifiers();
        AmountTextListResource resource = new AmountTextListResource(allModifiers);
        return new ResponseEntity<>(resource, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<UnitDisplayListResource> retrieveUnitList(String tagId, Boolean isLiquid, String domain) {
//        @GetMapping(value = "/{tagId}/unit")
//        ResponseEntity<UnitDisplayListResource> retrieveUnitList(@PathVariable("tagId") String tagId,
//                @PathVariable(name="liquid",  required = false) Boolean isLiquid,
//                @PathVariable(name="domain", required = false) String domain);
//
        return null;
    }

    @Override
    public ResponseEntity<TagListResource> retrieveUnitList(Boolean isLiquid, String domain) {
//        @GetMapping(value = "/unit")
//        ResponseEntity<TagListResource> retrieveUnitList(@PathVariable(name="liquid",  required = false) Boolean isLiquid,
//                @PathVariable(name="domain", required = false) String domain);
//
//
        return null;
    }
}
