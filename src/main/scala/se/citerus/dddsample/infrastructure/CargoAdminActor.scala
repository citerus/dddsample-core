package se.citerus.dddsample.infrastructure

import java.time.ZonedDateTime

import akka.actor.{Actor, ActorRef}
import akka.pattern.ask
import akka.util.Timeout
import se.citerus.dddsample.domain.model.location.UnLocode
import se.citerus.dddsample.infrastructure.BookingServiceActor.Book
import spray.routing.HttpService

import scala.concurrent.Future

import scala.concurrent.duration._

class CargoAdminActor(val bookingServiceActor: ActorRef) extends Actor with CargoAdminResource {

  def actorRefFactory = context
  def receive = runRoute(cargoAdminRoute)

}

trait CargoAdminResource extends HttpService { this: Actor =>

  import spray.httpx.marshalling.BasicMarshallers._

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(5.seconds)

  val bookingServiceActor: ActorRef

  val cargoAdminRoute =
    path("admin" / "book") {
      post {
        parameters('origin, 'destination, 'arrivalTime) { (origin, destination, arrivalTime) =>
          complete(book(origin, destination, arrivalTime).map(_.toString))
        }
      }
    }

  def book(origin: String, destination: String, arrivalTime: String): Future[Any] =
    bookingServiceActor ? Book(new UnLocode(origin), new UnLocode(destination), ZonedDateTime.parse(arrivalTime))

}