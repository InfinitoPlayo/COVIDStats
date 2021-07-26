package pe.edu.ulima.pm.covidinfo.fragments

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch
import pe.edu.ulima.pm.covidinfo.R
import pe.edu.ulima.pm.covidinfo.managers.CovidInfoManager
import pe.edu.ulima.pm.covidinfo.models.AppDatabase
import pe.edu.ulima.pm.covidinfo.models.dao.PremiumSingleCountryData
import pe.edu.ulima.pm.covidinfo.models.persistence.dao.FavoriteDAO
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.FavoriteEntity
import pe.edu.ulima.pm.covidinfo.objects.PremiumSingleCountryStats
import java.text.DecimalFormat

class SingleCountryPiechartFragment: Fragment() {

    private lateinit var tviDateCountry: TextView
    private lateinit var tviTotalConfirmedCountry: TextView
    private lateinit var tviTotalDeathsCountry: TextView
    private lateinit var tviTotalRecoveredCountry: TextView
    private lateinit var tviTotalActiveCasesCountry: TextView
    private var sc = PremiumSingleCountryStats.country //Singleton que contiene stats del pais seleccionado

    private var butAddFavorite: Button? = null
    private var favoriteDAO: FavoriteDAO? = null
    private var isFavorite = 0

    //Para configurar el PieChart
    private var dataList = arrayOfNulls<PieEntry>(2)
    private var colors: ArrayList<Int> = ArrayList()
    private lateinit var pchSingleCountry: PieChart
    private lateinit var emptyStar: Drawable
    private lateinit var coloredStar: Drawable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_single_country_piechart, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pchSingleCountry = view.findViewById(R.id.pchSingleCountry)
        favoriteDAO = AppDatabase.getInstance(view.context).favoriteDAO
        butAddFavorite = view.findViewById(R.id.butAddFavorite)
        emptyStar = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_star_border_24)!!
        coloredStar = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_star_yellow_24)!!

        setPieChart()
        lifecycleScope.launch { isFavorite(PremiumSingleCountryStats.country!!) }

        tviDateCountry = view.findViewById(R.id.tviDateCountry)
        tviTotalConfirmedCountry = view.findViewById(R.id.tviTotalConfirmedCountry)
        tviTotalDeathsCountry = view.findViewById(R.id.tviTotalDeathsCountry)
        tviTotalRecoveredCountry = view.findViewById(R.id.tviTotalRecoveredCountry)
        tviTotalActiveCasesCountry = view.findViewById(R.id.tviTotalActiveCasesCountry)

        val formattedDate = sc!!.Date.substring(0, 10).replace("-", " / ")
        val formattedHour = sc!!.Date.substring(11, 19)
        val formattedDateHour = "$formattedDate at $formattedHour"
        val df = DecimalFormat("###,###,###")


        // Seteando los TextView
        tviDateCountry.text = "Last updated: $formattedDateHour"
        tviTotalConfirmedCountry.text = "Total cases: ${df.format(sc!!.TotalCases)}"
        tviTotalDeathsCountry.text = "Total deaths: ${df.format(sc!!.TotalDeaths)}"
        tviTotalRecoveredCountry.text = "Total cases per million: ${df.format(sc!!.TotalCasesPerMillion)}"
        tviTotalActiveCasesCountry.text = "Total deaths per million: ${df.format(sc!!.TotalDeathsPerMillion)}"

        butAddFavorite!!.setOnClickListener {

            val favoriteEntity: FavoriteEntity = CovidInfoManager.getInstance().setFavoriteEntity(PremiumSingleCountryStats.country!!)
            lifecycleScope.launch {

                Log.i("SingleCountryPiechart", isFavorite.toString())
                if(isFavorite == 1) {
                    favoriteDAO!!.deleteSingleFavorite(favoriteEntity.ID)
                    Toast.makeText(context, "${PremiumSingleCountryStats.country!!.Country} removed from favorites", Toast.LENGTH_SHORT).show()
                    butAddFavorite!!.background = emptyStar
                    isFavorite = 0

                } else {
                    favoriteDAO!!.insertFavorite(favoriteEntity)
                    Toast.makeText(context, "${PremiumSingleCountryStats.country!!.Country} added to favorites", Toast.LENGTH_SHORT).show()
                    butAddFavorite!!.background = coloredStar
                    isFavorite = 1
                }
            }
        }

    }

    //Configurar el PieChart
    private fun setPieChart() {

        val pieDataSet = PieDataSet(getList().toCollection(ArrayList()), "")
        val pieData = PieData(pieDataSet)

        colors.add(ColorTemplate.MATERIAL_COLORS[1])
        colors.add(ColorTemplate.MATERIAL_COLORS[2])

        pieDataSet.colors = colors
        pieData.setValueTextSize(10f)
        pchSingleCountry.animateXY(1500,1500)
        pchSingleCountry.data = pieData
        pchSingleCountry.description.text=""
        pchSingleCountry.setCenterTextSize(50f)
        pchSingleCountry.invalidate()
    }

    private fun getList() : Array<PieEntry?> {

        dataList[0] = PieEntry(sc!!.TotalCasesPerMillion.toFloat(), "Cases/million")
        dataList[1] = PieEntry(sc!!.TotalDeathsPerMillion.toFloat(), "Deaths/Million")

        return dataList
    }

    private suspend fun isFavorite(country: PremiumSingleCountryData) {

        favoriteDAO = AppDatabase.getInstance(requireContext()).favoriteDAO

        Log.i("SingleCountryPieChart", country.ID)
        val query = favoriteDAO?.getSingleFavorite(country.ID)
        Log.i("SingleCountryPieChart", query.toString())


        if (favoriteDAO!!.getSingleFavorite(country.ID) == null) {
            butAddFavorite!!.background = emptyStar
            isFavorite = 0
        } else {
            butAddFavorite!!.background = coloredStar
            isFavorite = 1
        }
    }

}