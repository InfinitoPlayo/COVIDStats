package pe.edu.ulima.pm.covidinfo.models.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import pe.edu.ulima.pm.covidinfo.models.persistence.entities.TestEntity

@Dao
interface TestDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: TestEntity)
}