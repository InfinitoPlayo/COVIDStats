package pe.edu.ulima.pm.covidinfo.models.services

import pe.edu.ulima.pm.covidinfo.models.dao.LocationData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

//http://ip-api.com

interface IPAPIService {

    @GET
    suspend fun getLocation(@Url url: String): Response<LocationData>
}