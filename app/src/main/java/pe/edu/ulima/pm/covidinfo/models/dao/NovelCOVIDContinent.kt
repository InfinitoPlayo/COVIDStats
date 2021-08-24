package pe.edu.ulima.pm.covidinfo.models.dao

data class NovelCOVIDContinent(
    val updated: Long,
    val cases: Long,
    val todayCases: Long,
    val deaths: Long,
    val todayDeaths: Long,
    val recovered: Long,
    val todayRecovered: Long,
    val active: Long,
    val critical: Long,
    val casesPerOneMillion: Double,
    val deathsPerOneMillion: Double,
    val tests: Long,
    val testsPerOneMillion: Double,
    val population: Long,
    val continent: String,
    val activePerOneMillion: Double,
    val recoveredPerOneMillion: Double,
    val criticalPerOneMillion: Double,
    val countries: ArrayList<String>
)
