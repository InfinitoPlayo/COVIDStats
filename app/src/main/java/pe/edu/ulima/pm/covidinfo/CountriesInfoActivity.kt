package pe.edu.ulima.pm.covidinfo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import pe.edu.ulima.pm.covidinfo.adapters.CountriesInfoRVAdapter
import pe.edu.ulima.pm.covidinfo.adapters.OnCountryInfoItemClickListener
import pe.edu.ulima.pm.covidinfo.managers.ConnectionManager
import pe.edu.ulima.pm.covidinfo.models.dao.CovidAPIService
import pe.edu.ulima.pm.covidinfo.models.dao.PremiumSingleCountryData
import pe.edu.ulima.pm.covidinfo.objects.InternetConnection
import pe.edu.ulima.pm.covidinfo.objects.PremiumGlobalDataInfo
import pe.edu.ulima.pm.covidinfo.objects.PremiumSingleCountryStats
import pe.edu.ulima.pm.covidinfo.objects.SingleCountryHistoricalStats
import java.util.*
import kotlin.collections.ArrayList

class CountriesInfoActivity: AppCompatActivity(), OnCountryInfoItemClickListener {

    private var title: TextView? = null
    private var toolbar: androidx.appcompat.widget.Toolbar? = null
    private var bottomBar: BottomNavigationView? = null

    private var rviCompetitions: RecyclerView? = null
    private var countryName: String? = null
    private val retrofit = ConnectionManager.getInstance().getRetrofit()
    private var loadingDialog: LoadingDialog? = null

    private val displayList: ArrayList<PremiumSingleCountryData> = ArrayList()
    private val countriesInfoList = removeEmptyCountries()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countries_info)

        title = findViewById(R.id.tviCountriesInfoTitle)

        //Seteando el toolbar
        toolbar = findViewById(R.id.tbaCountriesInfo)
        setSupportActionBar(toolbar)

        //Seteando el BottomNavigationView
        bottomBar = findViewById(R.id.bnvCountriesInfo)
        bottomBar!!.setOnItemReselectedListener {

            when (it.itemId) {
                //Click en el icono Home
                R.id.ic_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                //Click en el icono de lista de paises
                R.id.ic_countries -> {
                    val intent = Intent(this, CountriesInfoActivity::class.java)
                    startActivity(intent)
                }
                //Click en el icono de ranking de paises
                R.id.ic_ranking -> {
                    val intent = Intent(this, CountriesRankActivity::class.java)
                    startActivity(intent)
                }
                //Click en el icono de favoritos
                R.id.ic_favorites -> {
                    val intent = Intent(this, FavoriteCountriesActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        val countriesInfoList = removeEmptyCountries()
        loadingDialog = LoadingDialog(this)
        displayList.addAll(countriesInfoList)

        //Llamando al RV del fragment
        rviCompetitions = findViewById(R.id.rviCountriesInfo)

        val countriesRVAdapter = CountriesInfoRVAdapter(displayList, this, this)
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
                startActivity(Intent(this@CountriesInfoActivity,SingleCountryActivity::class.java))
                Log.i("RequestHeaders", call.raw().toString())
            }
        }
    }

    override fun onClick(country: PremiumSingleCountryData) {
        //Actualizando el Singleton con la info del pais seleccionado
        PremiumSingleCountryStats.country = country
        // Para obtener el nombre del pais en minusculas y sin espacios
        countryName = country.Country.replace(" ", "-").lowercase()

        if (InternetConnection.isConnected) {
            searchSingleCountryHistoricalData()
        } else {
            startActivity(Intent(this,SingleCountryActivity::class.java))
        }
    }

    // Para implementar la busqueda de paises
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_countries_info_menu, menu)
        val menuItem = menu!!.findItem(R.id.mnuSearch)
        val searchView = menuItem.actionView as SearchView
        searchView.queryHint = "Search for a country"
        //searchView.setBackgroundColor(Color.WHITE)

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText!!.isNotEmpty()) {
                    displayList.clear()
                    val search = newText.lowercase(Locale.getDefault())
                    countriesInfoList.forEach {
                        // Si la busqueda coincide con el nombre de algun pais
                        if (it.Country.lowercase(Locale.getDefault()).contains(search)) {
                            displayList.add(it)
                        }
                    }
                    rviCompetitions!!.adapter!!.notifyDataSetChanged()

                } else {
                    displayList.clear()
                    displayList.addAll(countriesInfoList)
                    rviCompetitions!!.adapter!!.notifyDataSetChanged()
                }
                return true
            }
        })
        return super.onPrepareOptionsMenu(menu)
    }

}