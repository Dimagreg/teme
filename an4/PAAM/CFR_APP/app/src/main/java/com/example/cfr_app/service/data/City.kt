import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class City(
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) : Parcelable {
    // Empty constructor for Firestore
    constructor() : this("", 0.0, 0.0)
}