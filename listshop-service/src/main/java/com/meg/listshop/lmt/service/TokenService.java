/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.service;

import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.exception.TokenException;
import com.meg.listshop.lmt.api.model.TokenType;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface TokenService {


    void generateTokenForUser(TokenType tokenType, String userEmail) throws BadParameterException;

    void processTokenFromUser(TokenType type, String tokenValue, String tokenParameter) throws BadParameterException, TokenException;
}
