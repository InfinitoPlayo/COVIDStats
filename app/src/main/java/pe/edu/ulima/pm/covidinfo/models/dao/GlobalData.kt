package pe.edu.ulima.pm.covidinfo.models.dao

import java.io.Serializable

// Data class para poder hacer el ranking
data class GlobalData(
    val ID: String,
    val Message: String,
    var Global: Global,
    val Countries: ArrayList<SingleCountryData>
) : Serializable