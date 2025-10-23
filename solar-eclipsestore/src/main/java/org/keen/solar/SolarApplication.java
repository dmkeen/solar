package org.keen.solar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.nio.file.Path;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class SolarApplication {

    @Value("${app.eclipse-store.root-path}")
    private String rootPath;

    @Bean
    public Path rootStoragePath() {
        return Path.of(rootPath);
    }

	public static void main(String[] args) {
		SpringApplication.run(SolarApplication.class, args);
	}

}
