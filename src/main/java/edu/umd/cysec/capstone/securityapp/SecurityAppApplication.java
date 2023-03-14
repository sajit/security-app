package edu.umd.cysec.capstone.securityapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class SecurityAppApplication {



	public static void main(String[] args) {
		SpringApplication.run(SecurityAppApplication.class, args);
	}

}
