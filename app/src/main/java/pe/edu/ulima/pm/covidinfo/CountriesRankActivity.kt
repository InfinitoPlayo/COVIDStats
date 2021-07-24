package pe.edu.ulima.pm.covidinfo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import pe.edu.ulima.pm.covidinfo.fragments.CountriesRankFragment

class CountriesRankActivity: AppCompatActivity() {

    private var toolbar: androidx.appcompat.widget.Toolbar? = null
    private var bottomBar: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countries_rank)

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
        ft.replace(R.id.flaCountriesRank, CountriesRankFragment())
        ft.addToBackStack(null)
        ft.commit()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main_menu, menu)
        return true
    }

}