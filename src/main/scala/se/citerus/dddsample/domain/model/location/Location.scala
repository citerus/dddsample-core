package se.citerus.dddsample.domain.model.location

import se.citerus.dddsample.domain.shared.Entity

case class Location(unLocode: UnLocode, name: String) extends Entity[Location] {
  require(unLocode != null)
  require(name != null)

  def this() {
    this(new UnLocode("XXXXX"), "Unknown location")
  }

  def sameIdentityAs(other: Location): Boolean = {
    this.unLocode.sameValueAs(other.unLocode)
  }

  def ===(other: Location): Boolean = sameIdentityAs(other)

  private val id: Long = 0L
}

object Location {
  val UNKNOWN = new Location(new UnLocode("XXXXX"), "Unknown location")
}