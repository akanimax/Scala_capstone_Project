package observatory


import org.scalatest.{FunSuite, Matchers}
import org.scalatest.prop.Checkers
import Math._
import Extraction._
import Visualization._

trait VisualizationTest extends FunSuite with Checkers with Matchers {

  // To check if the test suite is properly setup
  test("A one should be equal to one") {
    assert(1 === 1, "This test should fail")
  }


  // To test the predictTemperature method:
  test("The temperature at z should be closer to 10 than 20") {
    val z = Location(88.0,-176.0)
    val x = Location(88.5,-176.0)
    val y = Location(0.0, 0.0)

    val predictedTemp = predictTemperature(Seq((x, 10), (y, 20)), z)

    println("predicted Temperature: " + predictedTemp)
    println("Distance between z and x: " + gCDistance(z, x))
    println("Distance between z and y: " + gCDistance(z, y))

    assert(abs(predictedTemp - 10) < abs(predictedTemp - 20), "predictedTemp is closer to 20 :(")
  }

  // Generate an Image for the year file 1996.
  ignore("Generate the file for year 1996") {

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
    println("Rendering the map of the acquired data ...")
    val temperatureMap = visualize(temperatures, colors)

    // Now finally, save the image in target folder
    val savePath = "target/" + year + ".png"
    println("Finally, saving the map at: " + savePath)
    temperatureMap.output(savePath)

    assert(1 == 1, "No Exception arose, so check the target folder.")
    // This one works
  }
}
