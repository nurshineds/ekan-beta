package com.kel5.ekanbeta.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.kel5.ekanbeta.Data.OrderData
import com.kel5.ekanbeta.Data.ProductData
import com.kel5.ekanbeta.Data.ReviewData
import kotlinx.coroutines.tasks.await

class ReviewRepo {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    suspend fun getReviews(productId: String): List<ReviewData>{
        val snapshot = firestore.collection("data")
            .document("stock")
            .collection("products")
            .document(productId)
            .collection("reviews")
            .get()
            .await()

        return snapshot.toObjects(ReviewData::class.java)
    }

    suspend fun getOrderById(uid: String, orderId: String): OrderData {
        val snapshot = firestore.collection("users")
            .document(uid)
            .collection("orders")
            .document(orderId)
            .get()
            .await()

        return snapshot.toObject(OrderData::class.java) ?: OrderData()
    }

    suspend fun getProductsByIds(ids: List<String>): List<ProductData>{
        if(ids.isEmpty()) return emptyList()

        val snapshot = firestore.collection("data")
            .document("stock")
            .collection("products")
            .whereIn("id", ids)
            .get()
            .await()

        return snapshot.toObjects(ProductData::class.java)
    }

    suspend fun submitReview(
        productId: String,
        rating: Float,
        comment: String,
        orderId: String
    ){
        val uid = auth.currentUser?.uid ?:return
        val userDoc = firestore.collection("users").document(uid).get().await()
        val username = userDoc.getString("username") ?: ""
        val review = ReviewData(
            uid = uid,
            rating = rating,
            comment = comment,
            timestamp = null,
            username = username
        )

        firestore.collection("data")
            .document("stock")
            .collection("products")
            .document(productId)
            .collection("reviews")
            .add(
                mapOf(
                    "uid" to review.uid,
                    "rating" to review.rating,
                    "comment" to review.comment,
                    "username" to review.username,
                    "timestamp" to FieldValue.serverTimestamp()
                )
            ).await()

        updateProductReviewSummary(productId)

        val globalOrderRef = firestore.collection("orders").document(orderId)
        val userOrderRef = firestore.collection("users").document(uid).collection("orders").document(orderId)

        firestore.runBatch { batch ->
            batch.update(globalOrderRef, "status", "reviewed")
            batch.update(userOrderRef, "status", "reviewed")
        }.await()
    }

    suspend fun updateProductReviewSummary(productId: String) {
        val reviewsSnapshot = firestore.collection("data")
            .document("stock")
            .collection("products")
            .document(productId)
            .collection("reviews")
            .get()
            .await()

        val reviewCount = reviewsSnapshot.size()
        if (reviewCount == 0) {
            firestore.collection("data")
                .document("stock")
                .collection("products")
                .document(productId)
                .update(
                    mapOf(
                        "rating" to 0.0,
                        "reviewCount" to 0
                    )
                ).await()
            return
        }

        var totalRating = 0.0
        for (doc in reviewsSnapshot.documents) {
            val rating = doc.getDouble("rating") ?: 0.0
            totalRating += rating
        }

        val avgRating = totalRating / reviewCount

        firestore.collection("data")
            .document("stock")
            .collection("products")
            .document(productId)
            .update(
                mapOf(
                    "rating" to avgRating,
                    "reviewCount" to reviewCount
                )
            ).await()
    }
}