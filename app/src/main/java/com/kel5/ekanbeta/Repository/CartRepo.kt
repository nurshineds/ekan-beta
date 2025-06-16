package com.kel5.ekanbeta.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.kel5.ekanbeta.Data.ProductData
import kotlinx.coroutines.tasks.await

class CartRepo {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun addToCart(productId: String) : Boolean {
        val uid = auth.currentUser?.uid ?: return false
        val userDoc = firestore.collection("users").document(uid)

        return try{
            val snapshot = userDoc.get().await()
            val currentCart = snapshot.get("cartItems") as? Map<String, Long> ?: emptyMap()
            val currentQuantity = currentCart[productId] ?: 0
            val updatedQuantity = currentQuantity + 1
            val updatedCart = mapOf("cartItems.$productId" to updatedQuantity)

            userDoc.update(updatedCart).await()
            true
        } catch ( e : Exception ){
            false
        }
    }

    suspend fun removeFromCart(productId: String, removeAll: Boolean = false): Boolean{
        val uid = auth.currentUser?.uid ?: return false
        val userDoc = firestore.collection("users").document(uid)

        return try{
            val snapshot = userDoc.get().await()
            val currentCart = snapshot.get("cartItems") as? Map<String, Long> ?: emptyMap()
            val currentQuantity = currentCart[productId] ?: 0
            val updatedQuantity = currentQuantity - 1

            val updatedCart=
                if (updatedQuantity <= 0 || removeAll)
                    mapOf("cartItems.$productId" to FieldValue.delete())
                else
                    mapOf("cartItems.$productId" to updatedQuantity)

            userDoc.update(updatedCart).await()
            true
        } catch (e: Exception){
            false
        }
    }

    fun getProductById(productId: String, onResult: (ProductData?) -> Unit){
        firestore.collection("data").document("stock")
            .collection("products").document(productId)
            .get()
            .addOnSuccessListener { document ->
                val product = document.toObject(ProductData::class.java)
                onResult(product)
            }
            .addOnFailureListener { onResult(null) }
    }
}