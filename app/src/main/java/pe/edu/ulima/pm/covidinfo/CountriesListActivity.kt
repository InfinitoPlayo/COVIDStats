package pe.edu.ulima.pm.covidinfo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import pe.edu.ulima.pm.covidinfo.fragments.CountriesInfoFragment
import pe.edu.ulima.pm.covidinfo.fragments.SingleCountryPiechartFragment

class CountriesListActivity: AppCompatActivity() {

    private var toolbar: androidx.appcompat.widget.Toolbar? = null
    private var fragments: ArrayList<Fragment> = ArrayList()
    private var bottomBar: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countries_list)

        fragments.add(CountriesInfoFragment())
        fragments.add(SingleCountryPiechartFragment())

        //Seteando el toolbar
        toolbar = findViewById(R.id.tbaMain)
        setSupportActionBar(toolbar)

        //Seteando el BottomNavigationView
        bottomBar = findViewById(R.id.bnvCountriesList)
        bottomBar!!.setOnItemReselectedListener {

            when (it.itemId) {
                //Click en el icono Home
                R.id.ic_home -> {
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

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flaCountriesList, fragments[0])
        ft.addToBackStack(null)
        ft.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main_menu, menu)
        return true
    }

}