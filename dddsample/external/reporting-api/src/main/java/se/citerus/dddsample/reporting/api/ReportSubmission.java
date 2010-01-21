package se.citerus.dddsample.reporting.api;

import javax.ws.rs.*;

@Consumes({"application/json", "application/xml"})
@Path("/")
public interface ReportSubmission {

  @PUT
  @Path("/cargo")
  void submitCargoDetails(CargoDetails cargoDetails);

  @POST
  @Path("/cargo/{trackingId}/handled")
  void submitHandling(@PathParam("trackingId") String trackingId, Handling handling);

}
