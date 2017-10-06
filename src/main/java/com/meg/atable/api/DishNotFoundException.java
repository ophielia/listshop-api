package com.meg.atable.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DishNotFoundException extends RuntimeException {

    public DishNotFoundException(Long dishId) {
        super("couldn't find this dish [" + dishId+"]");
    }
}
