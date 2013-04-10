package com.br

import com.novus.salat.annotations._
import com.novus.salat._

import akka.actor.{ ActorSystem, Props }
import spray.can.server.{ HttpServer, ServerSettings }
import spray.io.{ServerSSLEngineProvider, SingletonHandler, IOExtension}
import spray.http.MediaTypes._

import spray.routing._
import scala.concurrent.Future
import spray.http.{ HttpRequest, HttpResponse }
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor._
import spray.routing.Directives._
import scala.concurrent.duration._

// set implicit context for DAO
/*
package object context {
	val CustomTypeHint = "type"
	implicit val ctx = new Context {
		val name = "JsonContext-As-Needed"
		override val typeHintStrategy = StringTypeHintStrategy(when = TypeHintFrequency.WhenNecessary, typeHint = CustomTypeHint)
	}
}
import context._
*/

object Extension extends App {
  
	val z = new MulticastServer(9099, 1 second)  // more realistically like 10 minutes or so
	z.start
	val x = Client(9098,9099)
	x !! "akka://1"
	println("1. "+NodeRoster.alive)
	Thread.sleep(2000)
	NodeRoster.pruneTheDead(1 second)
	println("2. "+NodeRoster.alive)
	x !! "akka://2"
	Thread.sleep(500)
	println("3. "+NodeRoster.alive)
	Thread.sleep(2000)
	//z.die
  
	val feed = Feed(
	    List(
	    	Video("a"),Video("b"),Video("c")
	        ),
	    List(
	    	Container("z",List("b","c","bogus_1")),
	    	Container("y",List("z","a","bogus_2","bogus_3")),
	    	Container("x",List("y","z"))
	        )
	    )
	    
	println(containerAssetsAreDefined(feed))
	
	val tp = new java.text.SimpleDateFormat("MM/dd/yyyy")
	println(tp.parse("02/01/2012").getTime)
	println(tp.parse("02/02/2012").getTime)
	println(tp.parse("02/03/2012").getTime)
	println(tp.parse("02/04/2012").getTime)

	def containerAssetsAreDefined( mf : Feed ) : List[(String,List[String])] = {  // List[(container_aid,List[missing_assets_aid])]
		implicit val allAssetIds = (mf.videos ++ mf.containers).map( _.assetId )
		mf.containers.collect{ case MissingAsset(c,missing) => (c,missing) }
	}
	private object MissingAsset {
		def unapply(c:Container)(implicit allAssetIds:List[String]):Option[(String,List[String])] = {
			val missing = c.kids.collect { case assetId:String if(!allAssetIds.contains(assetId)) => assetId }
			if( missing.size > 0 ) Some( (c.assetId, missing) )
			else None
		}
	}
}

trait Asset {
  val assetId : String
}
case class Video (
    val assetId : String
    ) extends Asset
case class Container(
    val assetId : String,
    val kids : List[String]
    ) extends Asset
case class Feed (
    val videos : List[Video],
    val containers : List[Container]
    )
