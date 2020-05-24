package be.kdg.scoutsappadmin.fireBaseModels

class FireBasePeriode_Dag(
    val key: String,
    val dagDatum: String,
    val dagPersonen: Map<String, FireBasePeriode_Dag_Persoon>
) {
    constructor() : this(
        "",
        "",
        emptyMap<String, FireBasePeriode_Dag_Persoon>()
    )
}
