package se.citerus.dddsample.interfaces.tracking.ws;

import java.util.Date;

public class CargoRegistrationDTO {
    public String origin;
    public String destination;
    public String arrivalDeadline;

    public CargoRegistrationDTO(String origin, String destination, String arrivalDeadline) {
        this.origin = origin;
        this.destination = destination;
        this.arrivalDeadline = arrivalDeadline;
    }
}
