package be.kdg.scoutsappadmin.fireBaseModels

import be.kdg.scoutsappadmin.model.Rol

class FireBasePeriode_Dag_Persoon(
    val key: String,
    val persoonNaam: String,
    val persoonPass: String,
    val persoonConsumpties: Map<String, FireBasePeriode_Dag_Persoon_Consumpties>,
    val persoonRol: Rol
) {
    constructor() : this(
        "",
        "",
        "",
        emptyMap<String, FireBasePeriode_Dag_Persoon_Consumpties>(),
        Rol.LEIDER
    )
}