package pe.edu.ulima.pm.covidinfo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import pe.edu.ulima.pm.covidinfo.R
import pe.edu.ulima.pm.covidinfo.objects.InternetConnection
import pe.edu.ulima.pm.covidinfo.objects.SingleCountryHistoricalStats

class SingleCountryExtendedGraphFragment: Fragment() {

    private var tviChartInfo: TextView? = null
    private var lineChart: LineChart? = null
    private var entryList : ArrayList<Entry> = ArrayList()
    private var dataList = SingleCountryHistoricalStats.countryHistoricalData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_single_country_extended_graph, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tviChartInfo = view.findViewById(R.id.tviChartInfo)

        //Si hay internet
        if (InternetConnection.isConnected) {

            lineChart = view.findViewById(R.id.extendedLineChart)
            val lineDataSet = LineDataSet(getList(), "Total cases")
            lineDataSet.setDrawFilled(true)
            val lineData = LineData(lineDataSet)

            lineChart!!.data = lineData
            lineChart!!.animateXY(1000, 1000)
            lineChart!!.isDragEnabled = false
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
        for (i in 1..dataList!!.size) {
            entryList.add(Entry(i.toFloat(), dataList!![i-1].Confirmed.toFloat()))
        }
        return  entryList
    }

}