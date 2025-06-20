package com.kel5.ekanbeta.Repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kel5.ekanbeta.Data.CategoryData
import kotlinx.coroutines.tasks.await
import com.kel5.ekanbeta.Data.ProductData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ProductRepo {
    private val firestore = FirebaseFirestore.getInstance()
    val docRef = firestore.collection("data").document("stock").collection("products")
    val categoryRef = firestore.collection("data").document("stock").collection("categories")

    suspend fun getProducts(): List<ProductData>{
        return try{
            val snapshot = docRef.get().await()
            snapshot.documents.mapNotNull { it.toObject(ProductData::class.java) }
        } catch (e : Exception) {
            emptyList()
        }
    }

    suspend fun getProductById(productId: String): ProductData? {
        val detail = docRef.document(productId).get().await()
        return detail.toObject(ProductData::class.java)
    }

    suspend fun getProductByIds(ids: List<String>): List<ProductData>{
        if(ids.isEmpty()) return emptyList()
        val snapshot = firestore
            .collection("data")
            .document("stock")
            .collection("products")
            .whereIn("id", ids)
            .get()
            .await()
        return snapshot.toObjects(ProductData::class.java)
    }

    suspend fun getCategories(): List<CategoryData> {
        return try {
            val snapshot = categoryRef.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(CategoryData::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

}