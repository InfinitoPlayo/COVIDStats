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
import pe.edu.ulima.pm.covidinfo.managers.CovidAPIConnectionManager
import pe.edu.ulima.pm.covidinfo.managers.CovidInfoManager
import pe.edu.ulima.pm.covidinfo.models.AppDatabase
import pe.edu.ulima.pm.covidinfo.models.dao.CovidAPIService
import pe.edu.ulima.pm.covidinfo.models.persistence.dao.CountryDAO
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.CountryEntity
import pe.edu.ulima.pm.covidinfo.objects.InternetConnection
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
    private val retrofit = CovidAPIConnectionManager.getInstance().getRetrofit()
    private var loadingDialog: LoadingDialog? = null
    private val displayList: ArrayList<CountryEntity> = ArrayList()
    private var countryEntityList: ArrayList<CountryEntity> = ArrayList()
    private var countryDAO: CountryDAO? = null

    private var intentSingleCountry: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countries_info)

        //Llamando al RV del fragment
        rviCompetitions = findViewById(R.id.rviCountriesInfo)

        countryDAO = AppDatabase.getInstance(this).countryDAO
        lifecycleScope.launch {
            countryEntityList = removeEmptyCountries(ArrayList(countryDAO!!.getAllCountries()))
            Log.i("CountryEntityList", countryEntityList.toString())

            loadingDialog = LoadingDialog(this@CountriesInfoActivity)
            displayList.addAll(countryEntityList)

            val countriesRVAdapter = CountriesInfoRVAdapter(displayList, this@CountriesInfoActivity, this@CountriesInfoActivity)
            rviCompetitions!!.adapter = countriesRVAdapter
        }

        title = findViewById(R.id.tviCountriesInfoTitle)

        //Seteando el toolbar
        toolbar = findViewById(R.id.tbaCountriesInfo)
        setSupportActionBar(toolbar)

        intentSingleCountry = Intent(this, SingleCountryActivity::class.java)

        //Seteando el BottomNavigationView
        bottomBar = findViewById(R.id.bnvCountriesInfo)
        bottomBar!!.setOnItemSelectedListener {

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
            true
        }

    }

    // Devuelve una lista sin paises con 0 casos
    private fun removeEmptyCountries(countries: ArrayList<CountryEntity>): ArrayList<CountryEntity> {

        val validCountries: ArrayList<CountryEntity> = ArrayList()
        countries.forEach {
            if (it.TotalCases.toInt() > 0) {
                validCountries.add(it)
            }
        }
        return validCountries
    }

    // Se solicita la informacion historica del pais seleccionado
    private fun searchSingleCountryHistoricalData() {
        loadingDialog!!.startLoading()
        lifecycleScope.launch {
            val call = retrofit.create(CovidAPIService::class.java).getCountryHistoricalStats("/country/$countryName")

            if (call.isSuccessful) {
                SingleCountryHistoricalStats.countryHistoricalData = call.body()
                loadingDialog!!.isDismiss()
                startActivity(intentSingleCountry)
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

        // Para obtener el nombre del pais en minusculas y sin espacios
        countryName = country.Country.replace(" ", "-").lowercase()

        if (CovidInfoManager.getInstance().verifyAvailableNetwork(this)) {
            intentSingleCountry!!.putExtra("IsConnected", "true")
            searchSingleCountryHistoricalData()
        } else {
            intentSingleCountry!!.putExtra("IsConnected", "false")
            startActivity(intentSingleCountry)
        }
    }

}