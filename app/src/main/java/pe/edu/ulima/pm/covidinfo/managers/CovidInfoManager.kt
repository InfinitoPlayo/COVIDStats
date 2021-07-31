package pe.edu.ulima.pm.covidinfo.managers

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import pe.edu.ulima.pm.covidinfo.models.AppDatabase
import pe.edu.ulima.pm.covidinfo.models.dao.Global
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.CountryEntity
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.FavoriteEntity
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.GlobalEntity
import pe.edu.ulima.pm.covidinfo.objects.GlobalStats

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

    //Convierte un Global en un GlobalEntity
    fun setGlobalEntity(global: Global): GlobalEntity {

        return GlobalEntity(
            0,
            global.NewConfirmed,
            global.TotalConfirmed,
            global.NewDeaths,
            global.TotalDeaths,
            global.NewRecovered,
            global.TotalRecovered,
            global.Date
        )
    }

    fun verifyAvailableNetwork(activity: AppCompatActivity): Boolean {
        val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return  networkInfo!=null && networkInfo.isConnected
    }

    /*// Devuelve una lista sin paises con 0 casos
    fun removeEmptyCountries(): ArrayList<PremiumSingleCountryData> {
        val countries = PremiumGlobalDataInfo.premiumCountriesData
        for (i in 1..190) {
            if (countries!![i-1].TotalCases.toInt() == 0) {
                countries.removeAt(i-1)
            }
        }
        return countries!!
    }*/

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

    fun setGlobalStats(global: Global) {
        GlobalStats.TotalDeaths = global.TotalDeaths
        GlobalStats.TotalRecovered = global.TotalRecovered
        GlobalStats.TotalConfirmed = global.TotalConfirmed
        GlobalStats.Date = global.Date
        GlobalStats.NewRecovered = global.NewRecovered
        GlobalStats.NewDeaths = global.NewDeaths
        GlobalStats.NewConfirmed = global.NewConfirmed
    }

    fun setFavoriteFromCountryEntity(country: CountryEntity): FavoriteEntity {

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
            country.CaseFatalityRatio
        )
    }

    suspend fun updateFavorites(favorites: ArrayList<FavoriteEntity>, context: Context) {

        val countryDAO = AppDatabase.getInstance(context).countryDAO
        val favoriteDAO = AppDatabase.getInstance(context).favoriteDAO

        //val duplicateFavorites =favoriteDAO.getFavoritesWithSameName("New Zealand")
        //Log.i("duplicateFavorites", duplicateFavorites.toString())

        favorites.forEach {

            val country = countryDAO.getSingleCountryByID(it.ID)
            //val favorite = favoriteDAO.getSingleFavorite(it.ID)
            //Log.i("updateFavorites", country.toString())

            if (country != null) {
                favoriteDAO.deleteSingleFavorite(it.Country)
                val newFavorite = FavoriteEntity(
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
                    country.CaseFatalityRatio
                )
                favoriteDAO.insertFavorite(newFavorite)
            }
        }
    }

}