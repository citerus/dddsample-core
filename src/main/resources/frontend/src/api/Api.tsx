import {
  CargoLegDTO,
  CargoTracking,
  CargoTrackingDTO,
  RouteCandidate,
  RouteCandidateDTO,
} from "../types/Types";

function convertTrackingDTO(cargo: CargoTrackingDTO): CargoTracking {
  return {
    ...cargo,
    eta: cargo.eta !== "Unknown" ? new Date(cargo.eta) : undefined,
    handlingEvents: cargo.handlingEvents.map((he) => ({
      ...he,
      time: new Date(he.time),
    })),
    itinerary: cargo.itinerary.map((it) => ({
      ...it,
      loadTime: new Date(it.loadTime),
      unloadTime: new Date(it.unloadTime),
    })),
  };
}

export async function getAllCargo(): Promise<CargoTracking[]> {
  const response = await fetch(`/dddsample/api/cargo/`);
  if (!response.ok) {
    throw await response.json();
  }
  const cargoList = (await response.json()) as CargoTrackingDTO[];
  return cargoList.map(convertTrackingDTO);
}

export async function getCargoById(trackingId: string): Promise<CargoTracking> {
  const response = await fetch(`/dddsample/api/cargo/${trackingId}`);
  if (!response.ok) {
    if (response.status === 404) {
      throw {
        status: response.status,
        message: `Unable to find ${trackingId}`,
        error: "Cargo not found",
      };
    }
    throw await response.json();
  }
  const cargo = (await response.json()) as CargoTrackingDTO;
  return convertTrackingDTO(cargo);
}

export async function changeCargoDestination(
  trackingId: string,
  newDestination: string
) {
  const response = await fetch(
    `/dddsample/api/cargo/${trackingId}/destination`,
    {
      method: "PUT",
      body: JSON.stringify({
        unlocode: newDestination,
      }),
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
  if (!response.ok) {
    throw await response.json();
  }
  return response;
}

export async function createNewCargo(newCargo: {
  origin: string;
  destination: string;
  arrivalDeadline: string;
}): Promise<string> {
  const response = await fetch(`/dddsample/api/cargo/`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(newCargo),
  });

  const { trackingId } = await response.json();
  return trackingId;
}

export async function getPossibleRoutes(
  trackingId: string
): Promise<RouteCandidate[]> {
  const response = await fetch(
    `/dddsample/api/cargo/${trackingId}/routeCandidates`
  );
  const routes = (await response.json()) as RouteCandidateDTO[];
  return routes.map((routeCandidate) => ({
    legs: routeCandidate.legs.map((leg) => ({
      ...leg,
      loadTime: new Date(leg.loadTime),
      unloadTime: new Date(leg.unloadTime),
    })),
  }));
}

export async function setItinerary(trackingId: string, legs: CargoLegDTO[]) {
  return await fetch(`/dddsample/api/cargo/${trackingId}/itinerary`, {
    method: "PUT",
    body: JSON.stringify(legs),
    headers: {
      "Content-Type": "application/json",
    },
  });
}
