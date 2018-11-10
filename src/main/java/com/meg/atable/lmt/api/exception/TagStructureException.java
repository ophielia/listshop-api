package com.meg.atable.lmt.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TagStructureException extends RuntimeException {

    public TagStructureException(Long tagId) {
        super("couldn't find this tag [" + tagId+"]");
    }
}
