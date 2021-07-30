package pe.edu.ulima.pm.covidinfo.models.persistence.dao

import androidx.room.*
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.CountryEntity

// Aqui es donde vamos a interactuar con nuestro entidad País para poder luego agregarlos a la bd y mostrarlos en el recycler Vi
@Dao
interface CountryDAO {

    @Query("SELECT * FROM CountryEntity")
    suspend fun getAllCountries(): List<CountryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountry(country: CountryEntity)

    @Query("DELETE FROM CountryEntity WHERE ID = :ID")
    suspend fun deleteSingleCountry(ID: String)

    //Query para buscar un pais especifico
    @Transaction
    @Query("SELECT * FROM CountryEntity WHERE Country = :Country")
    suspend fun getSingleCountry(Country: String): CountryEntity

    //Query para buscar un pais especifico
    @Transaction
    @Query("SELECT * FROM CountryEntity WHERE ID = :ID")
    suspend fun getSingleCountryByID(ID: String): CountryEntity


}