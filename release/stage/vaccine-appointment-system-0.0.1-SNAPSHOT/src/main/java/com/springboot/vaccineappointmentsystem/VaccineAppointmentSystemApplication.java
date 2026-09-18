package com.springboot.vaccineappointmentsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VaccineAppointmentSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(VaccineAppointmentSystemApplication.class, args);
	}

}
