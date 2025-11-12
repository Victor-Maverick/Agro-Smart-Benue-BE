package dev.gagnon.bfpcapi;

import dev.gagnon.bfpcapi.security.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class BfpcApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BfpcApiApplication.class, args);
    }

}
