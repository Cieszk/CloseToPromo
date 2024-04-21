package pl.cieszk.closetopromo.data.repository

import pl.cieszk.closetopromo.data.model.Discount
import pl.cieszk.closetopromo.data.service.IFirestoreService
import javax.inject.Inject

class FirestoreRepository @Inject constructor(private val firestoreService: IFirestoreService) {
    fun getDiscount(documentPath: String) = firestoreService.getDiscount(documentPath)
    fun addDiscount(collectionPath: String, discount: Discount) = firestoreService.addDiscount(collectionPath, discount)
    fun updateDiscount(documentPath: String, discount: Discount) = firestoreService.updateDiscount(documentPath, discount)
    fun deleteDiscount(documentPath: String) = firestoreService.deleteDiscount(documentPath)
}