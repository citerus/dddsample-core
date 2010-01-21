package com.reporting;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Produces({"application/json", "application/pdf", "application/xml"})
@Path("/")
public interface ReportingService {

  @GET
  @Path("/cargo/{trackingId}.{format}")
  public Response getCargoReport(@PathParam("trackingId") String trackingId, @PathParam("format") String format);

  @GET
  @Path("/voyage/{voyageNumber}.{format}")
  public Response getVoyageReport(@PathParam("voyageNumber") String voyageNumber, @PathParam("format") String format);

}
