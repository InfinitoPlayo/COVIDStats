package pe.edu.ulima.pm.covidinfo.managers

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import pe.edu.ulima.pm.covidinfo.models.AppDatabase
import pe.edu.ulima.pm.covidinfo.models.dao.NovelCOVIDContinent
import pe.edu.ulima.pm.covidinfo.models.dao.NovelCOVIDCountry
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.ContinentEntity
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.CountryEntity
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.FavoriteEntity
import pe.edu.ulima.pm.covidinfo.models.services.NovelCOVIDService
import pe.edu.ulima.pm.covidinfo.objects.NovelCOVIDCountries

class CovidInfoManager {

    // Para instanciar el singleton
    companion object {
        private var instance : CovidInfoManager? = null

        fun getInstance() : CovidInfoManager {
            if (instance == null){
                instance = CovidInfoManager()
            }
            return instance!!
        }
    }

    fun verifyAvailableNetwork(activity: AppCompatActivity): Boolean {
        val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return  networkInfo!=null && networkInfo.isConnected
    }

    fun setFavoriteEntity(country: CountryEntity): FavoriteEntity {
        return FavoriteEntity(
            country.ID,
            country.CountryISO,
            country.Country,
            country.Continent,
            country.Date,
            country.TotalCases,
            country.NewCases,
            country.TotalDeaths,
            country.NewDeaths,
            country.TotalCasesPerMillion,
            country.NewCasesPerMillion,
            country.TotalDeathsPerMillion,
            country.NewDeathsPerMillion,
            country.StringencyIndex,
            country.DailyIncidenceConfirmedCases,
            country.DailyIncidenceConfirmedDeaths,
            country.DailyIncidenceConfirmedCasesPerMillion,
            country.DailyIncidenceConfirmedDeathsPerMillion,
            country.IncidenceRiskConfirmedPerHundredThousand,
            country.IncidenceRiskDeathsPerHundredThousand,
            country.IncidenceRiskNewConfirmedPerHundredThousand,
            country.IncidenceRiskNewDeathsPerHundredThousand,
            country.CaseFatalityRatio)
    }

    fun setPremiumSingleCountryDataFromCountryEntity(country: FavoriteEntity): CountryEntity {
        return CountryEntity(
            country.ID,
            country.CountryISO,
            country.Country,
            country.Continent,
            country.Date,
            country.TotalCases,
            country.NewCases,
            country.TotalDeaths,
            country.NewDeaths,
            country.TotalCasesPerMillion,
            country.NewCasesPerMillion,
            country.TotalDeathsPerMillion,
            country.NewDeathsPerMillion,
            country.StringencyIndex,
            country.DailyIncidenceConfirmedCases,
            country.DailyIncidenceConfirmedDeaths,
            country.DailyIncidenceConfirmedCasesPerMillion,
            country.DailyIncidenceConfirmedDeathsPerMillion,
            country.IncidenceRiskConfirmedPerHundredThousand,
            country.IncidenceRiskDeathsPerHundredThousand,
            country.IncidenceRiskNewConfirmedPerHundredThousand,
            country.IncidenceRiskNewDeathsPerHundredThousand,
            country.CaseFatalityRatio)
    }

