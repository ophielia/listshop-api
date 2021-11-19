package com.meg.listshop.auth.api.model;


import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.lmt.api.model.ModelMapper;
import org.springframework.hateoas.ResourceSupport;

public class UserResource extends ResourceSupport {

    private final User user;

    public UserResource(UserEntity userEntity, String token) {
        this.user = ModelMapper.toModel(userEntity, token);
    }


    public User getUser() {
        return user;
    }
}