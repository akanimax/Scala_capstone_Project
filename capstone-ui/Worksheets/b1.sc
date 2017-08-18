/**
  * Exploring the Extractors in scala
  * */

trait User {
  def name: String
  def score: Int
}

class FreeUser(val name: String, val score: Int, val upgradeProbability: Double)
  extends User

class PremiumUser(val name: String, val score: Int) extends User

object FreeUser {
  def unapply(user: FreeUser): Option[(String, Int, Double)] =
    Some((user.name, user.score, user.upgradeProbability))
}

object PremiumUser {
  def unapply(user: PremiumUser): Option[(String, Int)] = Some((user.name, user.score))
}

// one can write a pattern match block like following
val user: User = new FreeUser("Animesh", 55, 0.89)

user match {
  case FreeUser(name, _, _) => println("Hello " + name)
  case PremiumUser(_, score) => println("Hello Sir! " + score.toString)
}

/**
  * Take a look at the boolean extractor:
  * */

// The extractor doesn't have to be inside the class itself!
// it can be in a completely different object as well
object premiumCandidate {
  def unapply(user: FreeUser): Boolean = user.upgradeProbability > 0.75
}

user match {
  case premiumCandidate() =>
    println("Hello User! You will become premium soon")
  case _ => println("You have a long time to become a premium user")
}

// If you still wish to get the extracted object,
/** Use the @ operator as follows */

user match {
  case freeUser @ premiumCandidate() =>
    println("Hello " + freeUser.name + "! You will become premium soon.")
}

Some(10).isDefined
None.isDefined

val (a, b) = (0, 3)

val c = 1 to 10 by 3
c zip c.tail

// None.get

for {
  i <- 0 to 10
  j <- 11 to 20
} yield(i, j)

(90 until -90 by -1).length

-180 to 180

for(i <- (1 to 10).par) {
  println(i)
}

val y = scala.collection.mutable.Set()