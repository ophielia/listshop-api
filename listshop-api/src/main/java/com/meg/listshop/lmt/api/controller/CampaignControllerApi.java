package com.meg.listshop.lmt.api.controller;

import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.model.CampaignPut;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/campaign")
@CrossOrigin
public interface CampaignControllerApi {


    @PostMapping(produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> addCampaign(HttpServletRequest request, Principal principal, @RequestBody CampaignPut input) throws BadParameterException;

}
