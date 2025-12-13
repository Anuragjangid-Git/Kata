package com.backend.Kata;

import org.springframework.boot.SpringApplication;

public class TestKataApplication {

	public static void main(String[] args) {
		SpringApplication.from(KataApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
