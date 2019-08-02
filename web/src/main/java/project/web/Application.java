package project.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import project.persistence.InvitationRepository;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = InvitationRepository.class)
@EntityScan(basePackages = {"project"})
@ComponentScan({"project"})
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }
}

