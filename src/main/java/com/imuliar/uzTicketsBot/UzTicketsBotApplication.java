package com.imuliar.uzTicketsBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@ComponentScan("com.imuliar.uzTicketsBot")
@EntityScan(basePackages = "com.imuliar.uzTicketsBot.model")
@EnableJpaRepositories
@SpringBootApplication
public class UzTicketsBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(UzTicketsBotApplication.class, args);
	}

	@Bean
	public MessageSource messageSource(){
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("msg");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setCacheSeconds(3600);
		return messageSource;
	}

}
