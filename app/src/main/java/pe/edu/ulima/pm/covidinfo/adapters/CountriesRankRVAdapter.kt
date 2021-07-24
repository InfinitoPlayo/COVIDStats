package pe.edu.ulima.pm.covidinfo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pe.edu.ulima.pm.covidinfo.R
import pe.edu.ulima.pm.covidinfo.models.dao.PremiumSingleCountryData

interface OnCountryRankItemClickListener {
    fun onClick(country: PremiumSingleCountryData)
}

class CountriesRankRVAdapter: RecyclerView.Adapter<CountriesRankRVAdapter.ViewHolder> {

    class ViewHolder: RecyclerView.ViewHolder {
        var tviCountryName: TextView? = null
        var tviCountryInfo: TextView? = null
        var tviCountryPosition: TextView? = null

        constructor(view: View) : super(view) {
            tviCountryName = view.findViewById(R.id.tviCountryNameR)
            tviCountryInfo = view.findViewById(R.id.tviCountryInfoR)
            tviCountryPosition = view.findViewById(R.id.tviCountryPosition)
        }
    }

    private var countries: ArrayList<PremiumSingleCountryData>? = null
    private var listener: OnCountryRankItemClickListener? = null
    private var context: Context? = null

    constructor(countries : ArrayList<PremiumSingleCountryData>,
                listener: OnCountryRankItemClickListener,
                context: Context) : super() {
        this.countries = countries
        this.listener = listener
        this.context = context }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.item_country_rank, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = countries!![position]
        val pos = position+1

        //Se muestran los datos de la competicion en el RecyclerView
        holder.tviCountryName!!.text = country.Country
        holder.tviCountryInfo!!.text = "Cases per million: ${country.TotalCasesPerMillion}"
        holder.tviCountryPosition!!.text = "${pos}. "

        holder.itemView.setOnClickListener {
            listener!!.onClick(countries!![position])
        }
    }

    override fun getItemCount(): Int {
        return countries!!.size
    }
}