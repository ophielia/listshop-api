package com.meg.listshop.auth.service;

import com.meg.listshop.auth.api.model.ClientDeviceInfo;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;

public interface JwtService {
    String extractUsername(String token);

    Date extractExpiration(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    Boolean validateToken(String token, UserDetails userDetails);

    String generateToken(String username, ClientDeviceInfo deviceInfo);


}
