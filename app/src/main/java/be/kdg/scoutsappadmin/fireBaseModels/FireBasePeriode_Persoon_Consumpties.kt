package be.kdg.scoutsappadmin.fireBaseModels

class FireBasePeriode_Persoon_Consumpties(
    val key: String,
    val consumptieGegevenDoor: String,
    val consumptieGegevenDoorId: String,
    val timeStamp: Long
) {
    constructor() : this("", "", "", 0)
}