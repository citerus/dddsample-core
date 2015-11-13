package se.citerus.dddsample.interfaces.booking.facade;

import se.citerus.dddsample.interfaces.booking.facade.dto.CargoRoutingDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.LocationDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.RouteCandidateDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

/**
 * This facade shields the domain layer - model, services, repositories -
 * from concerns about such things as the user interface.
 */
public interface BookingServiceFacade {

  String bookNewCargo(String origin, String destination, Date arrivalDeadline) throws RemoteException;

  CargoRoutingDTO loadCargoForRouting(String trackingId) throws RemoteException;

  void assignCargoToRoute(String trackingId, RouteCandidateDTO route) throws RemoteException;

  void changeDestination(String trackingId, String destinationUnLocode) throws RemoteException;

  List<RouteCandidateDTO> requestPossibleRoutesForCargo(String trackingId) throws RemoteException;

  List<LocationDTO> listShippingLocations() throws RemoteException;

  List<CargoRoutingDTO> listAllCargos() throws RemoteException;

}
