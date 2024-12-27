package zerobase.weather_mission;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class WeatherMissionApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherMissionApplication.class, args);
	}

}
