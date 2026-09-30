package com.parkit.parkingsystem.integration.dao;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

public class ParkingSpotDAOTest {
    private static ParkingSpotDAO parkingSpotDAO;
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static DataBasePrepareService dataBasePrepareService;

    @BeforeAll
    static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    void setUpPerTest() {
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterEach
    void tearDown() {
        Connection connection = null;
        try {
            connection = dataBaseTestConfig.getConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE parking SET available = true");
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }

    @Test
    void shouldReturnNextAvailableCarParkingSpot() {
        int parkingSpotId = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertTrue(parkingSpotId > 0);
    }

    @Test
    void shouldReturnNextAvailableBikeParkingSpot() {
        int parkingSpotId = parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE);
        assertTrue(parkingSpotId > 0);
    }

    @Test
    void shouldReturnFalseWhenUpdatingUnknownParkingSpot() {
        ParkingSpot parkingSpot = new ParkingSpot(999, ParkingType.CAR, false);
        boolean result = parkingSpotDAO.updateParking(parkingSpot);
        assertFalse(result);
    }

    @Test
    void shouldUpdateParkingSpotAvailability() {
        int parkingSpotId = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        ParkingSpot parkingSpot = new ParkingSpot(parkingSpotId, ParkingType.CAR, false);
        boolean result = parkingSpotDAO.updateParking(parkingSpot);
        assertTrue(result);
        // Check that the parking spot is no longer available
        int nextAvailableSpot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertNotEquals(parkingSpotId, nextAvailableSpot);
    }
}
