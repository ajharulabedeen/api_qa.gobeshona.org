package org.gobeshona.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GobeshonaQAapi {

	public static void main(String[] args) {
    SpringApplication.run(GobeshonaQAapi.class, args);
	}

}
