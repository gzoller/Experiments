package com.greg


//---- The Entities -----
// Note that in real life these don't even have to be owned/controled by me.
// We can glom on typeclass behavior to already-compiled classes.
case class Stove( numBurners:Int )
case class Girl( name:String )

// Scoping Object
object IsHot {
	//---- Trait Defining Typeclass Behavior ----
	trait IsHot[T] {           // My type class
	  def isHot(t:T) : Boolean // definition of behavior
	}
	
	// Method looks for implicitly-available IsHot implementation for the type T
	// Version 1 (old way) -- curry the implicit IsHot
	//def isHot[T](t:T)(implicit h:IsHot[T]) = h.isHot(t)
	// Version 2 (new way) -- context-bound the type parameter = shortcut for the old way.
	//   Only gotcha is that on the right side we don't have 'h' t work with as in ver 1.
	//   So we use implicitly to "look up" the right IsHot implementation for type T.
	def isHot[T : IsHot](t:T) = implicitly[IsHot[T]].isHot(t)
	
	// This magic line of code adds the isHot method to an existing class (foo.isHot).  We 
	// see the same context bound implementation with the implicitly-got lookup on the right 
	// side, as we had above.  Nothing new there.  The real trick is the implicit class, which
	// is new to Scala 2.10.  This automatically creates a wrapper class for T having the desired
	// extended behavior defined in IsHot.  This is desugared to something that Scala 2.92 can
	// understand but I don't remember what... google if needed.
	implicit class HotSyntax[T: IsHot](t: T) { def isHot = implicitly[IsHot[T]].isHot(t) }
	
	// Implicitly-available implementations for the IsHot typeclass
	implicit object HotStove extends IsHot[Stove] { def isHot(s:Stove) = s.numBurners > 2 }
	implicit object HotGirl  extends IsHot[Girl]  { def isHot(g:Girl) = g.name == "Liliana" }
	
	// Alternate implementation (non-implicit here) for Stove
	object HotStove2 extends IsHot[Stove] { def isHot(s:Stove) = s.numBurners > 4 }
}

//---- Use -----
object Typeclass_Use {
	import IsHot._

	// Function-call style
	val g = Girl("Liliana")
	println("Girl: "+isHot(g))
	val s = Stove(3)
	println("Stove: "+isHot(s))
	println("Stove: "+isHot(s)(HotStove2)) // use alternate implementation
	
	// Inline-style
	println(g.isHot)
	println(s.isHot)
}

