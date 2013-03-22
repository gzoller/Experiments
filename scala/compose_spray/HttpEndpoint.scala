import scala.concurrent.Future
import akka.actor.{ Actor, ActorRef, ActorContext }
import spray.http.{ HttpRequest, HttpResponse }
import spray.http.HttpMethods._
import spray.routing._
import scala.concurrent.duration._
import scala.language.postfixOps // compiler-recommended import
import scala.concurrent.ExecutionContext.Implicits.global
import spray.http.MediaTypes._


// Define an extension
trait EpExt {
	val route : RequestContext => Unit
}

// Define the base API
trait BaseApi extends HttpService {
	val route : RequestContext => Unit = 
		get {
			path("ping") {
				respondWithMediaType(`application/json`) {
					complete{ 
						Future( "pong" ) 
					}
				}
			}
		}
}
