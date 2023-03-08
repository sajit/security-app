package edu.umd.cysec.capstone.securityapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import edu.umd.cysec.capstone.securityapp.dao.UserRepository;

@SpringBootApplication
@EnableMongoRepositories
public class SecurityAppApplication {

	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(SecurityAppApplication.class, args);
	}

}
