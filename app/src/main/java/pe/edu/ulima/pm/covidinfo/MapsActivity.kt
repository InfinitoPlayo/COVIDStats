package pe.edu.ulima.pm.covidinfo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import pe.edu.ulima.pm.covidinfo.objects.NovelCOVIDCountries
import java.text.DecimalFormat


class MapsActivity: AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var novelCOVIDCountries = NovelCOVIDCountries.countries
    private var maxCases: Long = 0

    private lateinit var redIcon: BitmapDescriptor
    private lateinit var greenIcon: BitmapDescriptor
    private lateinit var yellowIcon: BitmapDescriptor
    private var bottomBar: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        setIcons()
        setFragment()

        //Seteando el BottomNavigationView
        bottomBar = findViewById(R.id.bnvMaps)
        bottomBar!!.setOnItemSelectedListener {

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
                    //startActivity(Intent(this, MapsActivity::class.java))
                }
            }
            true
        }
    }

    private fun setFragment() {
        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.ftMaps) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Para obtener numero maximo de casos
        novelCOVIDCountries.forEach {
            if (it.cases > maxCases) maxCases = it.cases
        }
        //Insertar marcadores al mapa
        novelCOVIDCountries.forEach {
            setMarker(it.countryInfo.lat, it.countryInfo.long, it.country, it.cases, it.casesPerOneMillion.toDouble())
            setCircle(it.countryInfo.lat, it.countryInfo.long, it.cases)
        }
    }

    private fun setMarker(lat: Double, lon:Double, country: String, cases: Long, casesPerMillion: Double) {
        val coordinates = LatLng(lat, lon)
        val df = DecimalFormat("###,###,###")
        map.addMarker(MarkerOptions()
            .position(coordinates)
            .title("$country: ${df.format(cases)} cases ") // \d ${df.format(casesPerMillion)}
            .icon(chooseIcon(casesPerMillion)))
    }

    private fun setCircle(lat: Double, lon:Double, cases: Long) {
        val coordinates = LatLng(lat, lon)
        map.addCircle(CircleOptions()
            .center(coordinates)
            .radius(calculateCircleSize(cases))
            .strokeColor(Color.BLACK)
            .fillColor(0x30ff0000)
            .strokeWidth(2f))
    }

    private fun calculateCircleSize(cases: Long): Double {
        return if (cases.toDouble() / maxCases.toDouble() < 0.02) {
            0.02 * 1000000.0
        } else {
            (cases.toDouble() / maxCases.toDouble()) * 1000000.0
        }
    }

    private fun setIcons() {
        val r = BitmapFactory.decodeResource(resources, R.drawable.map_pin_2)
        val smallMarkerR = Bitmap.createScaledBitmap(r, 80, 80, false)
        redIcon = BitmapDescriptorFactory.fromBitmap(smallMarkerR)

        val g = BitmapFactory.decodeResource(resources, R.drawable.map_pin_green)
        val smallMarkerG = Bitmap.createScaledBitmap(g, 80, 80, false)
        greenIcon = BitmapDescriptorFactory.fromBitmap(smallMarkerG)

        val y = BitmapFactory.decodeResource(resources, R.drawable.map_pin_yellow)
        val smallMarkerY = Bitmap.createScaledBitmap(y, 80, 80, false)
        yellowIcon = BitmapDescriptorFactory.fromBitmap(smallMarkerY)
    }

    private fun chooseIcon(totalCasesPerMillion: Double): BitmapDescriptor {
          if (totalCasesPerMillion < 20000.0) return greenIcon
          if (totalCasesPerMillion < 80000.0) return yellowIcon
          return redIcon
    }

}