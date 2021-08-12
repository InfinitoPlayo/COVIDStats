package pe.edu.ulima.pm.covidinfo.models.dao

// Data class para obtener estadisticas premium de cada pais
data class PremiumSingleCountryData(
    val ID: String,
    val CountryISO: String,
    val Country: String,
    val Continent: String,
    val Date: String,
    val TotalCases: Long,
    val NewCases: Long,
    val TotalDeaths: Long,
    val NewDeaths: Long,
    val TotalCasesPerMillion: Double,
    val NewCasesPerMillion: Double,
    val TotalDeathsPerMillion: Double,
    val NewDeathsPerMillion: Double,
    val StringencyIndex: Long,
    val DailyIncidenceConfirmedCases: Double,
    val DailyIncidenceConfirmedDeaths: Double,
    val DailyIncidenceConfirmedCasesPerMillion: Double,
    val DailyIncidenceConfirmedDeathsPerMillion: Double,
    val IncidenceRiskConfirmedPerHundredThousand: Double,
    val IncidenceRiskDeathsPerHundredThousand: Double,
    val IncidenceRiskNewConfirmedPerHundredThousand: Double,
    val IncidenceRiskNewDeathsPerHundredThousand: Double,
    val CaseFatalityRatio: Double
)