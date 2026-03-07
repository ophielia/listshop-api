package com.meg.listshop.auth.service.impl;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface JwtAuthFilter extends Filter {
    void doFilterInternal(HttpServletRequest request,
                          HttpServletResponse response,
                          FilterChain filterChain) throws ServletException, IOException;
}
