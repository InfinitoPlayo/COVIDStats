package pe.edu.ulima.pm.covidinfo.models.dao

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url

//https://api.covid19api.com

// Le agregamos el header y token para acceder a la data con nuestra suscripcion
interface CovidAPIService {

    @Headers("X-Access-Token: 5cf9dfd5-3449-485e-b5ae-70a60e997864")
    @GET
    suspend fun getGlobalData(@Url url: String): Response<GlobalData>

    @Headers("X-Access-Token: 5cf9dfd5-3449-485e-b5ae-70a60e997864")
    @GET
    suspend fun getPremiumGlobalData(@Url url: String): Response<PremiumGlobalData>

    @Headers("X-Access-Token: 5cf9dfd5-3449-485e-b5ae-70a60e997864")
    @GET
    suspend fun getCountryHistoricalStats(@Url url: String): Response<ArrayList<CountryHistoricalData>>
}