package com.meg.listshop.auth.api.web;

import com.meg.listshop.auth.api.controller.AuthenticationRestControllerApi;
import com.meg.listshop.auth.api.model.UserResource;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.auth.service.impl.JwtAuthenticationRequest;
import com.meg.listshop.auth.service.impl.JwtAuthenticationResponse;
import com.meg.listshop.auth.service.impl.JwtTokenUtil;
import com.meg.listshop.auth.service.impl.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AuthenticationRestController implements AuthenticationRestControllerApi {

    @Value("${jwt.header}")
    private String tokenHeader;


    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    public ResponseEntity<Object> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest, Device device) {


        // Perform the security
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-security so we can generate token
        UserEntity userEntity = userService.getUserByUserEmail(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userEntity, device);

        // Return the token
        final UserResource user = new UserResource(userEntity, token);
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<Object> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);

        if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
            String refreshedToken = jwtTokenUtil.refreshToken(token);
            return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
