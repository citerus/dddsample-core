package se.citerus.dddsample.interfaces.tracking.ws;

/**
 * A data-transport object class represnting a view of a HandlingEvent owned by a Cargo.
 * Used by the REST API for public cargo tracking.
 */
public class HandlingEventDTO {

    public final String location;
    public final String time;
    public final String type;
    public final String voyageNumber;
    public final boolean isExpected;
    public final String description;

    public HandlingEventDTO(String location, String time, String type, String voyageNumber, boolean isExpected, String description) {
        this.location = location;
        this.time = time;
        this.type = type;
        this.voyageNumber = voyageNumber;
        this.isExpected = isExpected;
        this.description = description;
    }
}
