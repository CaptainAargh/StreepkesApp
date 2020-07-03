package be.kdg.scoutsappadmin.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class Dag(
    @PropertyName("dag_key") public var key: String?,
    @PropertyName("dag_Datum") public var dagDatum: String?,
    @PropertyName("dag_Personen") public var dagPersonen: List<Persoon>?
) : Parcelable {
    constructor() : this("", "",
        emptyList()
    )

    @PrimaryKey(autoGenerate = true)
    private var dagId = 0

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "key" to key,
            "dagDatum" to dagDatum,
            "dagPersonen" to dagPersonen
        )
    }

    override fun toString(): String {
        return "Dag(dagId=$dagId, dagDatum=$dagDatum, dagPersonen=$dagPersonen)"
    }


}