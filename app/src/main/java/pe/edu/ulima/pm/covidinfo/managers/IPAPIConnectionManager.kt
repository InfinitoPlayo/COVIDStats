package pe.edu.ulima.pm.covidinfo.managers

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Configurar conexion al API para obtener el pais
class IPAPIConnectionManager {

    companion object{
        private var instance:IPAPIConnectionManager? = null

        fun getInstance() : IPAPIConnectionManager {
            if (instance == null) {
                instance = IPAPIConnectionManager()
            }
            return instance!!
        }
    }

    private var retrofit: Retrofit? = null

    init {
        retrofit = Retrofit.Builder()
            .baseUrl("http://ip-api.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getRetrofit(): Retrofit {
        return retrofit!!
    }
}
