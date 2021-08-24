package pe.edu.ulima.pm.covidinfo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import pe.edu.ulima.pm.covidinfo.dialogues.LoadingDialog
import pe.edu.ulima.pm.covidinfo.fragments.SingleCountryActiveGraphFragment
import pe.edu.ulima.pm.covidinfo.fragments.SingleCountryExtendedGraphFragment
import pe.edu.ulima.pm.covidinfo.fragments.SingleCountryPiechartFragment
import pe.edu.ulima.pm.covidinfo.fragments.SingleCountryTotalGraphFragment
import pe.edu.ulima.pm.covidinfo.managers.CovidInfoManager
import pe.edu.ulima.pm.covidinfo.objects.InternetConnection
import pe.edu.ulima.pm.covidinfo.objects.PremiumSingleCountryStats

class SingleCountryActivity: AppCompatActivity() {

    private var tviTitle: TextView? = null
    private var toolbar: androidx.appcompat.widget.Toolbar? = null
    private var fragments: ArrayList<Fragment> = ArrayList()
    private var dlaSingleCountry: DrawerLayout? = null
    private var nviSingleCountry: NavigationView? = null
    private var bottomBar: BottomNavigationView? = null
    private var loadingDialog: LoadingDialog? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_country)

        tviTitle = findViewById(R.id.tviSingleCountryTitle)
        tviTitle!!.text = "Stats for ${PremiumSingleCountryStats.country!!.Country}"

        fragments.add(SingleCountryPiechartFragment())
        fragments.add(SingleCountryTotalGraphFragment())
        fragments.add(SingleCountryActiveGraphFragment())
        fragments.add(SingleCountryExtendedGraphFragment())

        loadingDialog = LoadingDialog(this)

        //Seteando el toolbar
        toolbar = findViewById(R.id.tbaMain)
        setSupportActionBar(toolbar)

        //Seteando el BottomNavigationView
        bottomBar = findViewById(R.id.bnvSingleCountry)
        setBottomNavigationView(bottomBar!!)

        //Seteando el NVI
        nviSingleCountry =findViewById(R.id.nviSingleCountry)
        dlaSingleCountry =findViewById(R.id.dlaSingleCountry)
        setNavigationView(nviSingleCountry!!)

        if (CovidInfoManager.getInstance().verifyAvailableNetwork(this)) {
            InternetConnection.isConnected
        } else {
            InternetConnection.isConnected = false
        }

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flaSingleCountry, fragments[0])
        ft.addToBackStack(null)
        ft.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main_menu, menu)
        return true
    }

    private fun setBottomNavigationView (bottomBar: BottomNavigationView) {

        bottomBar.setOnItemSelectedListener {

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
                //Click en el icono de maps
                R.id.ic_worldmap -> {
                    if (CovidInfoManager.getInstance().verifyAvailableNetwork(this)) {
                        lifecycleScope.launch {
                            loadingDialog!!.startLoading()
                            CovidInfoManager.getInstance().getNovelCOVIDCountries()
                            loadingDialog!!.isDismiss()
                            startActivity(Intent(this@SingleCountryActivity, MapsActivity::class.java))
                        }
                    } else {
                        Toast.makeText(this, "Internet connection is needed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setNavigationView(nvi: NavigationView) {

        //Se configura un listener en la barra de navegacion para que cambie de Fragment segun se solicite
        nvi.setNavigationItemSelectedListener { item: MenuItem ->
            item.isChecked = true
            val ft = supportFragmentManager.beginTransaction()

            when (item.itemId) {
                R.id.mnuPiechart -> {
                    // Abrir SingleCountryPiechartFragment
                    ft.replace(R.id.flaSingleCountry, fragments[0])
                    tviTitle!!.text = "Stats for ${PremiumSingleCountryStats.country!!.Country}"
                }
                R.id.mnuGraph -> {
                    // Abrir SingleCountryTotalGraphFragment
                    ft.replace(R.id.flaSingleCountry, fragments[1])
                    tviTitle!!.text = "${PremiumSingleCountryStats.country!!.Country} total cases graph"
                }
                R.id.mnuActiveGraph -> {
                    // Abrir SingleCountryNewGraphFragment
                    ft.replace(R.id.flaSingleCountry, fragments[2])
                    tviTitle!!.text = "${PremiumSingleCountryStats.country!!.Country} active cases graph"
                }
                R.id.mnuExtendedGraph -> {
                    // Abrir SingleCountryExtendedGraphFragment
                    ft.replace(R.id.flaSingleCountry, fragments[3])
                    tviTitle!!.text = "${PremiumSingleCountryStats.country!!.Country} active cases graph (historical)"
                }
            }
            ft.addToBackStack(null)
            ft.commit()
            dlaSingleCountry!!.closeDrawers()
            true
        }
    }

}