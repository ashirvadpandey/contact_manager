package com.smartcontact;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SmarContactManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmarContactManagerApplication.class, args);
		
//		System.out.println(new BCryptPasswordEncoder().encode("Deepu"));
	}

}
