package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean discount) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }
        //duration time in ms
        long durationInMillis =
                ticket.getOutTime().getTime() - ticket.getInTime().getTime();
        //duration time in hours
        double durationInHours =
                durationInMillis / (60.0 * 60 * 1000);

        //free duration in parking (30min = 1800 000 ms)
        final double MAX_FREE_TIME = 1800000.0;
        if (durationInMillis < MAX_FREE_TIME) {
            ticket.setPrice(0);
        } else {
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice(durationInHours * Fare.CAR_RATE_PER_HOUR);
                    break;
                }
                case BIKE: {
                    ticket.setPrice(durationInHours * Fare.BIKE_RATE_PER_HOUR);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown Parking Type");
            }
        }
        //set discount
        if (discount) {
            ticket.setPrice(ticket.getPrice()*0.95);
        }
    }

        public void calculateFare(Ticket ticket) {
            calculateFare(ticket, false);
        }
}