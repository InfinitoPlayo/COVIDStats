package pe.edu.ulima.pm.covidinfo.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import pe.edu.ulima.pm.covidinfo.dialogues.LoadingDialog
import pe.edu.ulima.pm.covidinfo.R
import pe.edu.ulima.pm.covidinfo.SingleCountryActivity
import pe.edu.ulima.pm.covidinfo.adapters.FavoriteCountriesRVAdapter
import pe.edu.ulima.pm.covidinfo.adapters.OnFavoriteCountryItemClickListener
import pe.edu.ulima.pm.covidinfo.managers.CovidAPIConnectionManager
import pe.edu.ulima.pm.covidinfo.managers.CovidInfoManager
import pe.edu.ulima.pm.covidinfo.models.AppDatabase
import pe.edu.ulima.pm.covidinfo.models.services.CovidAPIService
import pe.edu.ulima.pm.covidinfo.models.persistence.dao.FavoriteDAO
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.FavoriteEntity
import pe.edu.ulima.pm.covidinfo.objects.InternetConnection
import pe.edu.ulima.pm.covidinfo.objects.PremiumSingleCountryStats
import pe.edu.ulima.pm.covidinfo.objects.SingleCountryHistoricalStats

class FavoriteCountriesFragment: Fragment(), OnFavoriteCountryItemClickListener {

    private var rviFavoriteCountries: RecyclerView? = null
    private var countryName: String? = null
    private val retrofit = CovidAPIConnectionManager.getInstance().getRetrofit()
    private var loadingDialog: LoadingDialog? = null
    private var favorites : ArrayList<FavoriteEntity> = ArrayList()
    private var favoriteDAO: FavoriteDAO? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite_countries, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireActivity())
        favoriteDAO = AppDatabase.getInstance(view.context).favoriteDAO

        //Llamando al RV del fragment
        rviFavoriteCountries = view.findViewById(R.id.rviFavoriteCountries)

        lifecycleScope.launch {

            if (ArrayList(favoriteDAO!!.getAllFavorites()).isNotEmpty()){
                CovidInfoManager.getInstance().updateFavorites(ArrayList(favoriteDAO!!.getAllFavorites()), view.context)
                favorites = ArrayList(favoriteDAO!!.getAllFavorites())
            }
            val favoriteCountriesRVAdapter = FavoriteCountriesRVAdapter(favorites, this@FavoriteCountriesFragment, view.context)
            rviFavoriteCountries!!.adapter = favoriteCountriesRVAdapter
        }
    }

    private fun searchSingleCountryHistoricalData() {
        loadingDialog!!.startLoading()
        lifecycleScope.launch {
            val call = retrofit.create(CovidAPIService::class.java).getCountryHistoricalStats("/country/$countryName")

            if (call.isSuccessful) {
                SingleCountryHistoricalStats.countryHistoricalData = call.body()
                loadingDialog!!.isDismiss()
                startActivity(Intent(context, SingleCountryActivity::class.java))
                Log.i("RequestHeaders", call.raw().toString())
            }
        }
    }

    override fun onClick(country: FavoriteEntity) {
        //Actualizando el Singleton con la info del pais seleccionado
        PremiumSingleCountryStats.country = CovidInfoManager.getInstance().setPremiumSingleCountryDataFromCountryEntity(country)
        // Para obtener el nombre del pais en minusculas y sin espacios
        countryName = country.Country.replace(" ", "-").lowercase()

        if (InternetConnection.isConnected) {
            searchSingleCountryHistoricalData()
        } else {
            startActivity(Intent(context, SingleCountryActivity::class.java))
        }
    }
}