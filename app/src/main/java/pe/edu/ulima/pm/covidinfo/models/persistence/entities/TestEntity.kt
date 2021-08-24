package pe.edu.ulima.pm.covidinfo.models.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TestEntity(
    @PrimaryKey(autoGenerate = true) val ID: Int?,
    @ColumnInfo(name = "test") var test: String?,
)
