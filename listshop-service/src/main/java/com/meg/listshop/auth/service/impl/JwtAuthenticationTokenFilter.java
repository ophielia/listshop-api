package com.meg.listshop.auth.service.impl;

import com.meg.listshop.lmt.api.exception.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {


    @Autowired
    private ListShopUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authToken = request.getHeader(this.tokenHeader);

        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);


            // get user for token
            UserDetails userDetails = this.userDetailsService.loadUserByToken(authToken);

            if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = userDetails.getUsername();

                logger.debug("checking authentication for user " + username);

                if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    logger.info("authenticated user " + username + ", setting security context");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    logger.debug("Token exists for user, but it is invalid.");
                    throw new AuthenticationException("Token exists for user, but it is invalid");
                }
            }
        }
        chain.doFilter(request, response);
    }
}