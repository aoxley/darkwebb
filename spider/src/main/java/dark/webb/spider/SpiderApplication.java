package dark.webb.spider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpiderApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpiderApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpiderApplication.class, args);
	}
}
