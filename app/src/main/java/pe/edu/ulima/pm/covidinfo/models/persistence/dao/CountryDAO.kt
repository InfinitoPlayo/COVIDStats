package pe.edu.ulima.pm.covidinfo.models.persistence.dao

import androidx.room.*
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.CountryEntity

@Dao
interface CountryDAO {

    @Query("SELECT * FROM CountryEntity")
    suspend fun getAllCountries(): List<CountryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountry(country: CountryEntity)

    @Query("DELETE FROM CountryEntity WHERE ID = :ID")
    suspend fun deleteSingleCountry(ID: String)

    @Transaction
    @Query("SELECT * FROM CountryEntity WHERE Country = :Country")
    suspend fun getSingleCountry(Country: String): CountryEntity

    @Transaction
    @Query("SELECT * FROM CountryEntity WHERE ID = :ID")
    suspend fun getSingleCountryByID(ID: String): CountryEntity

    @Transaction
    @Query("SELECT * FROM CountryEntity WHERE CountryISO = :ISO")
    suspend fun getSingleCountryByISO(ISO: String): CountryEntity
}