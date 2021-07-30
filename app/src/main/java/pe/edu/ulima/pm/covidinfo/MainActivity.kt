package pe.edu.ulima.pm.covidinfo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import pe.edu.ulima.pm.covidinfo.fragments.GlobalInfoFragment
import pe.edu.ulima.pm.covidinfo.fragments.LoadingFragment
import pe.edu.ulima.pm.covidinfo.managers.CovidInfoManager
import pe.edu.ulima.pm.covidinfo.managers.CovidAPIConnectionManager
import pe.edu.ulima.pm.covidinfo.managers.IPAPIConnectionManager
import pe.edu.ulima.pm.covidinfo.models.AppDatabase
import pe.edu.ulima.pm.covidinfo.models.dao.*
import pe.edu.ulima.pm.covidinfo.models.dao.PremiumGlobalData
import pe.edu.ulima.pm.covidinfo.models.dao.PremiumSingleCountryData
import pe.edu.ulima.pm.covidinfo.models.persistence.dao.CountryDAO
import pe.edu.ulima.pm.covidinfo.models.persistence.dao.DateDAO
import pe.edu.ulima.pm.covidinfo.models.persistence.dao.FavoriteDAO
import pe.edu.ulima.pm.covidinfo.models.persistence.dao.GlobalDAO
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.DateEntity
import pe.edu.ulima.pm.covidinfo.objects.*
import java.io.Serializable

class MainActivity : AppCompatActivity(), Serializable {

    private var toolbar: androidx.appcompat.widget.Toolbar? = null
    private var fragments: ArrayList<Fragment> = ArrayList()
    private var globalData: GlobalData? = null //Datos globales de covid + lista de paises con info completa
    private var premiumCountriesDataList: ArrayList<PremiumSingleCountryData> = ArrayList() //Lista de paises con info premium completa
    private val retrofit = CovidAPIConnectionManager.getInstance().getRetrofit()
    private val retrofit2 = IPAPIConnectionManager.getInstance().getRetrofit()
    private var bottomBar: BottomNavigationView? = null

    //Para instanciar los DAO
    private var countryDAO: CountryDAO? = null
    private var globalDAO: GlobalDAO? = null
    private var dateDAO: DateDAO? = null
    private var favoriteDAO: FavoriteDAO? = null

    private var location: String? = null
    private var locationLowercase: String? = null

    private var dlaMain: DrawerLayout? = null
    private var nviMain: NavigationView? = null

    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getLocationFromAPI()
        loadingDialog = LoadingDialog(this)

