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
import kotlinx.coroutines.launch
import pe.edu.ulima.pm.covidinfo.R
import pe.edu.ulima.pm.covidinfo.managers.CovidInfoManager
import pe.edu.ulima.pm.covidinfo.models.AppDatabase
import pe.edu.ulima.pm.covidinfo.models.persistence.dao.FavoriteDAO
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.CountryEntity
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.FavoriteEntity
import pe.edu.ulima.pm.covidinfo.objects.PremiumSingleCountryStats
import java.text.DecimalFormat

class SingleCountryPiechartFragment: Fragment() {

    private lateinit var tviDateCountry: TextView
    private lateinit var tviTotalConfirmedCountry: TextView
    private lateinit var tviTotalDeathsCountry: TextView
    private lateinit var tviMortalityRatioCountry: TextView
    private lateinit var tviTotalCasesPerMillion: TextView
    private lateinit var tviTotalDeathsPerMillion: TextView
    private lateinit var tviTotalCasesPer100k: TextView
    private lateinit var tviTotalDeathsPer100k: TextView

    private lateinit var tviNewConfirmedCountry: TextView
    private lateinit var tviNewDeathsCountry: TextView
    private lateinit var tviNewMortalityRatioCountry: TextView
    private lateinit var tviNewCasesPerMillion: TextView
    private lateinit var tviNewDeathsPerMillion: TextView
    private lateinit var tviNewCasesPer100k: TextView
    private lateinit var tviNewDeathsPer100k: TextView

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

