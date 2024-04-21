package pl.cieszk.closetopromo.data.service

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import pl.cieszk.closetopromo.data.model.Discount

class FirestoreServiceImpl : IFirestoreService {
    private val db = FirebaseFirestore.getInstance()

    override fun getDiscount(documentPath: String): Task<DocumentSnapshot> {
        return db.document(documentPath).get()
    }

    override fun addDiscount(collectionPath: String, discount: Discount): Task<DocumentReference> {
        return db.collection(collectionPath).add(discount)
    }

    override fun updateDiscount(documentPath: String, discount: Discount): Task<Void> {
        return db.document(documentPath).set(discount)
    }

    override fun deleteDiscount(documentPath: String): Task<Void> {
        return db.document(documentPath).delete()
    }
}