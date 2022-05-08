package com.meg.listshop.admin.controller;

import com.meg.listshop.lmt.api.model.Tag;
import com.meg.listshop.lmt.api.model.TagListResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/admin/tag")
public interface AdminTagRestControllerApi {


    // standard/list (?filter=review)
// user/xxxxx/list
// PUT TagUpdate contains list of ids, operation (assignToUser, copyToStandard, review), userId
    @GetMapping(value = "/standard/list")
    public ResponseEntity<TagListResource> getStandardTagList(@RequestParam(value = "filter", required = false) String filter);

    @GetMapping(value = "/user/{userId}/list")
    ResponseEntity<TagListResource> getUserTagList(@PathVariable("userId") Long userId,
                                                   @RequestParam(value = "filter", required = false) String filter);

    @RequestMapping(value = "/delete/{tagId}", method = RequestMethod.DELETE)
    ResponseEntity<Object> saveTagForDelete(@PathVariable("tagId") Long tagId, @RequestParam(value = "replacementTagId", required = true) Long replacementTagId);

    @RequestMapping(value = "{tagId}/children", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    ResponseEntity addChildren(@PathVariable Long tagId, @RequestParam(value = "tagIds", required = false) String filter);

    @RequestMapping(value = "{parentId}/child/{childId}", method = RequestMethod.PUT, produces = "application/json")
    ResponseEntity assignChildToParent(@PathVariable("parentId") Long parentId, @PathVariable("childId") Long childId);

    @RequestMapping(value = "/base/{tagId}", method = RequestMethod.PUT, produces = "application/json")
    ResponseEntity assignChildToBaseTag(@PathVariable("tagId") Long tagId);

    @RequestMapping(method = RequestMethod.PUT, value = "/{tagId}", consumes = "application/json")
    ResponseEntity<Object> updateTag(@PathVariable Long tagId, @RequestBody Tag input);

    @RequestMapping(method = RequestMethod.PUT, value = "/{fromTagId}/dish/{toTagId}", produces = "application/json")
    ResponseEntity replaceTagsInDishes(HttpServletRequest request, Principal principal, @PathVariable("fromTagId") Long tagId, @PathVariable("toTagId") Long toTagId);


}
