package com.example.orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrdersSmellyApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdersSmellyApplication.class, args);
		System.out.println("Orders app started on 8080 (smelly)");

	}

}
