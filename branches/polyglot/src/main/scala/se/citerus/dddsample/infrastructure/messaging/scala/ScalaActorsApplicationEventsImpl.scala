package se.citerus.dddsample.infrastructure.messaging.scala

import application.{HandlingEventService, CargoInspectionService, ApplicationEvents}
import domain.model.handling.HandlingEvent
import domain.model.cargo.Cargo
import interfaces.handling.HandlingEventRegistrationAttempt

class ScalaActorsApplicationEventsImpl(
  cargoInspectionActor: CargoInspectionActor,
  handlingEventAttemptActor: HandlingEventRegistrationActor)
extends ApplicationEvents {

  def cargoWasHandled(event: HandlingEvent) =
    cargoInspectionActor ! CargoWasHandled(event)

  def cargoWasMisdirected(cargo: Cargo) =
    Console.println("Cargo was misdirected: " + cargo)

  def cargoHasArrived(cargo: Cargo) =
    Console.println("Cargo has arrived: " + cargo)

  def receivedHandlingEventRegistrationAttempt(attempt: HandlingEventRegistrationAttempt) =
    handlingEventAttemptActor ! HandlingEventRegistrationAttempted(attempt)

}