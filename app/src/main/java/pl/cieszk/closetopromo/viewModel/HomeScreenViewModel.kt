package pl.cieszk.closetopromo.viewModel

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pl.cieszk.closetopromo.data.model.Discount
import pl.cieszk.closetopromo.data.repository.FirestoreRepository
import pl.cieszk.closetopromo.receiver.GeofenceBroadcastReceiver
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    @ApplicationContext private val appContext: Context
) :
    ViewModel() {
    private val _discounts = MutableLiveData<List<Discount>>()
    val discounts: LiveData<List<Discount>> = _discounts
    private val geofencingClient: GeofencingClient =
        LocationServices.getGeofencingClient(appContext)
    private val geofenceList = mutableListOf<Geofence>()

    init {
        loadDiscounts()
        setupGeofences()
    }

    private fun loadDiscounts() {
        viewModelScope.launch {
            try {
                val result = firestoreRepository.getAllDiscounts("discount").await()

                val discountList = result.documents.mapNotNull { it.toObject<Discount>() }
                _discounts.value = discountList
            } catch (e: Exception) {
                Log.e("Discount", "Error while loading discount list: ${e.printStackTrace()}")
            }
        }
    }

    fun addDiscount(discount: Discount) {
        viewModelScope.launch {
            try {
                firestoreRepository.addDiscount("discount", discount)
            } catch (e: Exception) {
                Log.e("Discount", "Error while trying to add new discount: ${e.printStackTrace()}")
            }

        }
    }

    private fun createGeofence(discount: Discount, geofenceRadius: Float): Geofence {
        return Geofence.Builder()
            .setRequestId(discount.id!!)
            .setCircularRegion(
                discount.geolocation.latitude,
                discount.geolocation.longitude,
                geofenceRadius
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()
    }

    private fun createGeofencePendingIntent(): PendingIntent {
        val intent = Intent(appContext, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    @SuppressLint("MissingPermission")
    private fun addGeofences() {
        if (_discounts.value == null) return

        for (discount in _discounts.value!!) {
            val geofence = createGeofence(discount, 1000f)
            geofenceList.add(geofence)
        }

        if (geofenceList.isNotEmpty()) {
            val geofencingRequest = GeofencingRequest.Builder()
                .addGeofences(geofenceList)
                .build()

            geofencingClient.addGeofences(geofencingRequest, createGeofencePendingIntent())
                .addOnSuccessListener {
                    Log.d("Geofencing", "Geofences added")
                }
                .addOnFailureListener {
                    Log.e("Geofencing", "Failed to add geofences")
                }
        }
    }

    private fun setupGeofences() {
        viewModelScope.launch {
            addGeofences()
        }
    }
}