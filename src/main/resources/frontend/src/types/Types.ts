export type CargoTrackingDTO = {
  trackingId: string;
  statusText: string;
  origin: Unlocode;
  destination: Unlocode;
  eta: string;
  nextExpectedActivity: string;
  isMisdirected: boolean;
  isMisrouted: boolean;
  handlingEvents: HandlingEventDTO[];
  itinerary: CargoLegDTO[];
};

export type HandlingEventDTO = {
  location: string;
  time: string;
  type: string;
  voyageNumber: string;
  isExpected: boolean;
  description: string;
};

export type CargoLegDTO = {
  voyageNumber: string;
  from: string;
  to: string;
  loadTime: string;
  unloadTime: string;
};

export type RouteCandidateDTO = {
  legs: CargoLegDTO[];
};

export type RouteCandidate = {
  legs: CargoLeg[];
};

export type Unlocode = {
  unLocode: string;
  name: string;
};

export type CargoTracking = {
  trackingId: string;
  statusText: string;
  origin: Unlocode;
  destination: Unlocode;
  eta: Date | undefined;
  nextExpectedActivity: string;
  isMisdirected: boolean;
  isMisrouted: boolean;
  handlingEvents: HandlingEvent[];
  itinerary: CargoLeg[];
};

export type HandlingEvent = {
  location: string;
  time: Date;
  type: string;
  voyageNumber: string;
  isExpected: boolean;
  description: string;
};

export type CargoLeg = {
  voyageNumber: string;
  from: string;
  to: string;
  loadTime: Date;
  unloadTime: Date;
};

export type ApiError = {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
};
