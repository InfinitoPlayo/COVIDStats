package pe.edu.ulima.pm.covidinfo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pe.edu.ulima.pm.covidinfo.R
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.FavoriteEntity

interface OnFavoriteCountryItemClickListener {
    fun onClick(country: FavoriteEntity)
}

class FavoriteCountriesRVAdapter: RecyclerView.Adapter<FavoriteCountriesRVAdapter.ViewHolder> {

    class ViewHolder: RecyclerView.ViewHolder {
        var tviCountryName: TextView? = null
        var tviCountryInfo: TextView? = null

        constructor(view: View) : super(view) {
            tviCountryName = view.findViewById(R.id.tviFavoriteCountryName)
            tviCountryInfo = view.findViewById(R.id.tviFavoriteCountryInfo)
        }
    }

    private var countries: ArrayList<FavoriteEntity>? = null
    private var listener: OnFavoriteCountryItemClickListener? = null
    private var context: Context? = null

    constructor(countries : ArrayList<FavoriteEntity>,
                listener: OnFavoriteCountryItemClickListener,
                context: Context) : super() {
        this.countries = countries
        this.listener = listener
        this.context = context }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_country_info, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = countries!![position]

        //Se muestran los datos de la competicion en el RecyclerView
        holder.tviCountryName!!.text = country.Country
        holder.tviCountryInfo!!.text = "Confirmed cases: ${country.TotalCases}"

        holder.itemView.setOnClickListener {
            listener!!.onClick(countries!![position])
        }
    }

    override fun getItemCount(): Int {
        return countries!!.size
    }
}