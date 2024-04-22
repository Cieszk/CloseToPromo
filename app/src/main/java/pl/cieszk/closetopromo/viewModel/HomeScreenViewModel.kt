package pl.cieszk.closetopromo.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
class HomeScreenViewModel @Inject constructor(private val firestoreRepository: FirestoreRepository) :
    ViewModel() {
    private val _discounts = MutableLiveData<List<Discount>>()
    val discounts: LiveData<List<Discount>> = _discounts

    init {
        loadDiscounts()
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
}