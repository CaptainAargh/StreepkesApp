package be.kdg.scoutsappadmin.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class Persoon(
    public var key: String,
    public var persoonNaam: String?,
    public var persoonPass: String?,
    public var persoonConsumpties: List<Consumptie>?,
    public var persoonRol: Rol
) : Parcelable {
    constructor() : this("", "","",
        emptyList(), Rol.LEIDER
    )


    @PrimaryKey(autoGenerate = true)
    private var persoonId = 0



    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "key" to key,
            "persoonNaam" to persoonNaam,
            "persoonPass" to persoonPass,
            "persoonConsumpties" to persoonConsumpties,
            "persoonRol" to persoonRol

        )
    }






    override fun toString(): String {
        return "Persoon(persoonId=$persoonId, persoonNaam=$persoonNaam, persoonPass=$persoonPass, persoonConsumpties=$persoonConsumpties, persoonRol=$persoonRol)"
    }


}