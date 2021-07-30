package pe.edu.ulima.pm.covidinfo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import pe.edu.ulima.pm.covidinfo.adapters.CountriesRankRVAdapter
import pe.edu.ulima.pm.covidinfo.adapters.OnCountryRankItemClickListener
import pe.edu.ulima.pm.covidinfo.managers.CovidAPIConnectionManager
import pe.edu.ulima.pm.covidinfo.models.AppDatabase
import pe.edu.ulima.pm.covidinfo.models.dao.CovidAPIService
import pe.edu.ulima.pm.covidinfo.models.persistence.dao.CountryDAO
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.CountryEntity
import pe.edu.ulima.pm.covidinfo.objects.InternetConnection
import pe.edu.ulima.pm.covidinfo.objects.PremiumSingleCountryStats
import pe.edu.ulima.pm.covidinfo.objects.SingleCountryHistoricalStats
import java.util.*
import kotlin.collections.ArrayList

class CountriesRankActivity: AppCompatActivity(), OnCountryRankItemClickListener {

    private var toolbar: androidx.appcompat.widget.Toolbar? = null
    private var bottomBar: BottomNavigationView? = null
    private var rviCompetitions: RecyclerView? = null
    private var countryName: String? = null
    private val retrofit = CovidAPIConnectionManager.getInstance().getRetrofit()
    private var loadingDialog: LoadingDialog? = null
    private val displayList: ArrayList<CountryEntity> = ArrayList()

    private var countryEntityList: ArrayList<CountryEntity> = ArrayList()
    private var countryDAO: CountryDAO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countries_rank)

        //Llamando al RV del fragment
        rviCompetitions = findViewById(R.id.rviCountriesRank)

        countryDAO = AppDatabase.getInstance(this).countryDAO
        lifecycleScope.launch {
            countryEntityList = orderCountriesByTotalCases(ArrayList(countryDAO!!.getAllCountries()))

            loadingDialog = LoadingDialog(this@CountriesRankActivity)
            displayList.addAll(countryEntityList)

            val countriesRVAdapter = CountriesRankRVAdapter(displayList, this@CountriesRankActivity, this@CountriesRankActivity)
            rviCompetitions!!.adapter = countriesRVAdapter
        }

        //Seteando el toolbar
        toolbar = findViewById(R.id.tbaMain)
        setSupportActionBar(toolbar)

        //Seteando el BottomNavigationView
        bottomBar = findViewById(R.id.bnvCountriesRank)
        bottomBar!!.setOnItemReselectedListener {

            when (it.itemId) {
                //Click en el icono Home
                R.id.ic_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                //Click en el icono de lista de paises
                R.id.ic_countries -> {
                    startActivity(Intent(this, CountriesInfoActivity::class.java))
                }
                //Click en el icono de ranking de paises
                R.id.ic_ranking -> {
                    startActivity(Intent(this, CountriesRankActivity::class.java))
                }
                //Click en el icono de favoritos
                R.id.ic_favorites -> {
                    startActivity(Intent(this, FavoriteCountriesActivity::class.java))
                }
            }
        }

        val countriesRVAdapter = CountriesRankRVAdapter(displayList, this, this)
        rviCompetitions!!.adapter = countriesRVAdapter

    }

    // Se ordena la lista de paises de acuerdo a los casos por millon
    private fun orderCountriesByTotalCases(countries: ArrayList<CountryEntity>): ArrayList<CountryEntity>{

        val cases: ArrayList<Double> = ArrayList()
        val orderedCountries: ArrayList<CountryEntity> = ArrayList()

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
        return orderedCountries
    }

    // Se solicita la informacion historica del pais seleccionado
    private fun searchSingleCountryHistoricalData() {

        loadingDialog!!.startLoading()
        lifecycleScope.launch {
            val call = retrofit.create(CovidAPIService::class.java).getCountryHistoricalStats("/country/$countryName")

            if (call.isSuccessful) {
                SingleCountryHistoricalStats.countryHistoricalData = call.body()
                startActivity(Intent(this@CountriesRankActivity, SingleCountryActivity::class.java))
                loadingDialog!!.isDismiss()
                Log.i("RequestHeaders", call.raw().toString())
            }
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
                    countryEntityList.forEach {
                        // Si la busqueda coincide con el nombre de algun pais
                        if (it.Country.lowercase(Locale.getDefault()).contains(search)) {
                            displayList.add(it)
                        }
                    }
                    rviCompetitions!!.adapter!!.notifyDataSetChanged()

                } else {
                    displayList.clear()
                    displayList.addAll(countryEntityList)
                    rviCompetitions!!.adapter!!.notifyDataSetChanged()
                }
                return true
            }
        })
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onClick(country: CountryEntity) {
        //Actualizando el Singleton con la info del pais seleccionado
        PremiumSingleCountryStats.country = country

        //Para obtener el slug
        countryName = country.Country.replace(" ", "-").lowercase()

        if (InternetConnection.isConnected) {
            searchSingleCountryHistoricalData()
        } else {
            startActivity(Intent(this,SingleCountryActivity::class.java))
        }
    }

}