import akka.actor.{ ActorSystem, Props }
import com.mongodb.casbah.Imports._
import com.typesafe.config.ConfigFactory
import spray.can.server.{ HttpServer, ServerSettings }
import spray.io.{ServerSSLEngineProvider, SingletonHandler, IOExtension}
import spray.http.MediaTypes._

import spray.routing._
import scala.concurrent.Future
import spray.http.{ HttpRequest, HttpResponse }
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor._
import spray.routing.Directives._

object MainServer {
	def db( collectionName : String ) = MongoConnection(dbhost)(dbinst)(collectionName)
	private[service] val config = ConfigFactory.load
	private val dbhost = config getString "dbhost"
	private val dbinst = config getString "dbinst"
}

class EE() extends EpExt {
	val route = 
		get {
			path("hey") {
				respondWithMediaType(`application/json`) {
					complete{ 
						Future( "there" ) 
					}
				}
			}
		}
}

trait BRApp extends SimpleRoutingApp with BaseApi {
	def exts : List[EpExt]
	final val allRoutes = this.route :: exts.map(e => e.route)
	final val routes = allRoutes.reduceLeft(_ ~ _)
	startServer( interface="localhost", port=8080 )(routes)
}

object Main extends App with BRApp {
	def exts = List[EpExt]( new EE() )  // typically read & marshal from config
}
