package pe.edu.ulima.pm.covidinfo.models.persistence.dao

import androidx.room.*
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.FavoriteEntity

@Dao
interface FavoriteDAO {

    @Query("SELECT * FROM FavoriteEntity")
    suspend fun getAllFavorites(): List<FavoriteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM FavoriteEntity WHERE ID = :ID")
    suspend fun deleteSingleFavorite(ID: String)

    //Query para buscar un pais especifico
    @Transaction
    @Query("SELECT * FROM FavoriteEntity WHERE ID = :ID")
    suspend fun getSingleFavorite(ID: String): FavoriteEntity

    //Query para buscar un pais especifico
    @Transaction
    @Query("SELECT * FROM FavoriteEntity WHERE Country = :country")
    suspend fun getFavoritesWithSameName(country: String): List<FavoriteEntity>
}