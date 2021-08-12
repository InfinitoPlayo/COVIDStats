package pe.edu.ulima.pm.covidinfo.fragments

import android.annotation.SuppressLint
import android.os.Bundle
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
import pe.edu.ulima.pm.covidinfo.models.persistence.dao.GlobalDAO
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.GlobalEntity
import java.text.DecimalFormat

class GlobalInfoFragment: Fragment() {

    private lateinit var tviDateGlobal: TextView
    private lateinit var tviConfirmedGlobal: TextView
    private lateinit var tviDeathsGlobal: TextView
    private lateinit var tviRecoveredGlobal: TextView
    private lateinit var tviActiveCasesGlobal: TextView
    private lateinit var tviNewCasesGlobal: TextView
    private lateinit var tviNewRecoveredGlobal: TextView
    private lateinit var tviNewDeathsGlobal: TextView
    private lateinit var tviNewActiveCasesGlobal: TextView

    private var globalEntity: GlobalEntity? = null
    private var globalDAO: GlobalDAO? = null

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

        globalDAO = AppDatabase.getInstance(requireContext()).globalDAO

        pchGlobalData = view.findViewById(R.id.pchGlobalData)

        //Llamando a los TextView de fragment_global_info
        tviDateGlobal = view.findViewById(R.id.tviDateGlobal)
        tviConfirmedGlobal = view.findViewById(R.id.tviConfirmedGlobal)
        tviDeathsGlobal = view.findViewById(R.id.tviTotalDeathsGlobal)
        tviRecoveredGlobal = view.findViewById(R.id.tviRecoveredGlobal)
        tviActiveCasesGlobal = view.findViewById(R.id.tviActiveCasesGlobal)
        tviNewCasesGlobal = view.findViewById(R.id.tviNewCasesGlobal)
        tviNewRecoveredGlobal = view.findViewById(R.id.tviNewRecoveredGlobal)
        tviNewDeathsGlobal = view.findViewById(R.id.tviNewDeathsGlobal)
        tviNewActiveCasesGlobal = view.findViewById(R.id.tviNewActiveCasesGlobal)

        lifecycleScope.launch {
            val globalList = ArrayList(globalDAO!!.getAllGlobal())
            globalEntity = globalList[globalList.size-1]

            setPieChart()

            val active = globalEntity!!.TotalConfirmed.toInt() - globalEntity!!.TotalRecovered.toInt() - globalEntity!!.TotalDeaths.toInt()
            val newActive = globalEntity!!.NewConfirmed.toInt() - globalEntity!!.NewRecovered.toInt() - globalEntity!!.NewDeaths.toInt()
            val formattedDate = globalEntity!!.Date.substring(0, 10).replace("-", " / ")
            val formattedHour = globalEntity!!.Date.substring(11, 19)
            val df = DecimalFormat("###,###,###")

            // Seteando los TextView
            tviDateGlobal.text = "Last updated: $formattedDate at $formattedHour"
            tviConfirmedGlobal.text = df.format(globalEntity!!.TotalConfirmed)
            tviDeathsGlobal.text = df.format(globalEntity!!.TotalDeaths)
            tviRecoveredGlobal.text = df.format(globalEntity!!.TotalRecovered)
            tviActiveCasesGlobal.text = df.format(active)
            tviNewCasesGlobal.text = df.format(globalEntity!!.NewConfirmed)
            tviNewRecoveredGlobal.text = df.format(globalEntity!!.NewRecovered)
            tviNewDeathsGlobal.text = df.format(globalEntity!!.NewDeaths)
            tviNewActiveCasesGlobal.text = df.format(newActive)
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

    private fun getList() : ArrayList<PieEntry>{

        dataList.add(0, PieEntry(65.9f,"Recovered"))
        dataList.add(1, PieEntry(32.0f,"Active"))
        dataList.add(2, PieEntry(2.2f,"Deaths"))

        return dataList
    }

}