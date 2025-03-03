package com.chukchuk.haksa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ChukchukHaksaApplication {

	/**
	 * Launches the Spring Boot application.
	 *
	 * <p>This entry point method initiates the Spring application context by invoking
	 * {@link SpringApplication#run(Class, String[])} with the provided command line arguments.</p>
	 *
	 * @param args command line arguments used to start the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(ChukchukHaksaApplication.class, args);
	}

}
