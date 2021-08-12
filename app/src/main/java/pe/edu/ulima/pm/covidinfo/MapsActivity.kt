package pe.edu.ulima.pm.covidinfo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import pe.edu.ulima.pm.covidinfo.models.AppDatabase
import pe.edu.ulima.pm.covidinfo.models.dao.Coordinates
import pe.edu.ulima.pm.covidinfo.models.persistence.dao.CountryDAO
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.CountryEntity
import java.text.DecimalFormat


class MapsActivity: AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var countries: ArrayList<CountryEntity>? = null
    private var countryDAO: CountryDAO? = null
    private var coordinates: ArrayList<Coordinates> = ArrayList()
    private var maxCases: Long = 0
    private lateinit var redIcon: BitmapDescriptor
    private lateinit var greenIcon: BitmapDescriptor
    private lateinit var yellowIcon: BitmapDescriptor
    private var markers: ArrayList<Marker> = ArrayList()
    private var circles: ArrayList<Circle> = ArrayList()
    private var bottomBar: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        countryDAO = AppDatabase.getInstance(this).countryDAO
        setCoordinates()
        setIcons()

        lifecycleScope.launch {
            countries = ArrayList(countryDAO!!.getAllCountries())
            Log.i("MapsActivity", countries!!.size.toString())
            setFragment()
        }

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
                    startActivity(Intent(this, MapsActivity::class.java))
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

        //Ingresando los datos de cada pais al array de coordenadas
        for (i in 1..countries!!.size) {
            coordinates[i-1].country = countries!![i-1].Country
            coordinates[i-1].cases = countries!![i-1].TotalCases
            coordinates[i-1].casesPerMillion = countries!![i-1].TotalCasesPerMillion

            if (countries!![i-1].TotalCases > maxCases) maxCases = countries!![i-1].TotalCases
        }

        coordinates.forEach {
            setMarker(it.lat, it.lon, it.country, it.cases, it.casesPerMillion)
            setCircle(it.lat, it.lon, it.cases)
        }
    }

    private fun setMarker(lat: Double, lon:Double, country: String, cases: Long, casesPerMillion: Double) {
        val coordinates = LatLng(lat, lon)
        val df = DecimalFormat("###,###,###")
        val marker = map.addMarker(MarkerOptions()
            .position(coordinates)
            .title("$country: ${df.format(cases)} cases \n ${df.format(casesPerMillion)}")
            .icon(chooseIcon(casesPerMillion)))

        markers.add(marker!!)
    }

    private fun setCircle(lat: Double, lon:Double, cases: Long) {

        val coordinates = LatLng(lat, lon)
        val circle = map.addCircle(CircleOptions()
        .center(coordinates)
        .radius(calculateCircleSize(cases))
        .strokeColor(Color.BLACK)
        .fillColor(0x30ff0000)
        .strokeWidth(2f))
        circles.add(circle)
    }

    private fun setCoordinates() {
        coordinates.add(Coordinates(33.0,65.0, "", 0,0.0))
        coordinates.add(Coordinates(41.0,20.0, "", 0,0.0))
        coordinates.add(Coordinates(28.0,3.0, "", 0,0.0))
        coordinates.add(Coordinates(42.5, 1.6, "", 0,0.0))
        coordinates.add(Coordinates(-12.5, 18.5, "", 0,0.0))
        coordinates.add(Coordinates(18.25, -63.1667, "", 0,0.0))
        coordinates.add(Coordinates(17.5, -61.8, "", 0,0.0))
        coordinates.add(Coordinates(-34.0, -64.0, "", 0,0.0))
        coordinates.add(Coordinates(40.0,45.0, "", 0,0.0))
        coordinates.add(Coordinates(12.5, -69.9667, "", 0,0.0))
        coordinates.add(Coordinates(-27.0, 133.0, "", 0,0.0))
        coordinates.add(Coordinates(47.3333, 13.3333, "", 0,0.0))
        coordinates.add(Coordinates(40.5, 47.5, "", 0,0.0))
        coordinates.add(Coordinates(24.25, -76.0, "", 0,0.0))
        coordinates.add(Coordinates(26.0, 50.55, "", 0,0.0))
        coordinates.add(Coordinates(24.0, 90.0, "", 0,0.0))
        coordinates.add(Coordinates(13.1667, -59.5333, "", 0,0.0))
        coordinates.add(Coordinates(53.0, 28.0, "", 0,0.0))
        coordinates.add(Coordinates(50.8333, 4.0, "", 0,0.0))
        coordinates.add(Coordinates(17.25, -88.75, "", 0,0.0))
        coordinates.add(Coordinates(9.5, 2.25, "", 0,0.0))
        coordinates.add(Coordinates(32.3333, -64.75, "", 0,0.0))
        coordinates.add(Coordinates(27.5, 90.5, "", 0,0.0))
        coordinates.add(Coordinates(-17.0, -65.0, "", 0,0.0))
        coordinates.add(Coordinates(44.0, 18.0, "", 0,0.0))
        coordinates.add(Coordinates(-22.0, 24.0, "", 0,0.0))
        coordinates.add(Coordinates(-10.0, -55.0, "", 0,0.0))
        coordinates.add(Coordinates(18.5, -64.5, "", 0,0.0))
        coordinates.add(Coordinates(4.5, 114.6667, "", 0,0.0))
        coordinates.add(Coordinates(43.0, 25.0, "", 0,0.0))
        coordinates.add(Coordinates(13.0, -2.0, "", 0,0.0))
        coordinates.add(Coordinates(-3.5, 30.0, "", 0,0.0))
        coordinates.add(Coordinates(13.0, 105.0, "", 0,0.0))
        coordinates.add(Coordinates(6.0, 12.0, "", 0,0.0))
        coordinates.add(Coordinates(60.0, -95.0, "", 0,0.0))
        coordinates.add(Coordinates(16.0, -24.0, "", 0,0.0))
        coordinates.add(Coordinates(19.5, -80.5, "", 0,0.0))
        coordinates.add(Coordinates(7.0, 21.0, "", 0,0.0))
        coordinates.add(Coordinates(15.0, 19.0, "", 0,0.0))
        coordinates.add(Coordinates(-30.0, -71.0, "", 0,0.0))
        coordinates.add(Coordinates(35.0, 105.0, "", 0,0.0))
        coordinates.add(Coordinates(4.0, -72.0, "", 0,0.0))
        coordinates.add(Coordinates(-12.1667, 44.25, "", 0,0.0))
        coordinates.add(Coordinates(-1.0, 15.0, "", 0,0.0))
        coordinates.add(Coordinates(-18.8579, -159.7852, "", 0,0.0))
        coordinates.add(Coordinates(10.0, -84.0, "", 0,0.0))
        coordinates.add(Coordinates(8.0, -5.0, "", 0,0.0))
        coordinates.add(Coordinates(45.1667, 15.5, "", 0,0.0))
        coordinates.add(Coordinates(21.5, -80.0, "", 0,0.0))
        coordinates.add(Coordinates(35.0, 33.0, "", 0,0.0))

        coordinates.add(Coordinates(49.75, 15.5, "", 0,0.0))
        coordinates.add(Coordinates(-1.0, 15.0, "", 0,0.0))
        coordinates.add(Coordinates(56.0, 10.0, "", 0,0.0))
        coordinates.add(Coordinates(11.825, 42.5903, "", 0,0.0))
        coordinates.add(Coordinates(15.4167, -61.3333, "", 0,0.0))
        coordinates.add(Coordinates(19.0, -70.6667, "", 0,0.0))
        coordinates.add(Coordinates(-2.0, -77.5, "", 0,0.0))
        coordinates.add(Coordinates(27.0, 30.0, "", 0,0.0))
        coordinates.add(Coordinates(13.8333, -88.9167, "", 0,0.0))
        coordinates.add(Coordinates(2.0, 10.0, "", 0,0.0))
        coordinates.add(Coordinates(15.0, 39.0, "", 0,0.0))
        coordinates.add(Coordinates(59.0, 26.0, "", 0,0.0))
        coordinates.add(Coordinates(-26.5225, 31.4658, "", 0,0.0))
        coordinates.add(Coordinates(8.0, 38.0, "", 0,0.0))
        coordinates.add(Coordinates(62.0, -7.0, "", 0,0.0))
        coordinates.add(Coordinates(-51.75, -59.0, "", 0,0.0))
        coordinates.add(Coordinates(-18.0, 175.0, "", 0,0.0))
        coordinates.add(Coordinates(64.0, 26.0, "", 0,0.0))
        coordinates.add(Coordinates(46.0, 2.0, "", 0,0.0))
        coordinates.add(Coordinates(-15.0, -140.0, "", 0,0.0))
        coordinates.add(Coordinates(-1.0, 11.75, "", 0,0.0))
        coordinates.add(Coordinates(13.4667, -16.5667, "", 0,0.0))
        coordinates.add(Coordinates(42.0, 43.5, "", 0,0.0))
        coordinates.add(Coordinates(51.0, 9.0, "", 0,0.0))
        coordinates.add(Coordinates(8.0, -2.0, "", 0,0.0))
        coordinates.add(Coordinates(36.1833, -5.3667, "", 0,0.0))
        coordinates.add(Coordinates(39.0, 22.0, "", 0,0.0))
        coordinates.add(Coordinates(72.0, -40.0, "", 0,0.0))
        coordinates.add(Coordinates(12.1167, -61.6667, "", 0,0.0))
        coordinates.add(Coordinates(15.5, -90.25, "", 0,0.0))
        coordinates.add(Coordinates(0.0, 0.0, "", 0,0.0)) //TODO
        coordinates.add(Coordinates(11.0, -10.0, "", 0,0.0))
        coordinates.add(Coordinates(12.0, -15.0, "", 0,0.0))
        coordinates.add(Coordinates(5.0, -59.0, "", 0,0.0))
        coordinates.add(Coordinates(19.0, -72.4167, "", 0,0.0))
        coordinates.add(Coordinates(15.0, -86.5, "", 0,0.0))
        coordinates.add(Coordinates(22.25, 114.1667, "", 0,0.0))
        coordinates.add(Coordinates(47.0, 20.0, "", 0,0.0))
        coordinates.add(Coordinates(65.0, -18.0, "", 0,0.0))
        coordinates.add(Coordinates(20.0, 77.0, "", 0,0.0))
        coordinates.add(Coordinates(-5.0, 120.0, "", 0,0.0))
        coordinates.add(Coordinates(32.0, 53.0, "", 0,0.0))
        coordinates.add(Coordinates(33.0, 44.0, "", 0,0.0))
        coordinates.add(Coordinates(53.0, -8.0, "", 0,0.0))
        coordinates.add(Coordinates(54.23, -4.55, "", 0,0.0))
        coordinates.add(Coordinates(31.5, 34.75, "", 0,0.0))
        coordinates.add(Coordinates(42.8333, 12.8333, "", 0,0.0))
        coordinates.add(Coordinates(18.25, -77.5, "", 0,0.0))
        coordinates.add(Coordinates(36.0, 138.0, "", 0,0.0))
        coordinates.add(Coordinates(49.2144, -2.1312, "", 0,0.0))

        coordinates.add(Coordinates(31.0, 36.0, "", 0,0.0))
        coordinates.add(Coordinates(48.0, 68.0, "", 0,0.0))
        coordinates.add(Coordinates(1.0, 38.0, "", 0,0.0))
        coordinates.add(Coordinates(1.8709, -157.3626, "", 0,0.0))
        coordinates.add(Coordinates(42.66, 21.166, "", 0,0.0))
        coordinates.add(Coordinates(29.3375, 47.6581, "", 0,0.0))
        coordinates.add(Coordinates(41.0, 75.0, "", 0,0.0))
        coordinates.add(Coordinates(18.0, 105.0, "", 0,0.0))
        coordinates.add(Coordinates(57.0, 25.0, "", 0,0.0))
        coordinates.add(Coordinates(33.8333, 35.8333, "", 0,0.0))
        coordinates.add(Coordinates(-29.5, 28.5, "", 0,0.0))
        coordinates.add(Coordinates(6.5, -9.5, "", 0,0.0))
        coordinates.add(Coordinates(25.0, 17.0, "", 0,0.0))
        coordinates.add(Coordinates(47.1667, 6.5333, "", 0,0.0))
        coordinates.add(Coordinates(56.0, 24.0, "", 0,0.0))
        coordinates.add(Coordinates(49.75, 6.1667, "", 0,0.0))
        coordinates.add(Coordinates(22.1667, 113.55, "", 0,0.0))
        coordinates.add(Coordinates(-20.0, 47.0, "", 0,0.0))
        coordinates.add(Coordinates(-13.5, 34.0, "", 0,0.0))
        coordinates.add(Coordinates(2.5, 112.5, "", 0,0.0))
        coordinates.add(Coordinates(3.25, 73.0, "", 0,0.0))
        coordinates.add(Coordinates(17.0, -4.0, "", 0,0.0))
        coordinates.add(Coordinates(35.8333, 14.5833, "", 0,0.0))
        coordinates.add(Coordinates(9.0, 168.0, "", 0,0.0))
        coordinates.add(Coordinates(20.0, -12.0, "", 0,0.0))
        coordinates.add(Coordinates(-20.2833, 57.55, "", 0,0.0))
        coordinates.add(Coordinates(23.0, -102.0, "", 0,0.0))
        coordinates.add(Coordinates(6.9167, 158.25, "", 0,0.0))
        coordinates.add(Coordinates(47.0, 29.0, "", 0,0.0))
        coordinates.add(Coordinates(43.7333, 7.4, "", 0,0.0))
        coordinates.add(Coordinates(46.0, 105.0, "", 0,0.0))
        coordinates.add(Coordinates(42.0, 19.0, "", 0,0.0))
        coordinates.add(Coordinates(16.75, -62.2, "", 0,0.0))
        coordinates.add(Coordinates(32.0, -5.0, "", 0,0.0))
        coordinates.add(Coordinates(-18.25, 35.0, "", 0,0.0))
        coordinates.add(Coordinates(22.0, 98.0, "", 0,0.0))
        coordinates.add(Coordinates(-22.0, 17.0, "", 0,0.0))
        coordinates.add(Coordinates(-0.5272, 166.9367, "", 0,0.0))
        coordinates.add(Coordinates(28.0, 84.0, "", 0,0.0))
        coordinates.add(Coordinates(52.5, 5.75, "", 0,0.0))
        coordinates.add(Coordinates(-21.5, 165.5, "", 0,0.0))
        coordinates.add(Coordinates(-41.0, 174.0, "", 0,0.0))
        coordinates.add(Coordinates(13.0, -85.0, "", 0,0.0))
        coordinates.add(Coordinates(16.0, 8.0, "", 0,0.0))
        coordinates.add(Coordinates(10.0, 8.0, "", 0,0.0))
        coordinates.add(Coordinates(-19.0544, -169.8672, "", 0,0.0))
        coordinates.add(Coordinates(41.8333, 22.0, "", 0,0.0))
        coordinates.add(Coordinates(62.0, 10.0, "", 0,0.0))
        coordinates.add(Coordinates(21.0, 57.0, "", 0,0.0))
        coordinates.add(Coordinates(30.0, 70.0, "", 0,0.0))

        coordinates.add(Coordinates(32.0, 35.25, "", 0,0.0))
        coordinates.add(Coordinates(9.0, -80.0, "", 0,0.0))
        coordinates.add(Coordinates(-6.0, 147.0, "", 0,0.0))
        coordinates.add(Coordinates(-23.0, -58.0, "", 0,0.0))
        coordinates.add(Coordinates(-10.0, -76.0, "", 0,0.0))
        coordinates.add(Coordinates(13.0, 122.0, "", 0,0.0))
        coordinates.add(Coordinates(-24.3767, -128.3242, "", 0,0.0))
        coordinates.add(Coordinates(52.0, 20.0, "", 0,0.0))
        coordinates.add(Coordinates(39.5, -8.0, "", 0,0.0))
        coordinates.add(Coordinates(25.5, 51.25, "", 0,0.0))
        coordinates.add(Coordinates(46.0, 25.0, "", 0,0.0))
        coordinates.add(Coordinates(60.0, 100.0, "", 0,0.0))
        coordinates.add(Coordinates(-2.0, 30.0, "", 0,0.0))
        coordinates.add(Coordinates(-15.9333, -5.7, "", 0,0.0))
        coordinates.add(Coordinates(17.3333, -62.75, "", 0,0.0))
        coordinates.add(Coordinates(13.8833, -61.1333, "", 0,0.0))
        coordinates.add(Coordinates(13.25, -61.2, "", 0,0.0))
        coordinates.add(Coordinates(-13.5833, -172.3333, "", 0,0.0))
        coordinates.add(Coordinates(43.7667, 12.4167, "", 0,0.0))
        coordinates.add(Coordinates(1.0, 7.0, "", 0,0.0))
        coordinates.add(Coordinates(25.0, 45.0, "", 0,0.0))
        coordinates.add(Coordinates(14.0, -14.0, "", 0,0.0))
        coordinates.add(Coordinates(44.0, 21.0, "", 0,0.0))
        coordinates.add(Coordinates(-4.5833, 55.6667, "", 0,0.0))
        coordinates.add(Coordinates(8.5, -11.5, "", 0,0.0))
        coordinates.add(Coordinates(1.3667, 103.8, "", 0,0.0))
        coordinates.add(Coordinates(48.6667, 19.5, "", 0,0.0))
        coordinates.add(Coordinates(46.0, 15.0, "", 0,0.0))
        coordinates.add(Coordinates(-8.0, 159.0, "", 0,0.0))
        coordinates.add(Coordinates(10.0, 49.0, "", 0,0.0))
        coordinates.add(Coordinates(-29.0, 24.0, "", 0,0.0))
        coordinates.add(Coordinates(37.0, 127.5, "", 0,0.0))
        coordinates.add(Coordinates(6.8769, 31.3069, "", 0,0.0))
        coordinates.add(Coordinates(40.0, -4.0, "", 0,0.0))
        coordinates.add(Coordinates(7.0, 81.0, "", 0,0.0))
        coordinates.add(Coordinates(15.0, 30.0, "", 0,0.0))
        coordinates.add(Coordinates(4.0, -56.0, "", 0,0.0))
        coordinates.add(Coordinates(62.0, 15.0, "", 0,0.0))
        coordinates.add(Coordinates(47.0, 8.0, "", 0,0.0))
        coordinates.add(Coordinates(35.0, 38.0, "", 0,0.0))
        coordinates.add(Coordinates(23.5, 121.0, "", 0,0.0))
        coordinates.add(Coordinates(39.0, 71.0, "", 0,0.0))
        coordinates.add(Coordinates(-6.0, 35.0, "", 0,0.0))
        coordinates.add(Coordinates(15.0, 100.0, "", 0,0.0))
        coordinates.add(Coordinates(-8.55, 125.5167, "", 0,0.0))
        coordinates.add(Coordinates(8.0, 1.1667, "", 0,0.0))
        coordinates.add(Coordinates(-9.1668, -171.8182, "", 0,0.0))
        coordinates.add(Coordinates(-21.1789, -175.1982, "", 0,0.0))
        coordinates.add(Coordinates(11.0, -61.0, "", 0,0.0))
        coordinates.add(Coordinates(34.0, 9.0, "", 0,0.0))
        coordinates.add(Coordinates(39.0, 35.0, "", 0,0.0))

        coordinates.add(Coordinates(38.9638, 59.5576, "", 0,0.0))
        coordinates.add(Coordinates(21.75, -71.5833, "", 0,0.0))
        coordinates.add(Coordinates(-7.4784, 178.6799, "", 0,0.0))
        coordinates.add(Coordinates(1.0, 32.0, "", 0,0.0))
        coordinates.add(Coordinates(49.0, 32.0, "", 0,0.0))
        coordinates.add(Coordinates(24.0, 54.0, "", 0,0.0))
        coordinates.add(Coordinates(54.0, -2.0, "", 0,0.0))
        coordinates.add(Coordinates(38.0, -97.0,"", 0,0.0))
        coordinates.add(Coordinates(-33.0, -56.0, "", 0,0.0))
        coordinates.add(Coordinates(41.0, 64.0, "", 0,0.0))
        coordinates.add(Coordinates(-16.0, 167.0, "", 0,0.0))
        coordinates.add(Coordinates(41.9, 12.45, "", 0,0.0))
        coordinates.add(Coordinates(8.0, -66.0, "", 0,0.0))
        coordinates.add(Coordinates(21.0, 105.0, "", 0,0.0))
        coordinates.add(Coordinates(-13.3, -176.2, "", 0,0.0))
        coordinates.add(Coordinates(15.0, 48.0, "", 0,0.0))
        coordinates.add(Coordinates(-15.0, 30.0, "", 0,0.0))
        coordinates.add(Coordinates(-20.0, 30.0, "", 0,0.0))
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