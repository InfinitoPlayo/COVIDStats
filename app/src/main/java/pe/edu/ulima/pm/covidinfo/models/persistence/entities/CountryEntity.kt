package pe.edu.ulima.pm.covidinfo.models.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Data class con datos para cada pais
@Entity
data class CountryEntity (
    @PrimaryKey(autoGenerate = false) var ID: String,
    @ColumnInfo(name = "CountryISO") var CountryISO: String,
    @ColumnInfo(name = "Country") var Country: String,
    @ColumnInfo(name = "Continent") var Continent: String,
    @ColumnInfo(name = "Date") var Date: String,
    @ColumnInfo(name = "TotalCases") var TotalCases: Long,
    @ColumnInfo(name = "NewCases") var NewCases: Long,
    @ColumnInfo(name = "TotalDeaths") var TotalDeaths: Long,
    @ColumnInfo(name = "NewDeaths") val NewDeaths: Long,
    @ColumnInfo(name = "TotalCasesPerMillion") var TotalCasesPerMillion: Double,
    @ColumnInfo(name = "NewCasesPerMillion") var NewCasesPerMillion: Double,
    @ColumnInfo(name = "TotalDeathsPerMillion") var TotalDeathsPerMillion: Double,
    @ColumnInfo(name = "NewDeathsPerMillion") var NewDeathsPerMillion: Double,
    @ColumnInfo(name = "StringencyIndex") var StringencyIndex: Long,
    @ColumnInfo(name = "DailyIncidenceConfirmedCases") var DailyIncidenceConfirmedCases: Double,
    @ColumnInfo(name = "DailyIncidenceConfirmedDeaths") var DailyIncidenceConfirmedDeaths: Double,
    @ColumnInfo(name = "DailyIncidenceConfirmedCasesPerMillion") var DailyIncidenceConfirmedCasesPerMillion: Double,
    @ColumnInfo(name = "DailyIncidenceConfirmedDeathsPerMillion") var DailyIncidenceConfirmedDeathsPerMillion: Double,
    @ColumnInfo(name = "IncidenceRiskConfirmedPerHundredThousand") var IncidenceRiskConfirmedPerHundredThousand: Double,
    @ColumnInfo(name = "IncidenceRiskDeathsPerHundredThousand") var IncidenceRiskDeathsPerHundredThousand: Double,
    @ColumnInfo(name = "IncidenceRiskNewConfirmedPerHundredThousand") var IncidenceRiskNewConfirmedPerHundredThousand: Double,
    @ColumnInfo(name = "IncidenceRiskNewDeathsPerHundredThousand") var IncidenceRiskNewDeathsPerHundredThousand: Double,
    @ColumnInfo(name = "CaseFatalityRatio") var CaseFatalityRatio: Double
)