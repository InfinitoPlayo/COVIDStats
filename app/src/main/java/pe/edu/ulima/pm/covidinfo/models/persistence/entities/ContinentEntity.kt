package pe.edu.ulima.pm.covidinfo.models.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ContinentEntity(
    @PrimaryKey(autoGenerate = true) var ID: Int,
    @ColumnInfo(name = "updated") var updated: Long?,
    @ColumnInfo(name = "cases") var cases: Long?,
    @ColumnInfo(name = "todayCases") var todayCases: Long?,
    @ColumnInfo(name = "deaths") var deaths: Long?,
    @ColumnInfo(name = "todayDeaths") var todayDeaths: Long?,
    @ColumnInfo(name = "recovered") var recovered: Long?,
    @ColumnInfo(name = "todayRecovered") var todayRecovered: Long?,
    @ColumnInfo(name = "active") var active: Long?,
    @ColumnInfo(name = "critical") var critical: Long?,
    @ColumnInfo(name = "casesPerOneMillion") var casesPerOneMillion: Double?,
    @ColumnInfo(name = "deathsPerOneMillion") var deathsPerOneMillion: Double?,
    @ColumnInfo(name = "tests") var tests: Long?,
    @ColumnInfo(name = "testsPerOneMillion") var testsPerOneMillion: Double?,
    @ColumnInfo(name = "population") var population: Long?,
    @ColumnInfo(name = "continent") var continent: String?,
    @ColumnInfo(name = "activePerOneMillion") var activePerOneMillion: Double?,
    @ColumnInfo(name = "recoveredPerOneMillion") var recoveredPerOneMillion: Double?,
    @ColumnInfo(name = "criticalPerOneMillion") var criticalPerOneMillion: Double?
)
