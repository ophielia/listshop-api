package com.meg.listshop.auth.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Created by stephan on 20.03.16.
 */

public interface ListShopUserDetailsService extends UserDetailsService {


    UserDetails loadUserByToken(String token);
}
