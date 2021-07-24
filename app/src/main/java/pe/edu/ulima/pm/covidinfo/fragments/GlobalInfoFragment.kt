package pe.edu.ulima.pm.covidinfo.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

class GlobalInfoFragment: Fragment() {

    private lateinit var tviDateGlobal: TextView
    private lateinit var tviTotalConfirmedGlobal: TextView
    private lateinit var tviTotalDeathsGlobal: TextView
    private lateinit var tviTotalRecoveredGlobal: TextView
    private lateinit var tviTotalActiveCasesGlobal: TextView

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
        tviTotalConfirmedGlobal = view.findViewById(R.id.tviTotalConfirmedGlobal)
        tviTotalDeathsGlobal = view.findViewById(R.id.tviTotalDeathsGlobal)
        tviTotalRecoveredGlobal = view.findViewById(R.id.tviTotalRecoveredGlobal)
        tviTotalActiveCasesGlobal = view.findViewById(R.id.tviTotalActiveCasesGlobal)

        // Si hay conexion
        if (InternetConnection.isConnected) {

            setPieChart()

            totalActiveCasesGlobal = calculateActiveCases()

            // Seteando los TextView
            tviDateGlobal.text = "Last updated: ${gd?.Date}"
            tviTotalConfirmedGlobal.text = "Total confirmed cases: ${gd?.TotalConfirmed}"
            tviTotalDeathsGlobal.text = "Total deaths: ${gd?.TotalDeaths}"
            tviTotalRecoveredGlobal.text = "Total recovered: ${gd?.TotalRecovered}"
            tviTotalActiveCasesGlobal.text = "Total active cases: $totalActiveCasesGlobal"

            //Si no hay conexion
        } else {

            setPieChart()
            // Seteando los TextView
            tviDateGlobal.text = "Last updated: 2021-07-20T19:19:37.181Z"
            tviTotalConfirmedGlobal.text = "Total confirmed cases: 189736109"
            tviTotalDeathsGlobal.text = "Total deaths: 4079720"
            tviTotalRecoveredGlobal.text = "Total recovered: 125035616"
            tviTotalActiveCasesGlobal.text = "Total active cases: 60620773"
        }
    }

    //Configurar el PieChart
    private fun setPieChart() {

        val pieDataSet = PieDataSet(getList(), "")
        val pieData = PieData(pieDataSet)

        for (i in ColorTemplate.COLORFUL_COLORS) {
            colors.add(i)
        }

        pieDataSet.colors = colors
        pieData.setValueTextSize(10f)
        pieData.setValueFormatter(PercentFormatter(pchGlobalData))
        pchGlobalData.animateXY(2000,2000)
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