package com.meg.listshop.lmt.api.controller.v2;

import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.model.v2.Tag;
import com.meg.listshop.lmt.api.model.TagPut;
import com.meg.listshop.lmt.api.model.v2.TagList;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/v2/tag")
public interface V2TagRestControllerApi {


    @GetMapping()
    ResponseEntity<TagList> retrieveUserTagList(
            Authentication authentication,
            HttpServletRequest request);

    @PostMapping(value = "{tagId}/child", produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> addAsChild(Authentication authentication, HttpServletRequest request, @PathVariable("tagId") Long tagId, @RequestBody Tag input,
                                   @RequestParam(value = "asStandard", required = false, defaultValue = "false") boolean asStandard) throws BadParameterException, MalformedURLException;


    @GetMapping( value = "/{tagId}", produces = "application/json")
    ResponseEntity<Tag> readTag(HttpServletRequest request, @PathVariable("tagId") Long tagId);

    @PutMapping(value = "{tagId}", produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> updateTag(Authentication authentication, HttpServletRequest request, @PathVariable("tagId") Long tagId,
                                     @RequestBody TagPut input) throws BadParameterException;


}
