package com.meg.listshop.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by margaretmartin on 04/10/2017.
 */
@Configuration
public class SpringDocConfiguration {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("LISTSHOP API")
                        .description("REST API for The-List-Shop")
                        .version("v0.0.1"));
    }

}
