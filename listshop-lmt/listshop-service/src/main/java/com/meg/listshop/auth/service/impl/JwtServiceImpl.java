package com.meg.listshop.auth.service.impl;

import com.meg.listshop.auth.api.model.ClientDeviceInfo;
import com.meg.listshop.auth.api.model.ClientType;
import com.meg.listshop.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret.encoded}")
    public String secret;


    @Value("${web.login.period.in.days:7}")
    private Long expirationInDays;

    private static final Long DAY_TO_MILLISECONDS = 60L * 60L * 24L * 1000L;

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    @Override
    public String generateToken(String username, ClientDeviceInfo deviceInfo) {
        Map<String, Object> claims = new HashMap<>();
        Date expirationDate = determineExpirationDate(deviceInfo);
        return createToken(claims, username, expirationDate);
    }

    private Date determineExpirationDate(ClientDeviceInfo deviceInfo) {
        ClientType clientType = deviceInfo.getClientType();
        if (clientType == ClientType.Web) {
            return new Date(System.currentTimeMillis() + expirationInDays * DAY_TO_MILLISECONDS);
        }
        return null;
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        Date expirationDate = extractExpiration(token);
        return expirationDate != null && extractExpiration(token).before(new Date());
    }

    private String createToken(Map<String, Object> claims, String username, Date expirationDate) {
        JwtBuilder builder = Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()));

        if (expirationDate != null) {
            builder.expiration(expirationDate);
        }

        return builder
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
