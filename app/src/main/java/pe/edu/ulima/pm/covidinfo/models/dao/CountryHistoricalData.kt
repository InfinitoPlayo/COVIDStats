package pe.edu.ulima.pm.covidinfo.models.dao

import java.io.Serializable

// Data class para acceder a la data historica de un pais
data class CountryHistoricalData(
    val ID: String,
    val Country: String,
    val CountryCode: String,
    val Province: String,
    val City: String,
    val CityCode: String,
    val Lat: String,
    val Lon: String,
    val Confirmed: Long,
    val Deaths: Long,
    val Recovered: Long,
    val Active: Long,
    val Date: String
)