package com.kel5.ekanbeta.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CartRepo {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: ""
    val userDoc = firestore.collection("users").document(uid)

    suspend fun addToCart(productId: String) : Boolean {
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
}