package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TicketTest {

    @Test
    void shouldSetAndGetId() {
        Ticket ticket = new Ticket();
        ticket.setId(1);
        assertEquals(1, ticket.getId());
    }

    @Test
    void shouldSetAndGetParkingSpot() {
        Ticket ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        ticket.setParkingSpot(parkingSpot);
        assertEquals(parkingSpot, ticket.getParkingSpot());
    }

    @Test
    void shouldSetAndGetVehicleRegNumber() {
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("ABCDEF");
        assertEquals("ABCDEF", ticket.getVehicleRegNumber());
    }

    @Test
    void shouldSetAndGetPrice() {
        Ticket ticket = new Ticket();
        ticket.setPrice(2.25);
        assertEquals(2.25, ticket.getPrice());
    }

    @Test
    void shouldSetAndGetInTime() {
        Ticket ticket = new Ticket();
        LocalDateTime inTime = LocalDateTime.of(2026, 9, 23, 10, 0);
        ticket.setInTime(inTime);
        assertEquals(inTime, ticket.getInTime());
    }

    @Test
    void shouldSetAndGetOutTime() {
        Ticket ticket = new Ticket();
        LocalDateTime outTime = LocalDateTime.of(2026, 9, 23, 12, 0);
        ticket.setOutTime(outTime);
        assertEquals(outTime, ticket.getOutTime());
    }
}

