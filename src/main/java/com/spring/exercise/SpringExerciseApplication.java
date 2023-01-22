package com.spring.exercise;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Custom API title",
				version = "1.0"
		)
)
public class SpringExerciseApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringExerciseApplication.class, args);
	}

}
