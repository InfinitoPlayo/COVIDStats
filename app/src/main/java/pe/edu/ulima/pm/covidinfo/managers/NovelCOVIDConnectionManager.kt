package pe.edu.ulima.pm.covidinfo.managers

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NovelCOVIDConnectionManager {

    companion object{
        private var instance:NovelCOVIDConnectionManager? = null

        fun getInstance() : NovelCOVIDConnectionManager {
            if (instance == null) {
                instance = NovelCOVIDConnectionManager()
            }
            return instance!!
        }
    }

    private var retrofit: Retrofit? = null

    init {
        retrofit = Retrofit.Builder()
            .baseUrl("https://corona.lmao.ninja")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getRetrofit(): Retrofit {
        return retrofit!!
    }
}