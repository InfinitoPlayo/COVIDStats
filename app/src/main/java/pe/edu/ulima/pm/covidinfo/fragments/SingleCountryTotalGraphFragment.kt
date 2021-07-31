package pe.edu.ulima.pm.covidinfo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.fragment_global_info_piechart.*
import pe.edu.ulima.pm.covidinfo.R
import pe.edu.ulima.pm.covidinfo.SingleCountryActivity
import pe.edu.ulima.pm.covidinfo.managers.CovidInfoManager
import pe.edu.ulima.pm.covidinfo.models.dao.CountryHistoricalData
import pe.edu.ulima.pm.covidinfo.objects.InternetConnection
import pe.edu.ulima.pm.covidinfo.objects.SingleCountryHistoricalStats

class SingleCountryTotalGraphFragment: Fragment() {

    private var tviChartInfo: TextView? = null
    private var lineChart: LineChart? = null
    lateinit var countryList : ArrayList<Entry>
    private val limitedList = arrayOfNulls<CountryHistoricalData>(21)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_single_country_total_graph, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tviChartInfo = view.findViewById(R.id.tviChartInfo)

        //Si hay internet
        if (requireActivity().intent.getStringExtra("IsConnected") == "true") {
            getLastTwentyItems()

            lineChart = view.findViewById(R.id.lineChart)
            countryList = ArrayList()

            val lineDataSet = LineDataSet(getList(), "Total cases")
            lineDataSet.setDrawFilled(true)
            val lineData = LineData(lineDataSet)

            lineChart!!.data = lineData
            lineChart!!.animateXY(1000, 1000)
            lineChart!!.isDragEnabled = true
            lineChart!!.setScaleEnabled(true)
            lineChart!!.setDrawGridBackground(false)
            lineChart!!.description.isEnabled = false
            lineChart!!.invalidate()
        }

        //Si no hay internet, no se carga el grafico
        else {
            tviChartInfo!!.text = getString(R.string.internet_connection_is_needed)
        }
    }


    private fun getList(): ArrayList<Entry>{
        for (i in 1..20) {
            countryList.add(Entry(i.toFloat(), limitedList[i-1]?.Confirmed!!.toFloat()))
        }
        return  countryList
    }

    // Obtener ultimos 20 registros del pais
    private fun getLastTwentyItems() {
        val list = SingleCountryHistoricalStats.countryHistoricalData
        var index = 0

        for (i in 1..list!!.size) {
            if (i >= list.size-20) {
                index++
                limitedList[index-1] = list[i-1]
            }
        }
    }
}