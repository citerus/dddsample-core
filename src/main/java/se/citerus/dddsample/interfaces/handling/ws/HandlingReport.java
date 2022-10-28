package se.citerus.dddsample.interfaces.handling.ws;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class HandlingReport {
    @JsonProperty(required = true)
    public LocalDateTime completionTime;

    @JsonProperty(required = true)
    public List<String> trackingIds;

    @JsonProperty(required = true)
    public String type;

    @JsonProperty(required = true)
    public String unLocode;

    public String voyageNumber;

    public HandlingReport(LocalDateTime completionTime, List<String> trackingIds, String type, String unLocode, String voyageNumber) {
        this.completionTime = completionTime;
        this.trackingIds = trackingIds;
        this.type = type;
        this.unLocode = unLocode;
        this.voyageNumber = voyageNumber;
    }

    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(LocalDateTime completionTime) {
        this.completionTime = completionTime;
    }

    public List<String> getTrackingIds() {
        return trackingIds;
    }

    public void setTrackingIds(List<String> trackingIds) {
        this.trackingIds = trackingIds;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnLocode() {
        return unLocode;
    }

    public void setUnLocode(String unLocode) {
        this.unLocode = unLocode;
    }

    public String getVoyageNumber() {
        return voyageNumber;
    }

    public void setVoyageNumber(String voyageNumber) {
        this.voyageNumber = voyageNumber;
    }
}
