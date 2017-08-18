val a = Vector(1, 2, 3, 4, 5, 6)

// Use of scanLeft method
a.scanLeft(100)(_ + _)

/** difference between
  * def method
  * and
  * val function
  * */
// def method
def someMethod(x: Int) = x // this defines a method
// take a look at the output type for this method


// val function
val someFunc: Int => Int = x => x

val otherFunc = someMethod _

/**
  * Partial Functions in Scala:
  * */

val partial: PartialFunction[(Int, Int), Int] = {
  case (1, 2) => 3
  case (4, 5) => 6
}


partial(1, 2) // is defined
partial(4, 5) // is defined

partial.isDefinedAt(1, 2) // you can check if it is defined or not using this
partial.isDefinedAt(3, 4) // this returns a false

// partial(3, 4) // this throws a match error


"33.33".toDouble

"Animesh,,,,,".split(",", -1)

(90 until (90 + 256)).length