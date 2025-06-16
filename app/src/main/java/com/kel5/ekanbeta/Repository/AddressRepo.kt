package com.kel5.ekanbeta.Repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kel5.ekanbeta.Data.AddressData

class AddressRepo {
    private val firestore = FirebaseFirestore.getInstance()

    fun getUserAddresses(
        userId: String,
        onComplete: (List<AddressData>) -> Unit,
        onError: (Exception) -> Unit
    ){
        firestore.collection("users")
            .document(userId)
            .collection("addresses")
            .get()
            .addOnSuccessListener { result ->
                val addresses = result.map { doc ->
                    doc.toObject(AddressData::class.java).copy(id = doc.id)
                }
                onComplete(addresses)
            }
            .addOnFailureListener { onError(it) }
    }

    fun addUserAddress(
        userId: String,
        address: AddressData,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ){
        firestore.collection("users")
            .document(userId)
            .collection("addresses")
            .add(address)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }
}