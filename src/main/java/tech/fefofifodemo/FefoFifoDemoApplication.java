package tech.fefofifodemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FefoFifoDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(FefoFifoDemoApplication.class, args);
    }

}
