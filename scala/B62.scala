package com.greg 

// Base62 encoding--suitable for urls

object B62 {
	private val chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toArray
  
	// Generate a 5/6-character, Base62 character code.
	// Although extremely unlikely, duplicate codes *can* happen!  Plan accordingly.
	def genCode( shift:Int = 32 ) = 
		encodeBase10((java.lang.Math.random() * Long.MaxValue).toLong >> shift)
  
	def encodeBase10(b10:Long) = {
		val s:Stream[Long] = {
		def loop( v:Long ) : Stream[Long] = v #:: loop(v/62)
			loop(b10)
		}
		s.takeWhile( b => b > 0 ).map( s => chars.charAt( (s%62).toInt ) ).reverse.mkString
	}
  
  	// NOTE: Not useful if we've shifted the input a la genCode!
	def decodeBase62(b62:String) =
		b62.toCharArray.foldRight( (0L,1L) )( (ch,a) => (a._1+chars.indexOf(ch)*a._2, a._2*62) )._1
}