    //Para solo mostrar paises actualizados y quitar posibles duplicados
    suspend fun updateFavorites(favorites: ArrayList<FavoriteEntity>, context: Context) {
        val favoriteDAO = AppDatabase.getInstance(context).favoriteDAO
        val countryDAO = AppDatabase.getInstance(context).countryDAO

        favorites.forEach {
            val duplicateFavorites = ArrayList(favoriteDAO.getFavoritesWithSameName(it.Country))

            if (duplicateFavorites.size > 1) {
                duplicateFavorites.forEach { fav ->
                    favoriteDAO.deleteSingleFavorite(fav.ID)
                }
                val updatedCountry = countryDAO.getSingleCountry(duplicateFavorites[1].Country)
                favoriteDAO.insertFavorite(
                    FavoriteEntity(
                        updatedCountry.ID,
                        updatedCountry.CountryISO,
                        updatedCountry.Country,
                        updatedCountry.Continent,
                        updatedCountry.Date,
                        updatedCountry.TotalCases,
                        updatedCountry.NewCases,
                        updatedCountry.TotalDeaths,
                        updatedCountry.NewDeaths,
                        updatedCountry.TotalCasesPerMillion,
                        updatedCountry.NewCasesPerMillion,
                        updatedCountry.TotalDeathsPerMillion,
                        updatedCountry.NewDeathsPerMillion,
                        updatedCountry.StringencyIndex,
                        updatedCountry.DailyIncidenceConfirmedCases,
                        updatedCountry.DailyIncidenceConfirmedDeaths,
                        updatedCountry.DailyIncidenceConfirmedCasesPerMillion,
                        updatedCountry.DailyIncidenceConfirmedDeathsPerMillion,
                        updatedCountry.IncidenceRiskConfirmedPerHundredThousand,
                        updatedCountry.IncidenceRiskDeathsPerHundredThousand,
                        updatedCountry.IncidenceRiskNewConfirmedPerHundredThousand,
                        updatedCountry.IncidenceRiskNewDeathsPerHundredThousand,
                        updatedCountry.CaseFatalityRatio))
            } else {
                val updatedCountry = countryDAO.getSingleCountry(it.Country)

                favoriteDAO.deleteSingleFavorite(it.ID)
                favoriteDAO.insertFavorite(
                    FavoriteEntity(
                        updatedCountry.ID,
                        updatedCountry.CountryISO,
                        updatedCountry.Country,
                        updatedCountry.Continent,
                        updatedCountry.Date,
                        updatedCountry.TotalCases,
                        updatedCountry.NewCases,
                        updatedCountry.TotalDeaths,
                        updatedCountry.NewDeaths,
                        updatedCountry.TotalCasesPerMillion,
                        updatedCountry.NewCasesPerMillion,
                        updatedCountry.TotalDeathsPerMillion,
                        updatedCountry.NewDeathsPerMillion,
                        updatedCountry.StringencyIndex,
                        updatedCountry.DailyIncidenceConfirmedCases,
                        updatedCountry.DailyIncidenceConfirmedDeaths,
                        updatedCountry.DailyIncidenceConfirmedCasesPerMillion,
                        updatedCountry.DailyIncidenceConfirmedDeathsPerMillion,
                        updatedCountry.IncidenceRiskConfirmedPerHundredThousand,
                        updatedCountry.IncidenceRiskDeathsPerHundredThousand,
                        updatedCountry.IncidenceRiskNewConfirmedPerHundredThousand,
                        updatedCountry.IncidenceRiskNewDeathsPerHundredThousand,
                        updatedCountry.CaseFatalityRatio)
                    )
            }
        }
    }

    suspend fun getNovelCOVIDCountries() {
        val retrofit = NovelCOVIDConnectionManager.getInstance().getRetrofit()
        val countries: ArrayList<NovelCOVIDCountry> = ArrayList()

        val call = retrofit.create(NovelCOVIDService::class.java).getCountriesData("/v2/countries?yesterday&sort")
        // Si se devuelve data
        if (call.isSuccessful) {
            Log.i("MainActivity", call.raw().toString())
            call.body()!!.forEach { countries.add(it) }
            NovelCOVIDCountries.countries = countries

        }else {
            Log.i("MainActivity", call.errorBody().toString())
        }
    }

    fun setContinentEntity(continent: NovelCOVIDContinent): ContinentEntity {
        return ContinentEntity(
            0,
            continent.updated,
            continent.cases,
            continent.todayCases,
            continent.deaths,
            continent.todayDeaths,
            continent.recovered,
            continent.todayRecovered,
            continent.active,
            continent.critical,
            continent.casesPerOneMillion,
            continent.deathsPerOneMillion,
            continent.tests,
            continent.testsPerOneMillion,
            continent.population,
            continent.continent,
            continent.activePerOneMillion,
            continent.recoveredPerOneMillion,
            continent.criticalPerOneMillion
        )
    }
}