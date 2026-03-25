package yuseteam.mealticketsystemwas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EntityScan(basePackages = "yuseteam.mealticketsystemwas.domain.auth.entity")
@EnableJpaRepositories(basePackages = "yuseteam.mealticketsystemwas.domain.auth.repository")
public class MealTicketSystemWasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MealTicketSystemWasApplication.class, args);
    }

}
