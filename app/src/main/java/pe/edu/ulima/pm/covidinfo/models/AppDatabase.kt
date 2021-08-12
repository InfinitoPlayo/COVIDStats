package pe.edu.ulima.pm.covidinfo.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pe.edu.ulima.pm.covidinfo.models.persistence.dao.CountryDAO
import pe.edu.ulima.pm.covidinfo.models.persistence.dao.DateDAO
import pe.edu.ulima.pm.covidinfo.models.persistence.dao.FavoriteDAO
import pe.edu.ulima.pm.covidinfo.models.persistence.dao.GlobalDAO
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.CountryEntity
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.DateEntity
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.FavoriteEntity
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.GlobalEntity

// Configuracion de la bd
@Database(entities = [CountryEntity::class, GlobalEntity::class, DateEntity::class, FavoriteEntity::class], version = 2)
abstract class AppDatabase: RoomDatabase () {

    // Para acceder a los metodos de los DAO
    abstract val countryDAO: CountryDAO
    abstract val globalDAO: GlobalDAO
    abstract val dateDAO: DateDAO
    abstract val favoriteDAO: FavoriteDAO

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        //Instancia unica de la BD
        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "CovidInfo_db")
                    .build().also {
                        INSTANCE = it
                    }
            }
        }
    }
}