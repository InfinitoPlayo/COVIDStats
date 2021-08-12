package pe.edu.ulima.pm.covidinfo.models.dao

import pe.edu.ulima.pm.covidinfo.models.persistence.entities.CountryEntity

// Data class para poder hacer el ranking
data class PremiumGlobalData(
    val ID: String,
    val Message: String,
    val Countries: ArrayList<CountryEntity>,
    var Date: String
)
