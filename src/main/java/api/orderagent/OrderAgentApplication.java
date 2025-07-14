package api.orderagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OrderAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderAgentApplication.class, args);
	}

}
