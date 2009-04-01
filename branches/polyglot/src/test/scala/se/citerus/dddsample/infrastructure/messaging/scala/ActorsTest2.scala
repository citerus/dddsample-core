package se.citerus.dddsample.infrastructure.messaging.scala

import application.{CargoInspectionService, HandlingEventService}
import domain.model.cargo.TrackingId
import domain.model.handling.HandlingEvent
import domain.model.handling.HandlingEvent.Type
import domain.model.location.UnLocode
import domain.model.voyage.VoyageNumber
import interfaces.handling.HandlingEventRegistrationAttempt

import java.util.Date
import org.scalatest.junit.JUnit3Suite
import org.scalatest.prop.Checkers

object HandlingEventServiceStub extends HandlingEventService {
  def registerHandlingEvent(completionTime: Date, trackingId: TrackingId, voyageNumber: VoyageNumber, unLocode: UnLocode, `type` : Type) = {
    Console.println("--- Registering handling event ---")
  }
}

object CargoInspectionServiceStub extends CargoInspectionService {
  def inspectCargo(trackingId: TrackingId) = {
    Console.println("--- Inspecting cargo ---")
  }
}

class ActorsTest2 extends JUnit3Suite with Checkers {

  val inspectionActor = new CargoInspectionActor(CargoInspectionServiceStub)
  val handlingActor = new HandlingEventRegistrationActor(HandlingEventServiceStub)
  val applicationEvents = new ScalaActorsApplicationEventsImpl(inspectionActor, handlingActor)


  def testHandling() {
    inspectionActor.start()
    handlingActor.start()
    applicationEvents.receivedHandlingEventRegistrationAttempt(new HandlingEventRegistrationAttempt(
      new Date(),
      new Date(),
      new TrackingId("DEF"),
      new VoyageNumber("123"),
      HandlingEvent.Type.LOAD,
      new UnLocode("SESTO")
    ))
  }

}