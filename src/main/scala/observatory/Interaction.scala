package observatory

import java.lang.Math._

import com.sksamuel.scrimage.{Image, Pixel}

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
    /** !! The method has been parallelized to run faster !! */

    import Visualization._

    // set the constant dimension
    val dimension = 256
    val alpha_value = 127
    val constant_high_zoom = 8

    // generate all possible pixel coordinates for the given location
    val coordinates = (for {
      j <- (y * dimension) until ((y * dimension) + dimension)
      i <- (x * dimension) until ((x * dimension) + dimension)
    } yield (i, j)).par

    val imgData = coordinates.map {
      case(i, j) => interpolateColor (
        colors,
        predictTemperature(temperatures, tileLocation(zoom + constant_high_zoom, i, j))
      )
    }.map(x => Pixel(x.red, x.green, x.blue, alpha_value)).toArray

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
    yearlyData.par.foreach {
      case (year, data) => {
        // generate the tile based images for them and save them at appropriate
        // location
        val tile_coordinates = (for {
          zoom <- 0 to 3
          j <- 0 until pow(2, zoom).toInt
          i <- 0 until pow(2, zoom).toInt
        } yield (zoom, i, j)).par

        // for every such tile generate tile image and save it
        tile_coordinates.foreach {
          case(zoom, x, y) =>
            // generate the tile and save it at the proper location
            generateImage(year, zoom, x, y, data)
        }
      }
    }
  }

}
