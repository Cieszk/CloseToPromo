package pl.cieszk.closetopromo.viewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.cieszk.closetopromo.data.repository.FirestoreRepository
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val firestoreRepository: FirestoreRepository) : ViewModel() {
}