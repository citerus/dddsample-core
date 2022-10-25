CREATE TABLE Location(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    unLocode VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(128) NOT NULL
);

CREATE TABLE Leg(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    voyage INT,
    cargo INT,
    loadLocation INT,
    loadTime TIMESTAMP,
    unloadLocation INT,
    unloadTime TIMESTAMP
);

CREATE TABLE Voyage(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    voyageNumber VARCHAR(64) UNIQUE
);

CREATE TABLE CarrierMovement(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    arrivalLocation INT,
    arrivalTime TIMESTAMP,
    departureLocation INT,
    departureTime TIMESTAMP,
    voyage INT
);

CREATE TABLE HandlingEvent(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    voyage INT null,
    location INT,
    cargo INT,
    completionTime TIMESTAMP,
    registrationTime TIMESTAMP,
    type VARCHAR(12)
);

CREATE TABLE Cargo(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    origin INT,
    trackingId VARCHAR(32) UNIQUE,
    -- delivery
    misdirected BIT,
    eta TIMESTAMP,
    calculatedAt TIMESTAMP,
    isUnloadedAtDestination BIT,
    routingStatus VARCHAR(12),
    transportStatus VARCHAR(16),
    currentVoyage INT,
    lastKnownLocation INT,
    lastEvent INT,
    -- next expected activity (calculated in runtime?)
    -- route spec
    routeSpecification INT
    -- itinerary (legs)
);

CREATE TABLE RouteSpecification(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    origin INT,
    destination INT,
    arrivalDeadline TIMESTAMP
);
