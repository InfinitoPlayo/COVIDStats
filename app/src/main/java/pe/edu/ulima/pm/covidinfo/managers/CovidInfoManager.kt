package pe.edu.ulima.pm.covidinfo.managers

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import pe.edu.ulima.pm.covidinfo.models.AppDatabase
import pe.edu.ulima.pm.covidinfo.models.dao.Date
import pe.edu.ulima.pm.covidinfo.models.dao.Global
import pe.edu.ulima.pm.covidinfo.models.dao.PremiumSingleCountryData
import pe.edu.ulima.pm.covidinfo.models.persistence.dao.FavoriteDAO
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.CountryEntity
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.FavoriteEntity
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.GlobalEntity
import pe.edu.ulima.pm.covidinfo.objects.GlobalDataInfo
import pe.edu.ulima.pm.covidinfo.objects.GlobalStats
import pe.edu.ulima.pm.covidinfo.objects.PremiumGlobalDataInfo
import kotlin.math.log

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

    //Convierte un PremiumSingleCountryData en un CountryEntity
    fun setCountryEntity(country: PremiumSingleCountryData): CountryEntity {

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

    suspend fun getCountriesFromRoom(context: Context) {

        val countryDAO = AppDatabase.getInstance(context).countryDAO
        val countries: ArrayList<PremiumSingleCountryData> = ArrayList()
        val countryEntity = ArrayList(countryDAO.getAllCountries())
        val size = countryEntity.size

        for (i in (size-219) until size-1) {
            countries.add(
                PremiumSingleCountryData(
                    countryEntity[i].ID,
                    countryEntity[i].CountryISO,
                    countryEntity[i].Country,
                    countryEntity[i].Continent,
                    countryEntity[i].Date,
                    countryEntity[i].TotalCases,
                    countryEntity[i].NewCases,
                    countryEntity[i].TotalDeaths,
                    countryEntity[i].NewDeaths,
                    countryEntity[i].TotalCasesPerMillion,
                    countryEntity[i].NewCasesPerMillion,
                    countryEntity[i].TotalDeathsPerMillion,
                    countryEntity[i].NewDeathsPerMillion,
                    countryEntity[i].StringencyIndex,
                    countryEntity[i].DailyIncidenceConfirmedCases,
                    countryEntity[i].DailyIncidenceConfirmedDeaths,
                    countryEntity[i].DailyIncidenceConfirmedCasesPerMillion,
                    countryEntity[i].DailyIncidenceConfirmedDeathsPerMillion,
                    countryEntity[i].IncidenceRiskConfirmedPerHundredThousand,
                    countryEntity[i].IncidenceRiskDeathsPerHundredThousand,
                    countryEntity[i].IncidenceRiskNewConfirmedPerHundredThousand,
                    countryEntity[i].IncidenceRiskNewDeathsPerHundredThousand,
                    countryEntity[i].CaseFatalityRatio,)
            )
        }

        //Eliminar paises duplicados
        if(size>220) {
            for (i in 0 until size-220) {
                countryDAO.deleteSingleCountry(countryEntity[i].ID)
            }
        }

        // Anadir resultados al Singleton
        PremiumGlobalDataInfo.premiumCountriesData = countries
    }

    suspend fun getGlobalFromRoom(context: Context) {

        val globalDAO = AppDatabase.getInstance(context).globalDAO
        val globalEntity = ArrayList(globalDAO.getAllGlobal())
        val g = globalEntity[globalEntity.size-1]
        Log.i("getGlobalFromRoom", g.toString())

        GlobalStats.NewConfirmed = g.NewConfirmed
        GlobalStats.TotalConfirmed = g.TotalConfirmed
        GlobalStats.NewDeaths = g.NewDeaths
        GlobalStats.TotalDeaths = g.TotalDeaths
        GlobalStats.NewRecovered = g.NewRecovered
        GlobalStats.TotalRecovered = g.TotalRecovered
        GlobalStats.Date = g.Date

        // Anadir resultados al Singleton
        //GlobalDataInfo.globalData?.Global = global
        Log.i("getGlobalFromRoom", GlobalStats.NewConfirmed?.toString().toString())
    }

    suspend fun getDateFromRoom(context: Context) {

        val dateDAO = AppDatabase.getInstance(context).dateDAO
        val dateEntity = ArrayList(dateDAO.getAllDates())
        val d = dateEntity[dateEntity.size-1]
        val date = Date(d.ID, d.Date)

        // Anadir resultados al Singleton
        PremiumGlobalDataInfo.premiumGlobalData?.Date= date.Date
    }

    // Devuelve una lista sin paises con 0 casos
    fun removeEmptyCountries(): ArrayList<PremiumSingleCountryData> {
        val countries = PremiumGlobalDataInfo.premiumCountriesData
        for (i in 1..190) {
            if (countries!![i-1].TotalCases.toInt() == 0) {
                countries.removeAt(i-1)
            }
        }
        return countries!!
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