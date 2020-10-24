package pl.skyterix.sadsky;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * @author Skyte
 */
@SpringBootApplication
@EnableScheduling
public class SadSkyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SadSkyApplication.class, args);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Warsaw"));
    }

}
