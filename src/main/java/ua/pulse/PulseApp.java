package ua.pulse;

import com.vaadin.spring.annotation.EnableVaadin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
//@EnableScheduling
@EnableVaadin
@EntityScan("ua.pulse.entity")
@ComponentScan("ua.pulse")
@EnableAutoConfiguration
@EnableJpaRepositories("ua.pulse.repository")
@SpringBootApplication
public class PulseApp extends SpringBootServletInitializer{

	public static void main(String[] args) {
		 SpringApplication.run(PulseApp.class, args).registerShutdownHook();
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(PulseApp.class);
    }

}
