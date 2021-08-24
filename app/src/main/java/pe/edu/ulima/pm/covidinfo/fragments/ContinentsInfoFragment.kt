package pe.edu.ulima.pm.covidinfo.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.coroutines.launch
import pe.edu.ulima.pm.covidinfo.R
import pe.edu.ulima.pm.covidinfo.managers.CovidInfoManager
import pe.edu.ulima.pm.covidinfo.models.AppDatabase
import pe.edu.ulima.pm.covidinfo.models.persistence.dao.ContinentDAO
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.ContinentEntity
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ContinentsInfoFragment: Fragment() {

    private lateinit var tviDateContinent: TextView
    private lateinit var tviConfirmedContinent: TextView
    private lateinit var tviDeathsContinent: TextView
    private lateinit var tviRecoveredContinent: TextView
    private lateinit var tviActiveCasesContinent: TextView
    private lateinit var tviTotalCasesPerMillion: TextView
    private lateinit var tviTotalDeathsPerMillion: TextView
    private lateinit var tviTotalTests: TextView
    private lateinit var tviTestsPerMillion: TextView
    private lateinit var tviNewCasesContinent: TextView
    private lateinit var tviNewRecoveredContinent: TextView
    private lateinit var tviNewDeathsContinent: TextView
    private lateinit var tviNewActiveCasesContinent: TextView
    private lateinit var tviNewCasesPerMillion: TextView
    private lateinit var tviNewDeathsPerMillion: TextView
    private lateinit var tviMortalityRatio: TextView
    private lateinit var tviNewMortalityRatio: TextView

    private var totalConfirmed: Long = 0
    private var totalDeaths: Long = 0
    private var totalRecovered: Long = 0
    private var totalActiveCases: Long = 0
    private var casesPerMillion: Double = 0.0
    private var deathsPerMillion: Double = 0.0
    private var totalTests: Long = 0
    private var testsPerMillion: Double = 0.0
    private var newCases: Long = 0
    private var newRecovered: Long = 0
    private var newDeaths: Long = 0
    private var newActiveCases: Long = 0
    private var newCasesPerMillion: Double = 0.0
    private var newDeathsPerMillion: Double = 0.0
    private var mortalityRatio: Double = 0.0
    private var newMortalityRatio: Double = 0.0

    private var continentDAO: ContinentDAO? = null
    private lateinit var spinner: Spinner

    //Para configurar el PieChart
    private var dataList = arrayOfNulls<PieEntry>(3)
    private var colors: ArrayList<Int> = ArrayList()
    private lateinit var pchContinentsData: PieChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_continents_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        continentDAO = AppDatabase.getInstance(requireContext()).continentDAO
        spinner = view.findViewById(R.id.spnMain)
        pchContinentsData = view.findViewById(R.id.pchContinentsData)

        tviDateContinent = view.findViewById(R.id.tviDateContinent)
        tviConfirmedContinent = view.findViewById(R.id.tviConfirmedContinent)
        tviDeathsContinent = view.findViewById(R.id.tviTotalDeathsContinent)
        tviRecoveredContinent = view.findViewById(R.id.tviRecoveredContinent)
        tviActiveCasesContinent = view.findViewById(R.id.tviActiveCasesContinent)
        tviTotalCasesPerMillion = view.findViewById(R.id.tviTotalCasesPerMillion)
        tviTotalDeathsPerMillion = view.findViewById(R.id.tviTotalDeathsPerMillion)
        tviTotalTests = view.findViewById(R.id.tviTotalTests)
        tviTestsPerMillion = view.findViewById(R.id.tviTestsPerMillion)

        tviNewCasesContinent = view.findViewById(R.id.tviNewCasesContinent)
        tviNewRecoveredContinent = view.findViewById(R.id.tviNewRecoveredContinent)
        tviNewDeathsContinent = view.findViewById(R.id.tviNewDeathsContinent)
        tviNewActiveCasesContinent = view.findViewById(R.id.tviNewActiveCasesContinent)
        tviNewCasesPerMillion = view.findViewById(R.id.tviNewCasesPerMillion)
        tviNewDeathsPerMillion = view.findViewById(R.id.tviNewDeathsPerMillion)

        tviMortalityRatio = view.findViewById(R.id.tviMortalityRatio)
        tviNewMortalityRatio = view.findViewById(R.id.tviNewMortalityRatio)

        lifecycleScope.launch {
            val continentList: ArrayList<ContinentEntity> = ArrayList(continentDAO!!.getAllContinents())
            val continents: ArrayList<ContinentEntity> = ArrayList()

            for (i in 1..continentList.size) {
                if (i > continentList.size - 6) {
                    continents.add(continentList[i-1])
                }
            }

            val spnOptions = listOf("Asia", "North America", "South America", "Europe", "Africa", "Australia-Oceania")
            val spnAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spnOptions)
            spinner.adapter = spnAdapter

            spinner.onItemSelectedListener = object:
                AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    showContinentStats(spnOptions[position], continents)
                    setPieChart()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.i("ContinentsInfoFragment", "Nothing selected")
                }
            }
        }
    }

    //Configurar el PieChart
    private fun setPieChart() {
        val pieDataSet = PieDataSet(getList().toCollection(ArrayList()), "")
        val pieData = PieData(pieDataSet)

        colors.add(ContextCompat.getColor(requireContext(), R.color.green_500))
        colors.add(ContextCompat.getColor(requireContext(), R.color.blue_500))
        colors.add(ContextCompat.getColor(requireContext(), R.color.red_500))

        pieDataSet.colors = colors
        pieData.setValueTextSize(10f)
        pieData.setValueFormatter(PercentFormatter(pchContinentsData))
        pchContinentsData.animateXY(1000,1000)
        pchContinentsData.data = pieData
        pchContinentsData.setHoleColor(ContextCompat.getColor(requireContext(), R.color.lightblue))
        pchContinentsData.description.text=""
        pchContinentsData.setUsePercentValues(true)
        pchContinentsData.setCenterTextSize(50f)
        pchContinentsData.invalidate()
    }

    private fun getList(): Array<PieEntry?>{
        dataList[0] = PieEntry(totalRecovered.toFloat(),"Recovered")
        dataList[1] = PieEntry(totalActiveCases.toFloat(),"Active")
        dataList[2] = PieEntry(totalDeaths.toFloat(),"Deaths")

        return dataList
    }

    @SuppressLint("SetTextI18n")
    private fun showContinentStats(continent: String, continents: ArrayList<ContinentEntity>) {
        var selectedContinent: ContinentEntity? = null
        continents.forEach {
            if (it.continent == continent) selectedContinent = it
        }

        setContinentStats(selectedContinent!!)

        val df = DecimalFormat("###,###,###.##")
        tviDateContinent.text = "Last updated: ${CovidInfoManager.getInstance().convertLongToTime(selectedContinent!!.updated!!)}"
        tviConfirmedContinent.text = df.format(totalConfirmed)
        tviDeathsContinent.text = df.format(totalDeaths)
        tviRecoveredContinent.text = df.format(totalRecovered)
        tviActiveCasesContinent.text = df.format(totalActiveCases)
        tviTotalCasesPerMillion.text = df.format(casesPerMillion)
        tviTotalDeathsPerMillion.text = df.format(deathsPerMillion)
        tviTotalTests.text = df.format(totalTests)
        tviTestsPerMillion.text = df.format(testsPerMillion)
        tviNewCasesContinent.text = df.format(newCases)
        tviNewRecoveredContinent.text = df.format(newRecovered)
        tviNewDeathsContinent.text = df.format(newDeaths)
        if (newActiveCases > 0) {
            tviNewActiveCasesContinent.text = df.format(newActiveCases)
        } else {
            tviNewActiveCasesContinent.text = "-"
        }
        tviNewCasesPerMillion.text = df.format(newCasesPerMillion)
        tviNewDeathsPerMillion.text = df.format(newDeathsPerMillion)
        tviMortalityRatio.text = String.format("%.2f" , mortalityRatio*100) + "%"
        tviNewMortalityRatio.text = String.format("%.2f" , newMortalityRatio*100) + "%"
    }

    private fun setContinentStats(continent: ContinentEntity) {
        totalConfirmed = continent.cases!!
        totalDeaths = continent.deaths!!
        totalRecovered = continent.recovered!!
        totalActiveCases = continent.active!!
        casesPerMillion = continent.casesPerOneMillion!!
        deathsPerMillion = continent.deathsPerOneMillion!!
        totalTests = continent.tests!!
        testsPerMillion += continent.testsPerOneMillion!!
        newCases = continent.todayCases!!
        newRecovered = continent.todayRecovered!!
        newDeaths = continent.todayDeaths!!
        newActiveCases += (continent.todayCases!! - continent.todayRecovered!! - continent.todayDeaths!!)
        mortalityRatio += (continent.deaths!!.toDouble() / continent.cases!!.toDouble())
        newMortalityRatio += (continent.todayDeaths!!.toDouble() / continent.todayCases!!.toDouble())
    }

}