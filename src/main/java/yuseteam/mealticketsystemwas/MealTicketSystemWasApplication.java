package yuseteam.mealticketsystemwas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MealTicketSystemWasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MealTicketSystemWasApplication.class, args);
    }

}
