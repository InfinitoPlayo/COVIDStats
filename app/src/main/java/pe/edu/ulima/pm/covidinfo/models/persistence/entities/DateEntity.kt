package pe.edu.ulima.pm.covidinfo.models.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Cuando hacemos el call dataPremiumGlobal entre otras cosas nos devuelve el request al fecha
@Entity
data class DateEntity (
    @PrimaryKey(autoGenerate = true) val ID: Int,
    @ColumnInfo(name = "Date") val Date: String
)