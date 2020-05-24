package be.kdg.scoutsappadmin.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Entity
@Parcelize
data class Periode(
    @PropertyName("periode_key") public var key:String,
    @PropertyName("periode_naam") public var periodeNaam: String?,
    @PropertyName("periode_dagen") public var periodeDagen: List<Dag>?,
    @PropertyName("periode_personen") public var periodePersonen: List<Persoon>?
) : Parcelable {
    constructor() : this("", "",
        emptyList(), emptyList()
    )


    @PrimaryKey(autoGenerate = true)
     var periodeId = 0


    override fun toString(): String {
        return "Periode(periodeId=$periodeId, key=$key, periodeNaam=$periodeNaam, periodePersonen=$periodePersonen)"
    }
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "key" to key,
            "periodeNaam" to periodeNaam,
            "periodeDagen" to periodeDagen,
            "periodePersonen" to periodePersonen
            )
    }



}