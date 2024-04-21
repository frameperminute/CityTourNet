package it.unicam.cs.CityTourNet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class CityTourNetApplication {

	public static void main(String[] args) {
		SpringApplication.run(CityTourNetApplication.class, args);
	}

}
