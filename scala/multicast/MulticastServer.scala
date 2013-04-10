package com.br

import java.net._
import java.util.Date
import scala.collection.concurrent.TrieMap
import scala.util.Try
import scala.concurrent.duration._

class MulticastServer(port:Int, deadInterval:Duration) extends Thread {

  var socket = Try( new DatagramSocket(port) ).toOption
  
  // If socket isn't defined here, we should do something dramatic to notify someone.
  
  def die = {
    socket.fold()(_.close)
    socket = None 
    }
  
  override def run {
    try {
	    while( socket.isDefined ) {
	    	val packet = new DatagramPacket(new Array[Byte](255),255)
    		socket.get.receive(packet)
	    	val msg = new String(packet.getData)
	    	println("Received: "+msg)
	    	NodeRoster.ImAlive( msg )
	    	NodeRoster.pruneTheDead(deadInterval)
	    }
    } catch {
      case closed:SocketException => // do nothing... socket closed... part of normal termination
      case y:Throwable => { y.printStackTrace; die }
    }
  }
}

case class Client(myPort:Int, msgPort:Int) {
  val broadcastIp = InetAddress.getByAddress(Array[Byte](255.toByte,255.toByte,255.toByte,255.toByte))
  
  def !!( msg:String ) =
    Try({
		val socket = new DatagramSocket(myPort)
    	val packet = new DatagramPacket(msg.getBytes,msg.getBytes.size, broadcastIp, msgPort)
		socket.send(packet)
		socket.close
		1 // ok!
    }).toOption
}

object NodeRoster {
	val alive = TrieMap[String, Date]()

	def ImAlive( nodeAkkaUri : String ) = alive.put( nodeAkkaUri, new Date() )
	def pruneTheDead( deadInterval:Duration ) = {
	  val longAgo = new Date( (new Date).getTime - deadInterval.toMillis )  // "deadInterval" time before now
	  alive.retain( (n,d) => d.after(longAgo))  /// keep only those we've seen recently
	}
}
