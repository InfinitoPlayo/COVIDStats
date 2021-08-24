package pe.edu.ulima.pm.covidinfo.models.dao

data class NovelCOVIDCountryInfo(
    val _id: Int,
    val iso2: String,
    val iso3: String,
    val lat: Double,
    val long: Double,
    val flag: String
)
