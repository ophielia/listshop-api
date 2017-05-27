package com.meg.atable;

import com.meg.atable.model.Dish;
import com.meg.atable.model.User;
import com.meg.atable.repository.DishRepository;
import com.meg.atable.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}



	@Bean
	CommandLineRunner init(UserRepository userRepository,
						   DishRepository dishRepository) {
		return (evt) -> Arrays.asList("me,carrie,mom,michelle".split(","))
				.forEach(
						u -> {
							User user = userRepository.save(new User(u,"password"));
							dishRepository.save(new Dish(user,"dishname-" + u));
							dishRepository.save(new Dish(user,"dishname2-" + u));
						}

				);
	}


}
