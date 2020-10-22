package pl.skyterix.sadsky;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
class SadSkyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SadSkyApplication.class, args);
    }

}
