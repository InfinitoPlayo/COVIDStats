package pe.edu.ulima.pm.covidinfo.managers

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import pe.edu.ulima.pm.covidinfo.models.AppDatabase
import pe.edu.ulima.pm.covidinfo.models.dao.Date
import pe.edu.ulima.pm.covidinfo.models.dao.Global
import pe.edu.ulima.pm.covidinfo.models.dao.PremiumSingleCountryData
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.CountryEntity
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.FavoriteEntity
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.GlobalEntity
import pe.edu.ulima.pm.covidinfo.objects.GlobalDataInfo
import pe.edu.ulima.pm.covidinfo.objects.PremiumGlobalDataInfo

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

        for (i in (size-218)..size) {
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

        // Anadir resultados al Singleton
        PremiumGlobalDataInfo.premiumCountriesData = countries
    }

    suspend fun getGlobalFromRoom(context: Context) {

        val globalDAO = AppDatabase.getInstance(context).globalDAO
        val globalEntity = ArrayList(globalDAO.getAllGlobal())
        val g = globalEntity[globalEntity.size-1]

        val global = Global(
            g.NewConfirmed,
            g.TotalConfirmed,
            g.NewDeaths,
            g.TotalDeaths,
            g.NewRecovered,
            g.TotalRecovered,
            g.Date)

        // Anadir resultados al Singleton
        GlobalDataInfo.globalData?.Global = global
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

    fun setFavoriteEntity(country: PremiumSingleCountryData): FavoriteEntity {

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

    fun setPremiumSingleCountryDataFromFavorite(country: FavoriteEntity): PremiumSingleCountryData {

        return PremiumSingleCountryData(
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
}