package com.phonecompany.phonebill_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.phonecompany.phonebill_api", "com.phonecompany.billing"})
public class PhonebillApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhonebillApiApplication.class, args);
	}

}
