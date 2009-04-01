package se.citerus.dddsample.infrastructure.messaging.scala


import actors.Actor
import actors.Actor._
import application.{CargoInspectionService, HandlingEventService, ApplicationEvents}
import domain.model.cargo.Cargo
import domain.model.handling.HandlingEvent
import interfaces.handling.HandlingEventRegistrationAttempt

case class CargoWasHandled(event: HandlingEvent)
case class HandlingEventRegistrationAttempted(attempt: HandlingEventRegistrationAttempt)

class HandlingEventRegistrationActor(handlingEventService: HandlingEventService) extends Actor {

  def act() {
    loop {
      react {
        case HandlingEventRegistrationAttempted(attempt) =>
          handlingEventService.registerHandlingEvent(
            attempt.getCompletionTime(),
            attempt.getTrackingId(),
            attempt.getVoyageNumber(),
            attempt.getUnLocode(),
            attempt.getType()
          )
      }
    }
  }

}

class CargoInspectionActor(cargoInspectionService: CargoInspectionService) extends Actor {

  def act() {
    loop {
      react {
        case CargoWasHandled(event) =>
          cargoInspectionService.inspectCargo(event.cargo().trackingId())
      }
    }
  }

}