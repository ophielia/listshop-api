package com.meg.atable.api;

import com.meg.atable.model.Tag;
import com.meg.atable.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/tag")
public class TagRestController {

    private final TagService tagService;

    @Autowired
    TagRestController(TagService tagService) {
        this.tagService = tagService;
    }


    @RequestMapping(method = RequestMethod.GET)
    Collection<Tag> retrieveTagList() {
        return this.tagService.getTagList();
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@RequestBody Tag input) {
        Tag result = this.tagService.save(input);

        if (result != null) {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{id}")
                    .buildAndExpand(result.getId()).toUri();
            return ResponseEntity.created(location).build();

        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{tagId}")
    Tag readTag( @PathVariable Long tagId) {
        return tagService.getTagById(tagId);
    }

}
