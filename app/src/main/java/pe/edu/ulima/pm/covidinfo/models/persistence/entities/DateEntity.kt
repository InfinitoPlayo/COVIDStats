package pe.edu.ulima.pm.covidinfo.models.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Se guarda la fecha de actualizacion
@Entity
data class DateEntity (
    @PrimaryKey(autoGenerate = true) val ID: Int,
    @ColumnInfo(name = "Date") val Date: String
)