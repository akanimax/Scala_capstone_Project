package observatory

import java.lang.Math._
import com.sksamuel.scrimage.{Image, Pixel}

/**
  * 2nd milestone: basic visualization
  */
object Visualization {

  /**
    * The radius of earth in kilometers
    */
  val earthRadius = 6371 // km

  /**
    * Helper Method for rounding off a double value
    * @param x The double value to be rounded off
    * @return The rounded integer value
    */
  def round(x: Double): Int = (x + 0.5).toInt

  /**
    * Helper Method to calculate the Great-Circle distance between two locations.
    * @param x Location of point 1
    * @param y Location of point 2
    * @return The Great Circle distance between the two points.
    */
  def gCDistance(x: Location, y: Location): Double = {
    acos((sin(toRadians(x.lat)) * sin(toRadians(y.lat))) +
      (cos(toRadians(x.lat)) * cos(toRadians(y.lat)) *
        cos(abs(toRadians(x.lon) - toRadians(y.lon)))))
  }



  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Double)], location: Location): Double = {
    /** !! The method has been parallelized to run faster !! */
    /** check if the location already exists in the known temperatures:*/
    if(temperatures.exists(x => x._1 == location))
      temperatures.find(x => x._1 == location).get._2

    else if(temperatures.exists(x => gCDistance(x._1, location) < 1 / earthRadius))
      temperatures.find(x => gCDistance(x._1, location) < 1 / earthRadius).get._2

    else {
      /** Perform the interpolation to calculate the temperature at the required location.*/

      // fix the value of p for inverse distance weighting
      val p = 2 // The result with p = 2 should be smooth and pretty

      // calculate the distances from the list
      val dists = temperatures.par.map{case (point, temp) => (1 / pow(gCDistance(point, location), p), temp)}

      // Calculate the weighted average of the temps (weighed by the dists)
      dists.map{case (x, y) => x * y}.sum / dists.map(_._1).sum
    }
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Double, Color)], value: Double): Color = {

    val sortedPoints = points.toVector.sortBy(_._1)
    val ranges = sortedPoints zip sortedPoints.tail

    val range = ranges.find{ case (l, h) => l._1 <= value && h._1 >= value }

    range match {
      case Some((low, high)) =>
        val (lowColor, highColor) = (low._2, high._2)
        val (lowTemp, highTemp) = (low._1, high._1)
        val slope = (value - lowTemp) / (highTemp - lowTemp)

        Color(
          round(lowColor.red + ((highColor.red - lowColor.red) * slope)),
          round(lowColor.green + ((highColor.green - lowColor.green) * slope)),
          round(lowColor.blue + ((highColor.blue - lowColor.blue) * slope))
        )

      case None =>
        // This means it exceeds the highest temp color or
        // is even lower than the lowest temp.
        if(value > sortedPoints.last._1) sortedPoints.last._2
        else sortedPoints.head._2
    }
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)]): Image = {
    /** !! The method has been parallelized to run faster !! */
    /** Fix a few constant values to be reused in the method */
    val alpha_value = 128 //
    val (width, height) = (360, 180)

    // Generate the colors for all the possible lats and longs.
    val data = (for {
      lat <- 90 until -90 by -1
      lon <- -180 until 180
    } yield (lat, lon)).par

    // This one runs in parallel.
    val pixels = data.map {
      case (lat, lon) =>
        val color = interpolateColor(colors, predictTemperature(temperatures, Location(lat, lon)))
        Pixel(color.red, color.green, color.blue, alpha_value)
    }.toArray

    // return the image so formed
    Image(width, height, pixels)
  }
}

