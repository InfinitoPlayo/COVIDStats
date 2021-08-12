package pe.edu.ulima.pm.covidinfo.managers

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import pe.edu.ulima.pm.covidinfo.models.AppDatabase
import pe.edu.ulima.pm.covidinfo.models.dao.Global
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.CountryEntity
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.FavoriteEntity
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.GlobalEntity

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
                for (i in 0..duplicateFavorites.size - 2) {
                    favoriteDAO.deleteSingleFavorite(duplicateFavorites[i].ID)
                }
                val updatedCountry = countryDAO.getSingleCountry(duplicateFavorites[0].Country)
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
}