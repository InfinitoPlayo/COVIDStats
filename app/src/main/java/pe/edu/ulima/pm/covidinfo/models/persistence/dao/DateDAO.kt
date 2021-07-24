package pe.edu.ulima.pm.covidinfo.models.persistence.dao

import androidx.room.*
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.DateEntity

// Solo generamos para mostrar las fechas para el análisis del gráfico
@Dao
interface DateDAO {

    @Query("SELECT * FROM DateEntity")
    suspend fun getAllDates(): List<DateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDate(date: DateEntity)

    //Query para buscar un pais especifico
    @Transaction
    @Query("SELECT * FROM DateEntity WHERE ID = :ID")
    suspend fun getSingleDate(ID: String): DateEntity
}