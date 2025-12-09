package com.Lamicons.CurriculumService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.Lamicons.CurriculumService")
public class CurriculumServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurriculumServiceApplication.class, args);
	}

}
