package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    void calculateFareCar(){
        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.minusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0.5 * Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }

    @Test
    void calculateFareBike(){
        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.minusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0.5 * Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
    }

    @Test
    void calculateFareUnknownType(){
        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.minusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    void calculateFareBikeWithFutureInTime(){
        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.plusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    void calculateFareBikeWithLessThanOneHourParkingTime(){
        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.minusMinutes(45);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.25 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    void calculateFareCarWithLessThanOneHourParkingTime(){
        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.minusMinutes(45);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.25 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    void calculateFareCarWithMoreThanADayParkingTime(){
        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.minusDays(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((23.5 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    void calculateFareCarWithLessThan30minutesParkingTime() {
        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.minusMinutes(29);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( 0 , ticket.getPrice());
    }

    @Test
    void calculateFareBikeWithLessThan30minutesParkingTime() {
        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.minusMinutes(29);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( 0 , ticket.getPrice());
    }

    @Test
    void calculateFareCarWithDiscount() {
        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.minusMinutes(45);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        boolean discount = true;
        fareCalculatorService.calculateFare(ticket,discount);
        assertEquals((0.25 * Fare.CAR_RATE_PER_HOUR * 0.95), ticket.getPrice());
    }

    @Test
    void calculateFareBikeWithDiscount() {
        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.minusMinutes(45);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        boolean discount = true;
        fareCalculatorService.calculateFare(ticket,discount);
        assertEquals((0.25 * Fare.BIKE_RATE_PER_HOUR * 0.95), ticket.getPrice());

    }
}
