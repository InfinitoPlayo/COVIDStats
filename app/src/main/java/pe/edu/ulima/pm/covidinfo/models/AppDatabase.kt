package pe.edu.ulima.pm.covidinfo.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pe.edu.ulima.pm.covidinfo.models.persistence.dao.*
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.*

// Configuracion de la bd
@Database(entities = [CountryEntity::class, GlobalEntity::class, DateEntity::class, FavoriteEntity::class, TestEntity::class, ContinentEntity::class], version = 4)
abstract class AppDatabase: RoomDatabase () {

    // Para acceder a los metodos de los DAO
    abstract val countryDAO: CountryDAO
    abstract val globalDAO: GlobalDAO
    abstract val dateDAO: DateDAO
    abstract val favoriteDAO: FavoriteDAO
    abstract val testDAO: TestDAO
    abstract val continentDAO: ContinentDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /*private val MIGRATION_3_4 = object : Migration(3, 4){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `ContinentEntity` (`ID` INTEGER NOT NULL, `updated` INTEGER, `cases` INTEGER, `todayCases` INTEGER, `deaths` INTEGER, `todayDeaths` INTEGER, `recovered` INTEGER, `todayRecovered` INTEGER, `active` INTEGER, `critical` INTEGER, `casesPerOneMillion` REAL, `deathsPerOneMillion` REAL, `tests` INTEGER, `testsPerOneMillion` REAL, `population` INTEGER, `continent` TEXT, `activePerOneMillion` REAL, `recoveredPerOneMillion` REAL, `criticalPerOneMillion` REAL, PRIMARY KEY(`id`))")
            }
        }*/

        //Instancia unica de la BD
        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "CovidInfo_db")
                    /*.addMigrations(MIGRATION_3_4)*/
                    .build().also {
                        INSTANCE = it
                    }
            }
        }
    }
}