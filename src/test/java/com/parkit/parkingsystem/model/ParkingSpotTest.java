package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParkingSpotTest {
    @Test
    void shouldCreateParkingSpotWithGivenValues() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        assertEquals(1, parkingSpot.getId());
        assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
        assertTrue(parkingSpot.isAvailable());
    }

    @Test
    void shouldSetAndGetId() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        parkingSpot.setId(2);
        assertEquals(2, parkingSpot.getId());
    }

    @Test
    void shouldSetAndGetParkingType() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        parkingSpot.setParkingType(ParkingType.BIKE);
        assertEquals(ParkingType.BIKE, parkingSpot.getParkingType());
    }

    @Test
    void shouldSetAndGetAvailability() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        parkingSpot.setAvailable(false);
        assertFalse(parkingSpot.isAvailable());
    }

    @Test
    void shouldBeEqualWhenParkingSpotsHaveSameNumber() {
        ParkingSpot parkingSpot1 = new ParkingSpot(1, ParkingType.CAR, true);
        ParkingSpot parkingSpot2 = new ParkingSpot(1, ParkingType.BIKE, false);
        assertEquals(parkingSpot1, parkingSpot2);
    }

    @Test
    void shouldNotBeEqualWhenParkingSpotsHaveDifferentNumbers() {
        ParkingSpot parkingSpot1 = new ParkingSpot(1, ParkingType.CAR, true);
        ParkingSpot parkingSpot2 = new ParkingSpot(2, ParkingType.CAR, true);
        assertNotEquals(parkingSpot1, parkingSpot2);
    }

}
