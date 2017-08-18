package observatory

import java.time.LocalDate

import scala.io.Source

/**
  * 1st milestone: data extraction
  */


/**
  * ***********************************************************************************************************
  * WARNING! WARNING! WARNING!
  * ***********************************************************************************************************
  *
  * NOTE: Currently, These implementations use simple file based reading
  * And, normal computations for performing the Data extraction. If this module is used for runtime
  * user interactivity, It might cause a serious bottleneck. So, This implementation might change in order to
  * accommodate use of spark framework
  *
  * ************************************************************************************************************/

object Extraction {

  /** Helper method to convert fahrenheit to celsius*/
  def toCelsius(temp: Double): Double = ((temp - 32) * 5) / 9 // to convert to celcius


  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Int, stationsFile: String, temperaturesFile: String):
    /** !! The method has been parallelized to run faster !! */

    Iterable[(LocalDate, Location, Double)] = {

    // first acquire the two file resources
    val stationsResource = Source.fromInputStream(getClass.getResourceAsStream(stationsFile)).getLines()
    val temperatureResource = Source.fromInputStream(getClass.getResourceAsStream(temperaturesFile)).getLines()

    /** load all the stations in a well formed data-structure*/
    case class Station(stn: String, wban: String, loc: Location)

    object Stations {

      val stations: Vector[Station] = stationsResource.foldLeft(Vector[Station]()) (
        (store, value) => value.split(",", -1) match {
          case Array(stn, wban, lat, long) =>
            if(lat != "" && long != "") store :+ Station(stn, wban, Location(lat.toDouble, long.toDouble))
            else store
        }
      )

      def getStation(stn: String, wban: String): Option[Station] =
        stations.find(x => x.stn == stn && x.wban == wban)
    }


    /** Now create the main Iterable for the method*/
    temperatureResource.foldLeft(Vector[(LocalDate, Location, Double)]()) (
      (store, value) => value.split(",", -1) match {
        case Array(stn, wban, month, day, temp) =>
          Stations.getStation(stn, wban) match {
            case Some(x) =>
              store :+ ((LocalDate.of(year, month.toInt, day.toInt), x.loc, toCelsius(temp.toDouble)))

            case None => store
          }
      }
    )

  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Double)]):
    Iterable[(Location, Double)] = {
    /** !! The method has been parallelized to run faster !! */

    records.par.groupBy(_._2)
      .map {
        case (key, values) => (key, values.foldLeft(0.0)(_ + _._3) / values.size)
      }.toVector
  }

}
