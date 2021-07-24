package pe.edu.ulima.pm.covidinfo.models.dao

// Data class para la interfaz del pie chart con los casos a nivel global
data class Global(
    var NewConfirmed: Long,
    var TotalConfirmed: Long,
    var NewDeaths: Long,
    var TotalDeaths: Long,
    var NewRecovered: Long,
    var TotalRecovered: Long,
    var Date: String
)