        fragments.add(LoadingFragment())
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flaMain, fragments[0])
        ft.addToBackStack(null)
        ft.commit()

        //Seteando el NVI
        nviMain =findViewById(R.id.nviMain)
        dlaMain =findViewById(R.id.dlaMain)
        setNavigationView(nviMain!!)

        //Llamando a los DAO de las 3 entidades
        countryDAO = AppDatabase.getInstance(this).countryDAO
        globalDAO = AppDatabase.getInstance(this).globalDAO
        dateDAO = AppDatabase.getInstance(this).dateDAO
        favoriteDAO = AppDatabase.getInstance(this).favoriteDAO

        //Si hay conexion a internet, se recibe data actualizada del API
        if (CovidInfoManager.getInstance().verifyAvailableNetwork(this)) {

            searchGlobalData()
            searchPremiumGlobalData()

            fragments.add(GlobalInfoFragment())
            setFragments(fragments[0], fragments[1])

        //Si no hay internet, se carga data de Room
        } else {
            lifecycleScope.launch {
                InternetConnection.isConnected = false
                CovidInfoManager.getInstance().getCountriesFromRoom(this@MainActivity)
                CovidInfoManager.getInstance().getGlobalFromRoom(this@MainActivity)
                CovidInfoManager.getInstance().getDateFromRoom(this@MainActivity)
                fragments.add(GlobalInfoFragment())

                setFragments(fragments[0], fragments[1])
            }
        }

        //Seteando el BottomNavigationView
        bottomBar = findViewById(R.id.bnvMain)
        bottomBar!!.setOnItemReselectedListener {

            when (it.itemId) {
                //Click en el icono Home
                R.id.ic_home ->  {
                    startActivity(Intent(this, MainActivity::class.java))
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

        //Seteando el toolbar
        toolbar = findViewById(R.id.tbaMain)
        setSupportActionBar(toolbar)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main_menu, menu)
        return true
    }

    // Obtener datos de https://api.covid19api.com/summary
    private fun searchGlobalData() {

        lifecycleScope.launch {
            val call = retrofit.create(CovidAPIService::class.java).getGlobalData("/summary")

            // Si hay conexion con el API
            if (call.isSuccessful) {
                globalData = call.body()
                GlobalDataInfo.globalData = call.body()
                CovidInfoManager.getInstance().setGlobalStats(globalData!!.Global)

                //Insertar los datos globales en la BD
                val global = CovidInfoManager.getInstance().setGlobalEntity(GlobalDataInfo.globalData!!.Global)
                globalDAO!!.insertGlobal(global)

                Log.i("MainActivity", call.raw().toString())

            } else {
                Log.i("MainActivity", call.errorBody().toString())
            }
        }
    }

    // Obtener datos de https://api.covid19api.com/premium/summary
    private fun searchPremiumGlobalData() {

        lifecycleScope.launch {
            val call = retrofit.create(CovidAPIService::class.java).getPremiumGlobalData("/premium/summary")

            // Si se devuelve data
            if (call.isSuccessful) {
                PremiumGlobalDataInfo.premiumGlobalData = call.body()
                Log.i("MainActivity", call.raw().toString())

                setPremiumCountriesData(PremiumGlobalDataInfo.premiumGlobalData!!)
                PremiumGlobalDataInfo.premiumCountriesData = premiumCountriesDataList

                //Insertar los paises en la BD
                PremiumGlobalDataInfo.premiumCountriesData!!.forEach {
                    val country = CovidInfoManager.getInstance().setCountryEntity(it)
                    countryDAO!!.insertCountry(country)
                }

                //Eliminar paises desactualizados
                removeOutdatedCountries(this@MainActivity)

                //Insertar la fecha de actualizacion en la BD
                val date = DateEntity(0, PremiumGlobalDataInfo.premiumGlobalData!!.Date)
                dateDAO!!.insertDate(date)

                //
                PremiumSingleCountryStats.country = countryDAO!!.getSingleCountry(location!!)

                //Iniciar el fragment GlobalInfoFragment
                fragments.add(GlobalInfoFragment())
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.flaMain, fragments[1])
                ft.addToBackStack(null)
                ft.commit()

            }else {
                Log.i("MainActivity", call.errorBody().toString())
            }
        }
    }

    private fun getLocationFromAPI() {

        lifecycleScope.launch {
            val call = retrofit2.create(IPAPIService::class.java).getLocation("/json")

            if (call.isSuccessful) {
                Log.i("location", call.body().toString())
                location = call.body()!!.country
                locationLowercase = location!!.replace(" ", "-").lowercase()

            } else {
                Log.i("MainActivity", call.errorBody().toString())
            }
        }
    }

    private fun setPremiumCountriesData(premiumGlobalData: PremiumGlobalData) {
        for (i in premiumGlobalData.Countries) {
            premiumCountriesDataList.add(i)
        }
    }

    private fun setFragments(f0: Fragment, f1: Fragment) {

        val ft = supportFragmentManager.beginTransaction()
        if (FirstTime.isFirstTime == 1) {
            ft.replace(R.id.flaMain, f0)
            FirstTime.isFirstTime = 2
        }
        else {
            ft.replace(R.id.flaMain, f1)
        }
        ft.addToBackStack(null)
        ft.commit()
    }

    private suspend fun removeOutdatedCountries(context: Context) {

        val countryDAO = AppDatabase.getInstance(context).countryDAO
        val countries = countryDAO.getAllCountries()

        for (i in 0..countries.size) {
            if (countries.size > 300) {
                if (i < countries.size - 218) {
                    countryDAO.deleteSingleCountry(countries[i].ID)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setNavigationView(nvi: NavigationView) {

        //Se configura un listener en la barra de navegacion para que cambie de Fragment segun se solicite
        nvi.setNavigationItemSelectedListener { item: MenuItem ->

            item.isChecked = true
            val ft = supportFragmentManager.beginTransaction()

            when (item.itemId) {
                R.id.mnuGlobalInfo -> {
                    // Abrir SingleCountryPieChartFragment
                    ft.replace(R.id.flaMain, fragments[1])
                    ft.addToBackStack(null)
                    ft.commit()
                }
                R.id.mnuMyCountry -> {
                    // Abrir SingleCountryTotalPieChartFragment
                    searchSingleCountryHistoricalData()
                }

            }
            true
        }

    }

    // Se solicita la informacion historica del pais
    private fun searchSingleCountryHistoricalData() {
        loadingDialog!!.startLoading()
        lifecycleScope.launch {
            val call = retrofit.create(CovidAPIService::class.java).getCountryHistoricalStats("/country/$locationLowercase")

            if (call.isSuccessful) {
                SingleCountryHistoricalStats.countryHistoricalData = call.body()
                loadingDialog!!.isDismiss()
                startActivity(Intent(this@MainActivity, SingleCountryActivity::class.java))
                Log.i("RequestHeaders", call.raw().toString())
            }
        }
    }

}