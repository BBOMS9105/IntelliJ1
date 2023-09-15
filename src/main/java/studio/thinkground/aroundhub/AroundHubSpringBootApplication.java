package studio.thinkground.aroundhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class AroundHubSpringBootApplication {

  public static void main(String[] args) {
    SpringApplication.run(AroundHubSpringBootApplication.class, args);
    System.out.println("Server is now listening on port 8080.");
  }

}