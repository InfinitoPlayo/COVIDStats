package pe.edu.ulima.pm.covidinfo.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import pe.edu.ulima.pm.covidinfo.models.AppDatabase
import pe.edu.ulima.pm.covidinfo.models.persistence.dao.ContinentDAO
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.ContinentEntity
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GlobalInfoFragment: Fragment() {

    private lateinit var tviDateGlobal: TextView
    private lateinit var tviConfirmedGlobal: TextView
    private lateinit var tviDeathsGlobal: TextView
    private lateinit var tviRecoveredGlobal: TextView
    private lateinit var tviActiveCasesGlobal: TextView
    private lateinit var tviTotalCasesPerMillion: TextView
    private lateinit var tviTotalDeathsPerMillion: TextView
    private lateinit var tviTotalTests: TextView
    private lateinit var tviTestsPerMillion: TextView
    private lateinit var tviNewCasesGlobal: TextView
    private lateinit var tviNewRecoveredGlobal: TextView
    private lateinit var tviNewDeathsGlobal: TextView
    private lateinit var tviNewActiveCasesGlobal: TextView
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

    private var continents: ArrayList<ContinentEntity> = ArrayList()
    private var continentDAO: ContinentDAO? = null
    private var population: Long = 0

    //Para configurar el PieChart
    private var dataList: ArrayList<PieEntry> = ArrayList()
    private var colors: ArrayList<Int> = ArrayList()
    private lateinit var pchGlobalData: PieChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_global_info_piechart, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pchGlobalData = view.findViewById(R.id.pchGlobalData)
        continentDAO = AppDatabase.getInstance(requireContext()).continentDAO

        //Llamando a los TextView de fragment_global_info
        tviDateGlobal = view.findViewById(R.id.tviDateGlobal)
        tviConfirmedGlobal = view.findViewById(R.id.tviConfirmedGlobal)
        tviDeathsGlobal = view.findViewById(R.id.tviTotalDeathsGlobal)
        tviRecoveredGlobal = view.findViewById(R.id.tviRecoveredGlobal)
        tviActiveCasesGlobal = view.findViewById(R.id.tviActiveCasesGlobal)
        tviTotalCasesPerMillion = view.findViewById(R.id.tviTotalCasesPerMillion)
        tviTotalDeathsPerMillion = view.findViewById(R.id.tviTotalDeathsPerMillion)
        tviTotalTests = view.findViewById(R.id.tviTotalTests)
        tviTestsPerMillion = view.findViewById(R.id.tviTestsPerMillion)

        tviNewCasesGlobal = view.findViewById(R.id.tviNewCasesGlobal)
        tviNewRecoveredGlobal = view.findViewById(R.id.tviNewRecoveredGlobal)
        tviNewDeathsGlobal = view.findViewById(R.id.tviNewDeathsGlobal)
        tviNewActiveCasesGlobal = view.findViewById(R.id.tviNewActiveCasesGlobal)
        tviNewCasesPerMillion = view.findViewById(R.id.tviNewCasesPerMillion)
        tviNewDeathsPerMillion = view.findViewById(R.id.tviNewDeathsPerMillion)

        tviMortalityRatio = view.findViewById(R.id.tviMortalityRatio)
        tviNewMortalityRatio = view.findViewById(R.id.tviNewMortalityRatio)

        lifecycleScope.launch {
            val continentList = ArrayList(continentDAO!!.getAllContinents())
            for (i in 1..continentList.size) {
                if (i > continentList.size - 6) {
                    continents.add(continentList[i-1])
                    population += continentList[i-1].population!!
                }
            }
            Log.i("GlobalInfoFragment", population.toString())

            setGlobalStats()
            setPieChart()

            //Seteando los TextView
            val df = DecimalFormat("###,###,###.##")
            tviDateGlobal.text = "Last updated: ${convertLongToTime(continents[0].updated!!)}"
            tviConfirmedGlobal.text = df.format(totalConfirmed)
            tviDeathsGlobal.text = df.format(totalDeaths)
            tviRecoveredGlobal.text = df.format(totalRecovered)
            tviActiveCasesGlobal.text = df.format(totalActiveCases)
            tviTotalCasesPerMillion.text = df.format(casesPerMillion)
            tviTotalDeathsPerMillion.text = df.format(deathsPerMillion)
            tviTotalTests.text = df.format(totalTests)
            tviTestsPerMillion.text = df.format(testsPerMillion)
            tviNewCasesGlobal.text = df.format(newCases)
            tviNewRecoveredGlobal.text = df.format(newRecovered)
            tviNewDeathsGlobal.text = df.format(newDeaths)
            tviNewActiveCasesGlobal.text = df.format(newActiveCases)
            tviNewCasesPerMillion.text = df.format(newCasesPerMillion)
            tviNewDeathsPerMillion.text = df.format(newDeathsPerMillion)
            tviMortalityRatio.text = String.format("%.2f" , mortalityRatio*100) + "%"
            tviNewMortalityRatio.text = String.format("%.2f" , newMortalityRatio*100) + "%"
        }
    }

    //Configurar el PieChart
    private fun setPieChart() {
        val pieDataSet = PieDataSet(getList(), "")
        val pieData = PieData(pieDataSet)

        colors.add(ContextCompat.getColor(requireContext(), R.color.green_500))
        colors.add(ContextCompat.getColor(requireContext(), R.color.blue_500))
        colors.add(ContextCompat.getColor(requireContext(), R.color.red_500))

        pieDataSet.colors = colors
        pieData.setValueTextSize(10f)
        pieData.setValueFormatter(PercentFormatter(pchGlobalData))
        pchGlobalData.animateXY(1000,1000)
        pchGlobalData.data = pieData
        pchGlobalData.setHoleColor(ContextCompat.getColor(requireContext(), R.color.lightblue))
        pchGlobalData.description.text=""
        pchGlobalData.setUsePercentValues(true)
        pchGlobalData.setCenterTextSize(50f)
        pchGlobalData.invalidate()
    }

    private fun getList(): ArrayList<PieEntry>{
        dataList.add(0, PieEntry(totalRecovered.toFloat(),"Recovered"))
        dataList.add(1, PieEntry(totalActiveCases.toFloat(),"Active"))
        dataList.add(2, PieEntry(totalDeaths.toFloat(),"Deaths"))

        return dataList
    }

    private fun setGlobalStats() {
        continents.forEach {
            totalConfirmed += it.cases!!
            totalDeaths += it.deaths!!
            totalRecovered += it.recovered!!
            totalActiveCases += it.active!!
            casesPerMillion += (it.casesPerOneMillion!! * it.population!!) / population.toDouble()
            deathsPerMillion += (it.deathsPerOneMillion!! * it.population!!) / population.toDouble()
            totalTests += it.tests!!
            testsPerMillion += (it.testsPerOneMillion!! * it.population!!) / population.toDouble()
            newCases += it.todayCases!!
            newRecovered += it.todayRecovered!!
            newDeaths += it.todayDeaths!!
            newActiveCases += (it.todayCases!! - it.todayRecovered!! - it.todayDeaths!!)
            mortalityRatio += (it.deaths!!.toDouble() / it.cases!!.toDouble()) / continents.size.toDouble()
            newMortalityRatio += (it.todayDeaths!!.toDouble() / it.todayCases!!.toDouble()) / continents.size.toDouble()
        }
        newCasesPerMillion = 1000000.0 * newCases / population.toDouble()
        newDeathsPerMillion = 1000000.0 * newDeaths / population.toDouble()
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy/MM/dd HH:mm")
        return format.format(date)
    }

}