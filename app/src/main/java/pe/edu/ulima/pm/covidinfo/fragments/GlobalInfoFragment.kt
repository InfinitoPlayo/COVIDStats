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
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import pe.edu.ulima.pm.covidinfo.R
import pe.edu.ulima.pm.covidinfo.objects.GlobalDataInfo
import pe.edu.ulima.pm.covidinfo.objects.InternetConnection
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

    private var gd = GlobalDataInfo.globalData?.Global
    private var totalActiveCasesGlobal: Int? = null /*gd.TotalConfirmed - gd.TotalDeaths - gd.TotalRecovered*/

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

        //Llamando a los TextView de fragment_global_info
        tviDateGlobal = view.findViewById(R.id.tviDateGlobal)
        tviConfirmedGlobal = view.findViewById(R.id.tviConfirmedGlobal)
        tviDeathsGlobal = view.findViewById(R.id.tviTotalDeathsGlobal)
        tviRecoveredGlobal = view.findViewById(R.id.tviRecoveredGlobal)
        tviActiveCasesGlobal = view.findViewById(R.id.tviActiveCasesGlobal)
        tviNewCasesGlobal = view.findViewById(R.id.tviNewCasesGlobal)
        tviNewRecoveredGlobal = view.findViewById(R.id.tviNewRecoveredGlobal)
        tviNewDeathsGlobal = view.findViewById(R.id.tviNewDeathsGlobal)

        // Si hay conexion
        if (InternetConnection.isConnected) {

            setPieChart()
            totalActiveCasesGlobal = calculateActiveCases()
            val formattedDate = gd?.Date?.substring(0, 10)?.replace("-", " / ")
            val formattedHour = gd?.Date?.substring(11, 19)
            val formattedDateHour = "$formattedDate at $formattedHour"
            val df = DecimalFormat("###,###,###")

            // Seteando los TextView
            tviDateGlobal.text = "Last updated at $formattedDateHour"
            tviConfirmedGlobal.text = "Total confirmed cases: ${df.format(gd?.TotalConfirmed)}"
            tviDeathsGlobal.text = "Total deaths: ${df.format(gd?.TotalDeaths)}"
            tviRecoveredGlobal.text = "Total recovered: ${df.format(gd?.TotalRecovered)}"
            tviActiveCasesGlobal.text = "Total active cases: ${df.format(totalActiveCasesGlobal)}"
            tviNewCasesGlobal.text = "New cases: ${df.format(gd?.NewConfirmed)}"
            tviNewRecoveredGlobal.text = "New recovered: ${df.format(gd?.NewRecovered)}"
            tviNewDeathsGlobal.text = "New deaths: ${df.format(gd?.NewDeaths)}"

            //Si no hay conexion
        } else {

            setPieChart()
            // Seteando los TextView
            tviDateGlobal.text = "Last updated: 2021-07-20T19:19:37.181Z"
            tviConfirmedGlobal.text = "Total confirmed cases: 189736109"
            tviDeathsGlobal.text = "Total deaths: 4079720"
            tviRecoveredGlobal.text = "Total recovered: 125035616"
            tviActiveCasesGlobal.text = "Total active cases: 60620773"
        }
    }

    //Configurar el PieChart
    private fun setPieChart() {

        val pieDataSet = PieDataSet(getList(), "")
        val pieData = PieData(pieDataSet)

        /*for (i in ColorTemplate.MATERIAL_COLORS) {
            colors.add(i)
        }*/

        colors.add(ContextCompat.getColor(requireContext(), R.color.green_400))
        colors.add(ContextCompat.getColor(requireContext(), R.color.orange_400))
        colors.add(ContextCompat.getColor(requireContext(), R.color.red_400))

        pieDataSet.colors = colors
        pieData.setValueTextSize(10f)
        pieData.setValueFormatter(PercentFormatter(pchGlobalData))
        pchGlobalData.animateXY(1500,1500)
        pchGlobalData.data = pieData
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

    private fun calculateActiveCases(): Int {

        val a = gd?.TotalConfirmed
        val b = gd?.TotalDeaths?.toInt()
        val c = gd?.TotalRecovered?.toInt()

        return if (a == null && b == null && c == null){
            0
        } else {
            (a!! - b!! - c!!).toInt()
        }
    }

}