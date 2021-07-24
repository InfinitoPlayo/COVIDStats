package pe.edu.ulima.pm.covidinfo.models.persistence.dao

import androidx.room.*
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.GlobalEntity

//Aqui interactuaremos con la entidad dglobal para mostralo en la interfaz
@Dao
interface GlobalDAO {

    @Query("SELECT * FROM GlobalEntity")
    suspend fun getAllGlobal(): List<GlobalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobal(global: GlobalEntity)

    //Query para buscar un global especifico
    @Transaction
    @Query("SELECT * FROM GlobalEntity WHERE ID = :ID")
    suspend fun getSingleGlobal(ID: String): GlobalEntity
}