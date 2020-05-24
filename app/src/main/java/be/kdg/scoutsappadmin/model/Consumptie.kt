package be.kdg.scoutsappadmin.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class Consumptie(
    public var key: String?,
    public var consumptieGegevenDoor: String?,
     var consumptieGegevenDoorId: String,
    public var timeStamp: Long
) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    private var consumptieId = 0

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "key" to key,
            "consumptieGegevenDoor" to consumptieGegevenDoor,
            "consumptieGegevenDoorId" to consumptieGegevenDoorId,
            "timeStamp" to timeStamp
        )
    }

    override fun toString(): String {
        return "Consumptie(key=$key, consumptieGegevenDoor=$consumptieGegevenDoor, consumptieGegevenDoorId=$consumptieGegevenDoorId, timeStamp=$timeStamp, consumptieId=$consumptieId)"
    }


}