package observatory

import observatory.Extraction.{locateTemperatures, locationYearlyAverageRecords}
import observatory.Interaction._
import org.scalatest.prop.Checkers
import org.scalatest.{FunSuite, Matchers}

import scala.collection.concurrent.TrieMap

trait InteractionTest extends FunSuite with Checkers with Matchers {

  test("The tileLocation Must return proper coordinates") {
    val loc = tileLocation(8, 256, 0)
    println("The location: " + loc)
  }

  test("Check if a tile gets generated or not:") {

    // get all the temperature values from the 1996 file
    val year = 1996
    val stationsFile = "/stations.csv"
    println("Extracting data from: " + "/" + year + ".csv ...")
    val temperatures = locationYearlyAverageRecords(locateTemperatures(year, stationsFile, "/" + year + ".csv"))

    // Now set the colorMap to be used
    println("Setting the ColorMap to be used ...")
    val colors = Seq(
      (60.0, Color(255, 255, 255)),
      (32.0, Color(255, 0, 0)),
      (12.0, Color(255, 255, 0)),
      (0.0, Color(0, 255, 255)),
      (-15.0, Color(0, 0, 255)),
      (-27.0, Color(255, 0, 255)),
      (-50.0, Color(33, 0, 107)),
      (-60.0, Color(0, 0, 0))
    )

    // val consideredStations = 10

    // Print the number of weather stations available:
    println("The No. of Weather Stations: " + temperatures.size)
    println("The No. of Weather Stations considered: " + temperatures.size)

    // Now generate the Image for that data
    println("Generating all possible tiles for the year: " + year + " ...")
    println("This is going to take a very long time... Go relax!")
    generateTiles[Iterable[(Location, Double)]](TrieMap(year -> temperatures),
      (year, zoom, x, y, data) => {
        val temperatureMap = tile(data, colors, zoom, x, y)

        // Now finally, save the image in target folder
        val savePath = "target/temperatures/" + year + "/" + zoom + "/" + x + "-" + y + ".png"
        println("Finally, saving the map at: " + savePath)
        temperatureMap.output(savePath)
      }
    )

    assert(1 == 1, "No Exception arose, so check the target folder.")
    // This one works
  }

}
