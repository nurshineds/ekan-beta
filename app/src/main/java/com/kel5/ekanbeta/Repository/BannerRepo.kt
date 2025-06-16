package com.kel5.ekanbeta.Repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BannerRepo {
    private val firestore = FirebaseFirestore.getInstance()
    private val docRef = firestore.collection("data").document("banners")

    suspend fun getBanners(): List<String> {
        return try {
            val snapshot = docRef.get().await()
            snapshot.get("urls") as? List<String> ?: emptyList()
        } catch (e : Exception) {
            emptyList()
        }
    }
}