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
import pe.edu.ulima.pm.covidinfo.LoadingDialog
import pe.edu.ulima.pm.covidinfo.R
import pe.edu.ulima.pm.covidinfo.SingleCountryActivity
import pe.edu.ulima.pm.covidinfo.adapters.CountriesInfoRVAdapter
import pe.edu.ulima.pm.covidinfo.adapters.OnCountryInfoItemClickListener
import pe.edu.ulima.pm.covidinfo.managers.ConnectionManager
import pe.edu.ulima.pm.covidinfo.models.dao.CovidAPIService
import pe.edu.ulima.pm.covidinfo.models.dao.PremiumSingleCountryData
import pe.edu.ulima.pm.covidinfo.objects.*

class CountriesInfoFragment: Fragment(), OnCountryInfoItemClickListener {

    private var rviCompetitions: RecyclerView? = null
    private var countryName: String? = null
    private val retrofit = ConnectionManager.getInstance().getRetrofit()
    private var loadingDialog: LoadingDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_countries_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireActivity())

        //Llamando al RV del fragment
        rviCompetitions = view.findViewById(R.id.rviCountriesInfo)

        //removeEmptyCountries()

        val countriesRVAdapter = CountriesInfoRVAdapter(removeEmptyCountries(), this, view.context)
        rviCompetitions!!.adapter = countriesRVAdapter

    }

    // Devuelve una lista sin paises con 0 casos
    private fun removeEmptyCountries(): ArrayList<PremiumSingleCountryData> {
        val countries = PremiumGlobalDataInfo.premiumCountriesData
        for (i in 1..190) {
            if (countries!![i-1].TotalCases.toInt() == 0) {
                countries.removeAt(i-1)
            }
        }
        return countries!!
    }

    // Se solicita la informacion historica del pais seleccionado
    private fun searchSingleCountryHistoricalData() {
        loadingDialog!!.startLoading()
        lifecycleScope.launch {
            val call = retrofit.create(CovidAPIService::class.java).getCountryHistoricalStats("/country/$countryName")

            if (call.isSuccessful) {
                SingleCountryHistoricalStats.countryHistoricalData = call.body()
                loadingDialog!!.isDismiss()
                startActivity(Intent(context,SingleCountryActivity::class.java))
                Log.i("RequestHeaders", call.raw().toString())
            }
        }
    }

    //Cuando se hace click en un pais
    override fun onClick(country: PremiumSingleCountryData) {

        //Actualizando el Singleton con la info del pais seleccionado
        PremiumSingleCountryStats.country = country
        // Para obtener el nombre del pais en minusculas y sin espacios
        countryName = country.Country.replace(" ", "-").lowercase()

        if (InternetConnection.isConnected) {
            searchSingleCountryHistoricalData()
        } else {
            startActivity(Intent(context,SingleCountryActivity::class.java))
        }
    }
}