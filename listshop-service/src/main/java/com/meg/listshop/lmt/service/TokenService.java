package com.meg.listshop.lmt.service;

import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.model.TokenType;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface TokenService {


    void generateTokenForUser(TokenType tokenType, String encryptedEmail) throws BadParameterException;
}
