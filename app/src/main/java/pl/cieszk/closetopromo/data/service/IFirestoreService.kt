package pl.cieszk.closetopromo.data.service

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import pl.cieszk.closetopromo.data.model.Discount

interface IFirestoreService {
    fun getDiscount(documentPath: String): Task<DocumentSnapshot>
    fun getAllDiscounts(collectionPath: String): Task<QuerySnapshot>
    fun addDiscount(collectionPath: String, discount: Discount): Task<DocumentReference>
    fun updateDiscount(documentPath: String, discount: Discount): Task<Void>
    fun deleteDiscount(documentPath: String): Task<Void>

}