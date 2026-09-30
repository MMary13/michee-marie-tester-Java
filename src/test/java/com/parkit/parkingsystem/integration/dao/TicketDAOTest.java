package com.parkit.parkingsystem.integration.dao;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TicketDAOTest {
    private static TicketDAO ticketDAO;
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static DataBasePrepareService dataBasePrepareService;

    @BeforeAll
    static void setUp() {
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
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
    void shouldReturnNullWhenTicketDoesNotExist() {
        // given: no ticket has been created yet
        // when
        Ticket ticket = ticketDAO.getTicket("UNKNOWN");

        // then
        assertNull(ticket);
    }

    @Test
    void shouldReturnZeroWhenVehicleHasNoTicket() {
        // given: no ticket has been created yet
        // when
        int nbTicket = ticketDAO.getNbTicket("UNKNOWN");

        // then
        assertEquals(0, nbTicket);
    }

    @Test
    void shouldSaveTicketWithoutOutTime() {
        // given
        Ticket ticket = createTicket(null);
        // when: create ticket
        boolean result = ticketDAO.saveTicket(ticket);

        // then: check number of Tickets
        int nbTicket = ticketDAO.getNbTicket(ticket.getVehicleRegNumber());
        assertEquals(1, nbTicket);
        assertFalse(result);
    }

    @Test
    void shouldReturnTicketWithoutOutTime() {
        // given
        Ticket ticket = createTicket(null);

        // when: create ticket
        ticketDAO.saveTicket(ticket);

        // then: do validations
        Ticket result = ticketDAO.getTicket(ticket.getVehicleRegNumber());
        assertNotNull(result);
        assertEquals(ticket.getVehicleRegNumber(), result.getVehicleRegNumber());
        assertEquals(ticket.getPrice(), result.getPrice());
        assertEquals(ticket.getInTime(), result.getInTime());
        assertNull(result.getOutTime());
        assertNotNull(result.getParkingSpot());
        assertEquals(ticket.getParkingSpot().getId(), result.getParkingSpot().getId());
        assertEquals(ticket.getParkingSpot().getParkingType(), result.getParkingSpot().getParkingType());
    }

    @Test
    void shouldSaveAndReturnTicketWithOutTime() {
        // given
        LocalDateTime inTime = LocalDateTime.of(2026, 9, 30, 10, 0);
        LocalDateTime outTime = LocalDateTime.of(2026, 9, 30, 12, 0);
        Ticket ticket = createTicket("ABCDEF", inTime, outTime);

        // when
        ticketDAO.saveTicket(ticket);

        //then: do validations
        Ticket result = ticketDAO.getTicket(ticket.getVehicleRegNumber());
        assertNotNull(result);
        assertEquals(ticket.getVehicleRegNumber(), result.getVehicleRegNumber());
        assertEquals(ticket.getPrice(), result.getPrice());
        assertEquals(inTime, result.getInTime());
        assertEquals(outTime, result.getOutTime());
    }

    @Test
    void shouldReturnNumberOfTicketsForVehicle() {
        // given: two tickets
        Ticket firstTicket = createTicket("ABCDEF", LocalDateTime.of(2026, 9, 30, 8, 0), null);
        Ticket secondTicket = createTicket("ABCDEF", LocalDateTime.of(2026, 9, 30, 10, 0), null);

        // when
        ticketDAO.saveTicket(firstTicket);
        ticketDAO.saveTicket(secondTicket);

        // then: do validations
        int nbTicket = ticketDAO.getNbTicket("ABCDEF");
        assertEquals(2, nbTicket);
    }

    @Test
    void shouldUpdateTicket() {
        // given
        Ticket ticket = createTicket(null);
        ticketDAO.saveTicket(ticket);

        // when: a ticket is updated
        Ticket savedTicket = ticketDAO.getTicket(ticket.getVehicleRegNumber());
        LocalDateTime outTime = LocalDateTime.of(2026, 9, 30, 12, 0);
        savedTicket.setPrice(2.25);
        savedTicket.setOutTime(outTime);
        boolean result = ticketDAO.updateTicket(savedTicket);

        // then: do validation
        assertTrue(result);
        Ticket updatedTicket = ticketDAO.getTicket(ticket.getVehicleRegNumber());
        assertNotNull(updatedTicket);
        assertEquals(2.25, updatedTicket.getPrice());
        assertEquals(outTime, updatedTicket.getOutTime());
    }

    @Test
    void shouldReturnFalseWhenSavingInvalidTicket() {
        // given
        Ticket ticket = createTicket(null);
        ticket.setInTime(null);

        // when
        boolean result = ticketDAO.saveTicket(ticket);

        // then
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenUpdatingTicketWithoutOutTime() {
        // given
        Ticket ticket = createTicket(null);
        ticketDAO.saveTicket(ticket);
        Ticket savedTicket = ticketDAO.getTicket(ticket.getVehicleRegNumber());

        // when: update with outTime null
        savedTicket.setOutTime(null);
        boolean result = ticketDAO.updateTicket(savedTicket);

        // then
        assertFalse(result);
    }

    private Ticket createTicket(LocalDateTime outTime) {
        return createTicket("ABCDEF", LocalDateTime.of(2026, 9, 30, 10, 0), outTime);
    }

    private Ticket createTicket(String vehicleRegNumber, LocalDateTime inTime, LocalDateTime outTime) {

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(2.25);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);

        return ticket;
    }
}

