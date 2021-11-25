package com.meg.listshop.lmt.api.controller;

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
@RequestMapping("/tag")
public interface TagRestControllerApi {

    @RequestMapping(method = RequestMethod.GET)
    ResponseEntity<TagListResource> retrieveTagList(HttpServletRequest request,
                                                    @RequestParam(value = "filter", required = false) String filter,
                                                    @RequestParam(value = "tag_type", required = false) String tagType,
                                                    @RequestParam(value = "extended", required = false) Boolean extended);

    @RequestMapping(value = "/delete/{tagId}", method = RequestMethod.DELETE)
    ResponseEntity<Object> saveTagForDelete(@PathVariable("tagId") Long tagId, @RequestParam(value = "replacementTagId", required = true) Long replacementTagId);

    @RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    ResponseEntity<Tag> add(HttpServletRequest request, @RequestBody Tag input);

    @RequestMapping(value = "{tagId}/child", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    ResponseEntity<Tag> addAsChild(HttpServletRequest request, @PathVariable Long tagId, @RequestBody Tag input);

    @RequestMapping(value = "{tagId}/children", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    ResponseEntity addChildren(@PathVariable Long tagId, @RequestParam(value = "tagIds", required = false) String filter);

    @RequestMapping(value = "{parentId}/child/{childId}", method = RequestMethod.PUT, produces = "application/json")
    ResponseEntity assignChildToParent(@PathVariable("parentId") Long parentId, @PathVariable("childId") Long childId);

    @RequestMapping(value = "/base/{tagId}", method = RequestMethod.PUT, produces = "application/json")
    ResponseEntity assignChildToBaseTag(@PathVariable("tagId") Long tagId);

    @RequestMapping(method = RequestMethod.GET, value = "/{tagId}", produces = "application/json")
    ResponseEntity<Tag> readTag(HttpServletRequest request, @PathVariable("tagId") Long tagId);

    @RequestMapping(method = RequestMethod.PUT, value = "/{tagId}", consumes = "application/json")
    ResponseEntity<Object> updateTag(@PathVariable Long tagId, @RequestBody Tag input);

    @Deprecated
    @RequestMapping(method = RequestMethod.GET, value = "/{tagId}/dish", produces = "application/json")
    ResponseEntity<Tag> getChildrenTagDishAssignments(HttpServletRequest request, Principal principal, @PathVariable("tagId") Long tagId);

    @RequestMapping(method = RequestMethod.PUT, value = "/{fromTagId}/dish/{toTagId}", produces = "application/json")
    ResponseEntity replaceTagsInDishes(HttpServletRequest request, Principal principal, @PathVariable("fromTagId") Long tagId, @PathVariable("toTagId") Long toTagId);


}
