package pe.edu.ulima.pm.covidinfo.models.persistence.dao

import androidx.room.*
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.CountryEntity
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.FavoriteEntity

@Dao
interface FavoriteDAO {

    @Query("SELECT * FROM FavoriteEntity")
    suspend fun getAllFavorites(): List<FavoriteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM FavoriteEntity WHERE ID = :ID")
    suspend fun deleteSingleFavorite(ID: String)

    @Transaction
    @Query("SELECT * FROM FavoriteEntity WHERE country = :country")
    suspend fun getSingleFavorite(country: String): FavoriteEntity?

    @Transaction
    @Query("SELECT * FROM FavoriteEntity WHERE ID = :ID")
    suspend fun getSingleFavoriteByID(ID: String): FavoriteEntity?

    @Transaction
    @Query("SELECT * FROM FavoriteEntity WHERE Country = :country")
    suspend fun getFavoritesWithSameName(country: String): List<FavoriteEntity>
}