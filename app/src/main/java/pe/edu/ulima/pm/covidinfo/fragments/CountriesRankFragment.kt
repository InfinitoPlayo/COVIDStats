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
import pe.edu.ulima.pm.covidinfo.adapters.CountriesRankRVAdapter
import pe.edu.ulima.pm.covidinfo.adapters.OnCountryRankItemClickListener
import pe.edu.ulima.pm.covidinfo.managers.ConnectionManager
import pe.edu.ulima.pm.covidinfo.models.dao.CovidAPIService
import pe.edu.ulima.pm.covidinfo.models.dao.PremiumSingleCountryData
import pe.edu.ulima.pm.covidinfo.objects.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CountriesRankFragment: Fragment(), OnCountryRankItemClickListener {

    private var rviCompetitions: RecyclerView? = null
    private var orderedCountries: ArrayList<PremiumSingleCountryData> = ArrayList()
    private var countryName: String? = null
    private val retrofit = ConnectionManager.getInstance().getRetrofit()
    private var loadingDialog: LoadingDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_countries_rank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireActivity())

        orderCountriesByTotalCases()

        //Llamando al RV del fragment
        rviCompetitions = view.findViewById(R.id.rviCountriesRank)

        val countriesRVAdapter = CountriesRankRVAdapter(orderedCountries, this, view.context)
        rviCompetitions!!.adapter = countriesRVAdapter

    }

    // Se ordena la lista de paises de acuerdo a los casos por millon
    private fun orderCountriesByTotalCases() {

        val countries = PremiumGlobalDataInfo.premiumCountriesData!!
        val cases: ArrayList<Double> = ArrayList()

        for (i in 1..countries.size) {
            cases.add(countries[i-1].TotalCasesPerMillion)
        }

        cases.sort()

        //Se excluye a los paises con 0 casos
        for (i in 1..countries.size) {
            for (j in 1..countries.size) {
                if (countries[j-1].TotalCasesPerMillion == cases[i-1] && countries[j-1].TotalCases.toInt() != 0)
                    orderedCountries.add(countries[j-1])
            }
        }
    }

    // Se solicita la informacion historica del pais seleccionado
    private fun searchSingleCountryHistoricalData() {

        loadingDialog!!.startLoading()
        lifecycleScope.launch {
            val call = retrofit.create(CovidAPIService::class.java).getCountryHistoricalStats("/country/$countryName")

            if (call.isSuccessful) {
                SingleCountryHistoricalStats.countryHistoricalData = call.body()
                startActivity(Intent(context,SingleCountryActivity::class.java))
                loadingDialog!!.isDismiss()
                Log.i("RequestHeaders", call.raw().toString())
            }
        }
    }

    //Cuando se hace click en un pais
    override fun onClick(country: PremiumSingleCountryData) {

        //Actualizando el Singleton con la info del pais seleccionado
        PremiumSingleCountryStats.country = country
        //Para obtener el slug
        countryName = country.Country.replace(" ", "-").lowercase()


        if (InternetConnection.isConnected) {
            searchSingleCountryHistoricalData()
        } else {
            startActivity(Intent(context,SingleCountryActivity::class.java))
        }
    }
}