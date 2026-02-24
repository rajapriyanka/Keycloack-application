package com.flight;

import org.apache.logging.log4j.LogManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.apache.logging.log4j.Logger;

@SpringBootApplication
public class FlightReservationSystemApplication {
	private static final Logger logger = LogManager.getLogger(FlightReservationSystemApplication.class);

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(FlightReservationSystemApplication.class, args);
//		BookingService b = context.getBean(BookingService.class);
//		BookingService b2 = context.getBean(BookingService.class);
//		System.out.println(b);
//		System.out.println(b2);
//        context.getBean(SuccessMessage.class);

		logger.info("Application Started Hurray!");
	}

}
