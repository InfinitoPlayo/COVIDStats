package pe.edu.ulima.pm.covidinfo.models.dao

import java.io.Serializable

// Data class para poder hacer el ranking
data class PremiumGlobalData(
    val ID: String,
    val Message: String,
    val Countries: ArrayList<PremiumSingleCountryData>,
    var Date: String
) : Serializable
