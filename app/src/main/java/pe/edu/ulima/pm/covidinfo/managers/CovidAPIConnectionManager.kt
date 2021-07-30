package pe.edu.ulima.pm.covidinfo.managers

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//Configurar conexion al API
class CovidAPIConnectionManager {

    companion object{
        private var instance:CovidAPIConnectionManager? = null

        fun getInstance() : CovidAPIConnectionManager {
            if (instance == null) {
                instance = CovidAPIConnectionManager()
            }
            return instance!!
        }
    }

    private var retrofit: Retrofit? = null

    init {
        retrofit = Retrofit.Builder()
            .baseUrl("https://api.covid19api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getRetrofit(): Retrofit {
        return retrofit!!
    }
}