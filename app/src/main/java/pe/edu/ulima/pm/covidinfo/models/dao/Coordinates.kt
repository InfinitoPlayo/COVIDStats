package pe.edu.ulima.pm.covidinfo.models.dao

data class Coordinates (
    val lat: Double,
    val lon: Double,
    var country: String,
    var cases: Long,
    var casesPerMillion: Double
)