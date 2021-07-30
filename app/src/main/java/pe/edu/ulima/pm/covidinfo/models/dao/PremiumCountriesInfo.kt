package pe.edu.ulima.pm.covidinfo.models.dao

import java.io.Serializable

data class PremiumCountriesInfo(var countries: ArrayList<PremiumSingleCountryData>): Serializable
