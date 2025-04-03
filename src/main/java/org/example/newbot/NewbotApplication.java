package org.example.newbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication

@EntityScan("org.example.newbot.model")
@EnableJpaRepositories("org.example.newbot.repository")
public class NewbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewbotApplication.class, args);
    }

}
