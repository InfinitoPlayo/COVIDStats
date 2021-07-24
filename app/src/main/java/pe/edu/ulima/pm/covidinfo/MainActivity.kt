package pe.edu.ulima.pm.covidinfo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import pe.edu.ulima.pm.covidinfo.fragments.GlobalInfoFragment
import pe.edu.ulima.pm.covidinfo.fragments.SynchronizeFragment
import pe.edu.ulima.pm.covidinfo.managers.CovidInfoManager
import pe.edu.ulima.pm.covidinfo.managers.ConnectionManager
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

class MainActivity : AppCompatActivity() {

    private var toolbar: androidx.appcompat.widget.Toolbar? = null
    private var fragments: ArrayList<Fragment> = ArrayList()
    private var globalData: GlobalData? = null //Datos globales de covid + lista de paises con info completa
    private var premiumCountriesDataList: ArrayList<PremiumSingleCountryData> = ArrayList() //Lista de paises con info premium completa
    private val retrofit = ConnectionManager.getInstance().getRetrofit()
    private var bottomBar: BottomNavigationView? = null

    //Para instanciar los DAO
    private var countryDAO: CountryDAO? = null
    private var globalDAO: GlobalDAO? = null
    private var dateDAO: DateDAO? = null
    private var favoriteDAO: FavoriteDAO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragments.add(SynchronizeFragment())
        fragments.add(GlobalInfoFragment())

        //Llamando a los DAO de las 3 entidades
        countryDAO = AppDatabase.getInstance(this).countryDAO
        globalDAO = AppDatabase.getInstance(this).globalDAO
        dateDAO = AppDatabase.getInstance(this).dateDAO
        favoriteDAO = AppDatabase.getInstance(this).favoriteDAO

        //Si hay conexion a internet, se recibe data actualizada del API
        if (CovidInfoManager.getInstance().verifyAvailableNetwork(this)) {
            searchGlobalData()
            searchPremiumGlobalData()

            //Si no hay internet, se carga data de Room
        } else {
            lifecycleScope.launch {
                InternetConnection.isConnected = false
                CovidInfoManager.getInstance().getCountriesFromRoom(this@MainActivity)
                CovidInfoManager.getInstance().getGlobalFromRoom(this@MainActivity)
                CovidInfoManager.getInstance().getDateFromRoom(this@MainActivity)
            }
        }

        //Seteando el BottomNavigationView
        bottomBar = findViewById(R.id.bnvMain)
        bottomBar!!.setOnItemReselectedListener {

            when (it.itemId) {
                //Click en el icono Home
                R.id.ic_home ->  {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                //Click en el icono de lista de paises
                R.id.ic_countries -> {
                    val intent = Intent(this, CountriesListActivity::class.java)
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

        //Seteando el toolbar
        toolbar = findViewById(R.id.tbaMain)
        setSupportActionBar(toolbar)

        //Asignando al fragment SynchronizeFragment
        val ft = supportFragmentManager.beginTransaction()
        if (FirstTime.isFirstTime == 1) {
            ft.replace(R.id.flaMain, fragments[0])
            FirstTime.isFirstTime = 2
        }
        else {
            ft.replace(R.id.flaMain, fragments[1])
        }
        ft.addToBackStack(null)
        ft.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main_menu, menu)
        return true
    }

    // Obtener datos de https://api.covid19api.com/summary
    private fun searchGlobalData() {

        lifecycleScope.launch {
            val call =
                retrofit.create(CovidAPIService::class.java).getGlobalData("/summary")

            // Si hay conexion con el API
            if (call.isSuccessful) {
                globalData = call.body()
                GlobalDataInfo.globalData = call.body()
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

                //Insertar los datos globales en la BD
                val global = CovidInfoManager.getInstance().setGlobalEntity(GlobalDataInfo.globalData!!.Global)
                globalDAO!!.insertGlobal(global)

                //Insertar la fecha de actualizacion en la BD
                val date = DateEntity(0, PremiumGlobalDataInfo.premiumGlobalData!!.Date)
                dateDAO!!.insertDate(date)

            }else {
                Log.i("MainActivity", call.errorBody().toString())
            }
        }
    }

    private fun setPremiumCountriesData(premiumGlobalData: PremiumGlobalData) {
        for (i in premiumGlobalData.Countries) {
            premiumCountriesDataList.add(i)
        }
    }

}