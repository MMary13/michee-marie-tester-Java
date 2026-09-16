package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean discount) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime());
        }
        //total duration
        Duration duration = Duration.between(ticket.getInTime(), ticket.getOutTime());

        //30 minutes are free
        final long FREE_MINUTES = 30;

        if (duration.toMinutes() <= FREE_MINUTES) {
            ticket.setPrice(0);
            return;
        } else {
            //remove the free minutes
            Duration billableDuration = duration.minusMinutes(FREE_MINUTES);

            //convert remaining duration to hours
            double durationInHours = billableDuration.toMinutes() / 60.0;
            if (ticket.getParkingSpot() == null ||
                    ticket.getParkingSpot().getParkingType() == null) {
                throw new IllegalArgumentException("Unknown Parking Type");
            }
            switch (ticket.getParkingSpot()
                    .getParkingType()) {
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
}