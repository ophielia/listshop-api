package com.meg.atable.auth.api.model;


import com.meg.atable.auth.data.entity.UserEntity;
import com.meg.atable.lmt.api.model.ModelMapper;
import org.springframework.hateoas.ResourceSupport;

public class UserResource extends ResourceSupport {

    private final User user;

    public UserResource(UserEntity UserEntity) {
        this.user = ModelMapper.toModel(UserEntity);

        Long userId = UserEntity.getId();
    }


    public User getUser() {
        return user;
    }
}