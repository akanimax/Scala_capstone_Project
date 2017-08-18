package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import Math._

/**
  * 3rd milestone: interactive visualization
  */
object Interaction {

  /**
    * @param zoom Zoom level
    * @param x X coordinate
    * @param y Y coordinate
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(zoom: Int, x: Int, y: Int): Location = {
    // This function is basically required to invert the mercator projection.

    val lon = ((x / pow(2, zoom)) * 360) - 180 // This value is in degrees
    val lat = atan(sinh(PI - ((y / pow(2, zoom)) * (2 * PI)))) * (180 / PI)// value in degrees

    // return a location corresponding to the lat and long value
    Location(lat, lon)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @param zoom Zoom level
    * @param x X coordinate
    * @param y Y coordinate
    * @return A 256×256 image showing the contents of the tile defined by `x`, `y` and `zooms`
    */
  def tile(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)],
           zoom: Int, x: Int, y: Int): Image = {

    /** Current Implementation doesn't use the suggested recursive strategy*/
    import Visualization._

    // set the constant dimension
    val dimension = 256
    val alpha_value = 127
    val constant_high_zoom = 8

    // generate an array of Pixel values for every location inside the tile
    val imgData = (for {
      j <- (y * dimension) until ((y * dimension) + dimension)
      i <- (x * dimension) until ((x * dimension) + dimension)
    } yield interpolateColor(
        colors,
        predictTemperature(temperatures, tileLocation(zoom + constant_high_zoom, i, j))
    )).map(x => Pixel(x.red, x.green, x.blue, alpha_value)).toArray

    // return the Image corresponding to this:
    Image(dimension, dimension, imgData)
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    * @param yearlyData Sequence of (year, data), where `data` is some data associated with
    *                   `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
    yearlyData: Iterable[(Int, Data)],
    generateImage: (Int, Int, Int, Int, Data) => Unit
  ): Unit = {
    yearlyData.foreach {
      case (year, data) =>

    }
  }

}
