package pe.edu.ulima.pm.covidinfo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import pe.edu.ulima.pm.covidinfo.fragments.SingleCountryActiveGraphFragment
import pe.edu.ulima.pm.covidinfo.fragments.SingleCountryTotalGraphFragment
import pe.edu.ulima.pm.covidinfo.fragments.SingleCountryPiechartFragment
import pe.edu.ulima.pm.covidinfo.objects.PremiumSingleCountryStats
import pe.edu.ulima.pm.covidinfo.objects.Test

class SingleCountryActivity: AppCompatActivity() {

    private var tviTitle: TextView? = null

    private var toolbar: androidx.appcompat.widget.Toolbar? = null
    private var fragments: ArrayList<Fragment> = ArrayList()
    private var dlaSingleCountry: DrawerLayout? = null
    private var nviSingleCountry: NavigationView? = null
    private var bottomBar: BottomNavigationView? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_country)

        tviTitle = findViewById(R.id.tviSingleCountryTitle)
        tviTitle!!.text = "Stats for ${PremiumSingleCountryStats.country!!.Country}"

        fragments.add(SingleCountryPiechartFragment())
        fragments.add(SingleCountryTotalGraphFragment())
        fragments.add(SingleCountryActiveGraphFragment())

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

        bottomBar.setOnItemReselectedListener {

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
    }

    @SuppressLint("SetTextI18n")
    private fun setNavigationView(nvi: NavigationView) {

        //Se configura un listener en la barra de navegacion para que cambie de Fragment segun se solicite
        nvi.setNavigationItemSelectedListener { item: MenuItem ->
            Test.isFirstTime = 1
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
            }

            ft.addToBackStack(null)
            ft.commit()
            dlaSingleCountry!!.closeDrawers()
            true
        }

    }

}