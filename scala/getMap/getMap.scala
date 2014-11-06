package com.greg

object getMap {
	implicit def fromOptionToConvertedVal[T](o:Option[T]) = new {
		def getMap[R] (doWithSomeVal:(T) => R) = new {
			def orElse(handleNone: => R) = o match {
				case Some(value) => doWithSomeVal(value)
				case None => handleNone
			}
		}
	}
}


// use:  someOptInt getMap (_*3) orElse 0
