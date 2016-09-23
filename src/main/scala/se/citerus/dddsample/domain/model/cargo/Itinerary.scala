package se.citerus.dddsample.domain.model.cargo

import java.util.{Collections, Date}

import se.citerus.dddsample.domain.model.handling.HandlingEvent
import se.citerus.dddsample.domain.model.handling.HandlingEvent.Type._
import se.citerus.dddsample.domain.model.location.{Location, Locations}

import scala.collection.JavaConverters._

/**
  * Created by dan on 2016-09-23.
  */
case class Itinerary(legs: java.util.List[Leg]) {

  require(legs != null, "Legs cannot be null")
  require(!legs.contains(null), "Legs cannot have null elements")

  def this() {
    this(Collections.emptyList())
  }

  def isExpected(event: HandlingEvent): Boolean = {
    if (legs.isEmpty) return true

    event.`type`() match {
      case RECEIVE =>
        legs.get(0).loadLocation === event.location
      case LOAD =>
        legs.asScala.exists(leg => leg.loadLocation === event.location && leg.voyage.sameIdentityAs(event.voyage))
      case UNLOAD =>
        legs.asScala.exists(leg => leg.unloadLocation === event.location && leg.voyage.sameIdentityAs(event.voyage))
      case CLAIM =>
        legs.asScala.last.unloadLocation === event.location
      case _ => true
    }
  }

  private[cargo]  def initialDepartureLocation: Location = {
    if (legs.isEmpty) Locations.UNKNOWN
    else legs.asScala.head.loadLocation
  }

  private[cargo]  def finalArrivalLocation: Location = {
    if (legs.isEmpty) Locations.UNKNOWN
    else legs.asScala.last.unloadLocation
  }

  private[cargo]  def finalArrivalDate: Date = {
    if (legs.isEmpty) {
      new Date(Long.MaxValue)
    }
    else {
      new Date(legs.asScala.last.unloadTime.getTime)
    }
  }
}