        favoriteDAO = AppDatabase.getInstance(view.context).favoriteDAO
        pchSingleCountry = view.findViewById(R.id.pchSingleCountry)
        butAddFavorite = view.findViewById(R.id.butAddFavorite)
        emptyStar = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_star_border_24)!!
        coloredStar = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_star_yellow_24)!!

        setPieChart()
        lifecycleScope.launch { isFavorite(PremiumSingleCountryStats.country!!) }

        tviDateCountry = view.findViewById(R.id.tviDateCountry)
        tviTotalConfirmedCountry = view.findViewById(R.id.tviTotalConfirmedCountry)
        tviTotalDeathsCountry = view.findViewById(R.id.tviTotalDeathsCountry)
        tviMortalityRatioCountry = view.findViewById(R.id.tviMortalityRatioCountry)
        tviTotalCasesPerMillion = view.findViewById(R.id.tviTotalCasesPerMillion)
        tviTotalDeathsPerMillion = view.findViewById(R.id.tviTotalDeathsPerMillion)
        tviTotalCasesPer100k = view.findViewById(R.id.tviTotalCasesPer100k)
        tviTotalDeathsPer100k = view.findViewById(R.id.tviTotalDeathsPer100k)

        tviNewConfirmedCountry = view.findViewById(R.id.tviNewConfirmedCountry)
        tviNewDeathsCountry = view.findViewById(R.id.tviNewDeathsCountry)
        tviNewMortalityRatioCountry = view.findViewById(R.id.tviNewMortalityRatioCountry)
        tviNewCasesPerMillion = view.findViewById(R.id.tviNewCasesPerMillion)
        tviNewDeathsPerMillion = view.findViewById(R.id.tviNewDeathsPerMillion)
        tviNewCasesPer100k = view.findViewById(R.id.tviNewCasesPer100k)
        tviNewDeathsPer100k = view.findViewById(R.id.tviNewDeathsPer100k)

        val formattedDate = sc!!.Date.substring(0, 10).replace("-", "/")
        val formattedHour = sc!!.Date.substring(11, 19)
        val df = DecimalFormat("###,###,###")

        // Seteando los TextView
        tviDateCountry.text = "Last updated: $formattedDate at $formattedHour"
        tviTotalConfirmedCountry.text = df.format(sc?.TotalCases)
        tviTotalDeathsCountry.text = df.format(sc?.TotalDeaths)
        tviMortalityRatioCountry.text = String.format("%.2f", sc?.CaseFatalityRatio) + "%"
        tviTotalCasesPerMillion.text = df.format(String.format("%.2f", sc?.TotalCasesPerMillion).toDouble())
        tviTotalDeathsPerMillion.text = df.format(String.format("%.2f", sc?.TotalDeathsPerMillion).toDouble())
        tviTotalCasesPer100k.text = df.format(String.format("%.2f", sc?.IncidenceRiskConfirmedPerHundredThousand).toDouble())
        tviTotalDeathsPer100k.text = df.format(String.format("%.2f", sc?.IncidenceRiskDeathsPerHundredThousand).toDouble())
        tviNewConfirmedCountry.text = sc?.NewCases.toString()
        tviNewDeathsCountry.text = sc?.NewDeaths.toString()
        tviNewCasesPerMillion.text = df.format(String.format("%.2f", sc?.NewCasesPerMillion).toDouble())
        tviNewDeathsPerMillion.text = df.format(String.format("%.2f", sc?.NewDeathsPerMillion).toDouble())
        tviNewCasesPer100k.text = df.format(String.format("%.2f", sc?.IncidenceRiskNewConfirmedPerHundredThousand).toDouble())
        tviNewDeathsPer100k.text = df.format(String.format("%.2f", sc?.IncidenceRiskNewDeathsPerHundredThousand).toDouble())
        if (sc!!.NewCases.toInt() != 0)
            tviNewMortalityRatioCountry.text = String.format("%.2f", sc!!.NewDeaths.toDouble()*100.0 / (sc!!.NewCases.toDouble()+1.0)) + "%"
        else
            tviNewMortalityRatioCountry.text = "-"

        //Al hacer click en el boton de favorito, se anade o elimina el pais de los favoritos
        butAddFavorite!!.setOnClickListener {

            val favoriteEntity: FavoriteEntity = CovidInfoManager.getInstance().setFavoriteEntity(PremiumSingleCountryStats.country!!)
            lifecycleScope.launch {
                if(isFavorite == 1) {
                    favoriteDAO!!.deleteSingleFavorite(favoriteEntity.ID)
                    Toast.makeText(context, "${sc?.Country} removed from favorites", Toast.LENGTH_SHORT).show()
                    butAddFavorite!!.background = emptyStar
                    isFavorite = 0

                } else {
                    favoriteDAO!!.insertFavorite(favoriteEntity)
                    Toast.makeText(context, "${sc?.Country} added to favorites", Toast.LENGTH_SHORT).show()
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

        colors.add(ContextCompat.getColor(requireContext(), R.color.blue_500))
        colors.add(ContextCompat.getColor(requireContext(), R.color.red_500))

        pieDataSet.colors = colors
        pieData.setValueTextSize(10f)
        pchSingleCountry.animateXY(1000,1000)
        pchSingleCountry.data = pieData
        pchSingleCountry.description.text=""
        pchSingleCountry.setHoleColor(ContextCompat.getColor(requireContext(), R.color.lightblue))
        pchSingleCountry.setCenterTextSize(50f)
        pchSingleCountry.invalidate()
    }

    private fun getList() : Array<PieEntry?> {
        dataList[0] = PieEntry(sc!!.TotalCasesPerMillion.toFloat(), "Cases/million")
        dataList[1] = PieEntry(sc!!.TotalDeathsPerMillion.toFloat(), "Deaths/million")
        return dataList
    }

    private suspend fun isFavorite(country: CountryEntity) {
        favoriteDAO = AppDatabase.getInstance(requireContext()).favoriteDAO
        val query = favoriteDAO!!.getSingleFavorite(country.Country)

        if (query == null) {
            butAddFavorite!!.background = emptyStar
            isFavorite = 0
        } else {
            butAddFavorite!!.background = coloredStar
            isFavorite = 1
        }
    }

}