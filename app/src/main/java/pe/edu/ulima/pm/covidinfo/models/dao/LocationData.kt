package pe.edu.ulima.pm.covidinfo.models.dao

data class LocationData(
    val status: String,
    val country: String,
    val countryCode: String,
    val region: String,
    val regionName: String,
    val city: String,
    val zip: String,
    val lat: Float,
    val lon: Float,
    val timezone: String,
    val isp: String,
    val org: String,
    val aas: String,
    val query: String
)