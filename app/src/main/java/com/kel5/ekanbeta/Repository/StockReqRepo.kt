package com.kel5.ekanbeta.Repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class StockReqRepo() {
    private val firestore = FirebaseFirestore.getInstance()
    val docRef = firestore.collection("admins").document("requests").collection("cekstok")

    suspend fun sendStockRequest(userId: String){
        val data = hashMapOf(
            "uid" to userId,
            "status" to "pending"
        )
        docRef.document(userId).set(data).await()
    }

    suspend fun cancelStockRequest(userId: String){
        docRef.document(userId).delete().await()
    }

    suspend fun confirmStockRequest(userId: String){
        docRef.document(userId).update("status", "confirmed").await()
    }

    suspend fun getStockRequestStatus(userId: String): String? {
        val doc = docRef.document(userId).get().await()
        return doc.getString("status")
    }

    suspend fun getAllStockRequest(): List<Map<String, Any>>{
        val snapshot = docRef.get().await()
        return snapshot.documents.mapNotNull { it.data }
    }
}