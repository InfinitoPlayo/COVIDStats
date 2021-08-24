package pe.edu.ulima.pm.covidinfo.models.dao

data class NovelCOVIDCountry (

    val updated: Long,
    val country: String,
    val countryInfo: NovelCOVIDCountryInfo,
    val cases: Long,
    val todayCases: Long,
    val deaths: Long,
    val todayDeaths: Long,
    val recovered: Long,
    val todayRecovered: Long,
    val active: Long,
    val critical: Long,
    val casesPerOneMillion: Long,
    val deathsPerOneMillion: Double,
    val tests: Long,
    val testsPerOneMillion: Long,
    val population: Long,
    val continent: String,
    val oneCasePerPeople: Long,
    val oneDeathPerPeople: Long,
    val oneTestPerPeople: Long,
    val activePerOneMillion: Double,
    val recoveredPerOneMillion: Double,
    val criticalPerOneMillion: Double
    )