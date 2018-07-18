package com.imuliar.uzTicketsBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@EntityScan(basePackages = "model")
@SpringBootApplication
public class UzTicketsBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(UzTicketsBotApplication.class, args);
	}
}
