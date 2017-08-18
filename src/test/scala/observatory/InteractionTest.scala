package observatory

import org.scalatest.{FunSuite, Matchers}
import org.scalatest.prop.Checkers
import Interaction._

import scala.collection.concurrent.TrieMap

trait InteractionTest extends FunSuite with Checkers with Matchers {

  test("The tileLocation Must return proper coordinates") {
    val loc = tileLocation(8, 256, 0)
    println("The location: " + loc)
  }

}
