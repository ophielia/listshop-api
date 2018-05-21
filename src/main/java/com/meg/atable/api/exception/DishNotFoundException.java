package com.meg.atable.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DishNotFoundException extends ObjectNotFoundException {

    public DishNotFoundException(Long dishId) {
        super(dishId, "dish");
    }
}
