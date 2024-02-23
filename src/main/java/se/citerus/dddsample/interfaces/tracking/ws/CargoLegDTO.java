package se.citerus.dddsample.interfaces.tracking.ws;

public class CargoLegDTO {
    public final String voyageNumber;
    public final String from;
    public final String to;
    public final String loadTime;
    public final String unloadTime;

     public CargoLegDTO(final String voyageNumber, final String from, final String to, String loadTime, String unloadTime) {
        this.voyageNumber = voyageNumber;
        this.from = from;
        this.to = to;
        this.loadTime = loadTime;
        this.unloadTime = unloadTime;
    }
}
