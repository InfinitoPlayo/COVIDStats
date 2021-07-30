package pe.edu.ulima.pm.covidinfo.objects

import pe.edu.ulima.pm.covidinfo.models.dao.PremiumGlobalData
import pe.edu.ulima.pm.covidinfo.models.dao.PremiumSingleCountryData
import java.io.Serializable

object PremiumGlobalDataInfo {

    var premiumGlobalData: PremiumGlobalData? = null
    var premiumCountriesData: ArrayList<PremiumSingleCountryData>? = null
}