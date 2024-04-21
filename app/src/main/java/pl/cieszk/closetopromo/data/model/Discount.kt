package pl.cieszk.closetopromo.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Discount(
    @DocumentId
    val id: String? = null,
    val title: String,
    val shop: String,
    val description: String,
    val discountAmount: Int,
    val dateFrom: Timestamp,
    val dateTo: Timestamp,
    val geolocation: GeoPoint,
    val picture: String
)
