package com.ninos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})  // it's meaning ignore a security
public class InvoiceAppApplication {

	public static final int STRENGTH = 12;

	public static void main(String[] args) {
		SpringApplication.run(InvoiceAppApplication.class, args);
	}


    @Bean
	public BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder(STRENGTH);
	}

}
