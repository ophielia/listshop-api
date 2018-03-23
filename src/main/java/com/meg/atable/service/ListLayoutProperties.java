package com.meg.atable.service;

import com.meg.atable.api.model.ListLayoutType;
import com.meg.atable.api.model.ListType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by margaretmartin on 30/10/2017.
 */
@Configuration
@ConfigurationProperties(prefix = "list.layout.properties")
public class ListLayoutProperties {

    private Integer dispOrderIncrement;

    public Integer getDispOrderIncrement() {
        return dispOrderIncrement;
    }

    public void setDispOrderIncrement(Integer dispOrderIncrement) {
        this.dispOrderIncrement = dispOrderIncrement;
    }
}
