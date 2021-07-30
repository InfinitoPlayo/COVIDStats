package pe.edu.ulima.pm.covidinfo.models.dao

import java.io.Serializable

// Simplemente para poder traer la fecha del formato JSON
data class Date(
    var ID: Int,
    var Date: String
) : Serializable