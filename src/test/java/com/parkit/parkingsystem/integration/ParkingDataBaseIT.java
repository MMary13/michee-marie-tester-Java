package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    void setUpPerTest() throws Exception {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
        assertEquals(0, ticketDAO.getNbTicket("ABCDEF"));
    }



    @Test
    void testParkingACar() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        parkingService.processIncomingVehicle();

        // get ticket from database
        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        // do validations
        assertNotNull(ticket);
        assertEquals("ABCDEF", ticket.getVehicleRegNumber());
        assertEquals("CAR", ticket.getParkingSpot().getParkingType().toString());
    }


    @Test
    void testParkingLotExit() {
        LocalDateTime outTime = LocalDateTime.of(2026, 9, 28, 20, 0, 0);
        LocalDateTime inTime = outTime.minusHours(2);

        Clock fixedClock = Clock.fixed(
                outTime.atZone(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault()
        );

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, fixedClock);
        // given
        Ticket newTicket = new Ticket();
        newTicket.setVehicleRegNumber("ABCDEF");
        newTicket.setParkingSpot(new ParkingSpot(2, ParkingType.CAR, true));
        newTicket.setInTime(inTime);
        ticketDAO.saveTicket(newTicket);
        // when
        parkingService.processExitingVehicle();
        // then
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        //do validation
        assertNotNull(ticket);
        assertEquals("ABCDEF", ticket.getVehicleRegNumber());
        assertEquals(2.25, ticket.getPrice());
    }

    @Test
    void testParkingLotExitRecurringUser() {
        LocalDateTime outTime = LocalDateTime.of(2026, 9, 28, 20, 0, 0);
        LocalDateTime inTime = outTime.minusHours(2);

        Clock fixedClock = Clock.fixed(
                outTime.atZone(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault()
        );

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, fixedClock);
        // given previous ticket
        Ticket oldTicket = new Ticket();
        ticketDAO.getTicket("ABCDEF");
        oldTicket.setVehicleRegNumber("ABCDEF");
        oldTicket.setParkingSpot(new ParkingSpot(2, ParkingType.CAR, true));
        oldTicket.setOutTime(LocalDateTime.of(2026, 9, 23, 12, 0));
        oldTicket.setInTime(oldTicket.getOutTime().minusHours(2));
        ticketDAO.saveTicket(oldTicket);
        // when
        Ticket newTicket = new Ticket();
        newTicket.setVehicleRegNumber("ABCDEF");
        newTicket.setParkingSpot(new ParkingSpot(2, ParkingType.CAR, true));
        newTicket.setInTime(inTime);
        ticketDAO.saveTicket(newTicket);
        parkingService.processExitingVehicle();
        // then
        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        //do validation
        assertNotNull(ticket);
        assertEquals("ABCDEF", ticket.getVehicleRegNumber());
        assertEquals(2.14, ticket.getPrice());
    }

}