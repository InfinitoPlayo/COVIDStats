package pe.edu.ulima.pm.covidinfo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pe.edu.ulima.pm.covidinfo.R
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.CountryEntity
import java.text.DecimalFormat

interface OnCountryInfoItemClickListener {
    fun onClick(country: CountryEntity)
}

class CountriesInfoRVAdapter: RecyclerView.Adapter<CountriesInfoRVAdapter.ViewHolder> {

    class ViewHolder: RecyclerView.ViewHolder {
        var tviCountryName: TextView? = null
        var tviCountryInfo: TextView? = null

        constructor(view: View) : super(view) {
            tviCountryName = view.findViewById(R.id.tviCountryName)
            tviCountryInfo = view.findViewById(R.id.tviCountryInfo)
        }
    }

    private var countries: ArrayList<CountryEntity>? = null
    private var listener: OnCountryInfoItemClickListener? = null
    private var context: Context? = null

    constructor(countries : ArrayList<CountryEntity>,
                listener: OnCountryInfoItemClickListener,
                context: Context) : super() {
        this.countries = countries
        this.listener = listener
        this.context = context }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.item_country_info, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = countries!![position]
        val df = DecimalFormat("###,###,###")

        //Se muestran los datos de la competicion en el RecyclerView
        holder.tviCountryName!!.text = country.Country
        holder.tviCountryInfo!!.text = "Confirmed cases: ${df.format(country.TotalCases)}"

        holder.itemView.setOnClickListener {
            listener!!.onClick(countries!![position])
        }
    }

    override fun getItemCount(): Int {
        return countries!!.size
    }
}