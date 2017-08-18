package observatory

import java.time.LocalDate

import org.scalatest.{FunSuite, Matchers}

trait ExtractionTest extends FunSuite with Matchers {

  // A simple test to check if the method runs properly.
  ignore("locateTemperatures") {
    val records = Extraction.locateTemperatures(1996, "/stations.csv", "/1996.csv")
    records should contain ((LocalDate.of(1996, 1, 1), Location(+70.933, -008.667), Extraction.toCelsius(15.2)))
  }

}