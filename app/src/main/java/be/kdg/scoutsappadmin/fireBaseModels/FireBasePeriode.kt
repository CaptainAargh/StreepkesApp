package be.kdg.scoutsappadmin.fireBaseModels


class FireBasePeriode(
    val key: String,
    val periodeNaam: String,
    val periodeDagen: Map<String, FireBasePeriode_Dag>,
    val periodePersonen: Map<String, FireBasePeriode_Persoon>
) {
    constructor() : this(
        "",
        "",
        emptyMap<String, FireBasePeriode_Dag>(),
        emptyMap<String, FireBasePeriode_Persoon>()
    )
}