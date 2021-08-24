package pe.edu.ulima.pm.covidinfo.models.services

import pe.edu.ulima.pm.covidinfo.models.dao.NovelCOVIDContinent
import pe.edu.ulima.pm.covidinfo.models.dao.NovelCOVIDCountry
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface NovelCOVIDService {
    @GET
    suspend fun getCountriesData(@Url url: String): Response<ArrayList<NovelCOVIDCountry>>

    @GET
    suspend fun getContinentsData(@Url url: String): Response<ArrayList<NovelCOVIDContinent>>
}