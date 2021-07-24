package pe.edu.ulima.pm.covidinfo.models.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Creamos nuestro data class de Global  para la persistencia a recibir de la api y poder mostrar la data de todos los países
@Entity
data class GlobalEntity(
    @PrimaryKey(autoGenerate = true) val ID: Int,
    @ColumnInfo(name = "NewConfirmed") var NewConfirmed: Long,
    @ColumnInfo(name = "TotalConfirmed") var TotalConfirmed: Long,
    @ColumnInfo(name = "NewDeaths") var NewDeaths: Long,
    @ColumnInfo(name = "TotalDeaths") var TotalDeaths: Long,
    @ColumnInfo(name = "NewRecovered") var NewRecovered: Long,
    @ColumnInfo(name = "TotalRecovered") var TotalRecovered: Long,
    @ColumnInfo(name = "Date") var Date: String
)