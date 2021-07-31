package pe.edu.ulima.pm.covidinfo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import pe.edu.ulima.pm.covidinfo.fragments.FavoriteCountriesFragment

class FavoriteCountriesActivity: AppCompatActivity() {

    private var bottomBar: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_countries)

        //Seteando el BottomNavigationView
        bottomBar = findViewById(R.id.bnvFavoriteCountries)
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

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flaFavoriteCountries, FavoriteCountriesFragment())
        ft.addToBackStack(null)
        ft.commit()

    }

}