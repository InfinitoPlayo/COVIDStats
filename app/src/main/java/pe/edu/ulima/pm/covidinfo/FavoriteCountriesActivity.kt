package pe.edu.ulima.pm.covidinfo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import pe.edu.ulima.pm.covidinfo.dialogues.LoadingDialog
import pe.edu.ulima.pm.covidinfo.fragments.FavoriteCountriesFragment
import pe.edu.ulima.pm.covidinfo.managers.CovidInfoManager
import pe.edu.ulima.pm.covidinfo.objects.InternetConnection

class FavoriteCountriesActivity: AppCompatActivity() {

    private var bottomBar: BottomNavigationView? = null
    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_countries)

        loadingDialog = LoadingDialog(this)

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
                //Click en el icono de maps
                R.id.ic_worldmap -> {
                    if (CovidInfoManager.getInstance().verifyAvailableNetwork(this)) {
                        lifecycleScope.launch {
                            loadingDialog!!.startLoading()
                            CovidInfoManager.getInstance().getNovelCOVIDCountries()
                            loadingDialog!!.isDismiss()
                            startActivity(Intent(this@FavoriteCountriesActivity, MapsActivity::class.java))
                        }
                    } else {
                        Toast.makeText(this, "Internet connection is needed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            true
        }

        InternetConnection.isConnected = CovidInfoManager.getInstance().verifyAvailableNetwork(this)

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flaFavoriteCountries, FavoriteCountriesFragment())
        ft.addToBackStack(null)
        ft.commit()

    }

}