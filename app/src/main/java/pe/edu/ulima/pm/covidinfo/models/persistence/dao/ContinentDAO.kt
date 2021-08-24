package pe.edu.ulima.pm.covidinfo.models.persistence.dao

import androidx.room.*
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.ContinentEntity

@Dao
interface ContinentDAO {

    @Query("SELECT * FROM ContinentEntity")
    suspend fun getAllContinents(): List<ContinentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContinent(continent: ContinentEntity)

    @Query("DELETE FROM ContinentEntity WHERE ID = :ID")
    suspend fun deleteSingleContinent(ID: Int)

    //Query para buscar un continente especifico
    @Transaction
    @Query("SELECT * FROM ContinentEntity WHERE ID = :ID")
    suspend fun getSingleContinent(ID: Int): ContinentEntity
}