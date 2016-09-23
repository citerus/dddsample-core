package se.citerus.dddsample.infrastructure

import java.time.ZonedDateTime
import java.util.UUID

import akka.actor.Actor
import se.citerus.dddsample.domain.model.cargo.TrackingId
import se.citerus.dddsample.domain.model.location.UnLocode
import se.citerus.dddsample.infrastructure.BookingServiceActor.{Book, BookingRegistered}

class BookingServiceActor extends Actor {

  override def receive: Receive = {
    case Book(origin, destination, arrivalTime) =>
      sender() ! BookingRegistered(new TrackingId(UUID.randomUUID().toString))
  }
}

object BookingServiceActor {
  case class Book(origin: UnLocode, destination: UnLocode, arrivalTime: ZonedDateTime)
  case class BookingRegistered(trackingId: TrackingId)
}
