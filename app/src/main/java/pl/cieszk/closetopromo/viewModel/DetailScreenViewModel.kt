package pl.cieszk.closetopromo.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pl.cieszk.closetopromo.data.model.Discount
import pl.cieszk.closetopromo.data.repository.FirestoreRepository
import javax.inject.Inject

@HiltViewModel
class DetailScreenViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _discount = MutableLiveData<Discount>()
    val discount: LiveData<Discount> = _discount

    init {
        val discountId = savedStateHandle.get<String>("discountId")
        discountId?.let {
            loadDiscount(it)
        }
    }

    private fun loadDiscount(discountId: String) {
        viewModelScope.launch {
            try {
                val result = firestoreRepository.getDiscount("discount/${discountId}").await()
                val discount = result.toObject<Discount>()
                discount?.let {
                    _discount.value = it
                }
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Error loading discount", e)
            }
        }
    }

    fun deleteDiscount(discountId: String) {
        viewModelScope.launch {
            try {
                val result = firestoreRepository.deleteDiscount(discountId)
                val success = result.isSuccessful
                Log.d("DetailViewModel", "Successfully deleted a discount")
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Error while deleting discount", e)
            }
        }
    }
}