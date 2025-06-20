package com.kel5.ekanbeta.Repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.kel5.ekanbeta.Data.UserData
import kotlinx.coroutines.tasks.await

class AuthRepo(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
){
    suspend fun isUsernameTaken(username : String) : Boolean{
        return try{
            val result = firestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .await()
            !result.isEmpty
        } catch (e: Exception){
            false
        }
    }

    suspend fun isEmailTaken(email : String) : Boolean{
        return try{
            val result = firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()
            !result.isEmpty
        } catch (e: Exception){
            false
        }
    }

    suspend fun registerUser(
        username : String,
        email : String,
        password : String
    ) : Pair<Boolean, String?>{
        try{
            if(isUsernameTaken(username)){
                return Pair(false, "Username sudah digunakan")
            }

            if(isEmailTaken(email)){
                return Pair(false, "Email sudah digunakan")
            }

            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user : FirebaseUser? = authResult.user

            user?.uid?.let{ uid ->
                val createdAt = Timestamp.now()
                val user = UserData(uid, username, email, role = "user", createdAt)
                firestore.collection("users").document(uid).set(user).await()
                return Pair(true, null)
            } ?: return Pair(false, "Gagal mendapatkan UID")
        } catch (e: FirebaseAuthUserCollisionException){
            return Pair(false, "Email sudah digunakan")
        } catch (e: Exception){
            return Pair(false, e.message)
        }
    }

    suspend fun loginUser(email: String, password: String): Pair<Boolean, String?> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Pair(true, null)
        } catch (e: FirebaseAuthInvalidUserException) {
            Pair(false, "Email tidak terdaftar")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Pair(false, "Email atau password salah")
        } catch (e: Exception) {
            Pair(false, e.message)
        }
    }

    suspend fun getUserRole(): String? {
        return try {
            val uid = auth.currentUser?.uid ?: return null
            val doc = firestore.collection("users").document(uid).get().await()
            doc.getString("role")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserByIdWithRole(role: String): UserData? {
        return try {
            val result = firestore.collection("users")
                .whereEqualTo("role", role)
                .limit(1)
                .get()
                .await()
            result.documents.firstOrNull()?.toObject(UserData::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getCurrentUser(): UserData?{
        val uid = auth.currentUser?.uid ?: return null
        val snapshot = firestore.collection("users").document(uid).get().await()
        return snapshot.toObject(UserData::class.java)
    }

    fun generateUserId(): String?{
        return auth.currentUser?.uid
    }

    fun _getCurrentUser(){
        auth.currentUser
    }

    suspend fun getCurrentUserData(): Map<String, Any?>? {
        return try{
            val uid = auth.currentUser?.uid ?: return null
            val doc = firestore.collection("users").document(uid).get().await()
            if(doc.exists()) doc.data else null
        } catch (e: Exception) {
            null
        }
    }

    fun logout(){
        auth.signOut()
    }

    fun getUserListener(onUserChange: (UserData?) -> Unit) : ListenerRegistration? {
        val uid = auth.currentUser?.uid ?: return null
        return firestore.collection("users")
            .document(uid)
            .addSnapshotListener { snapshot, error ->
                if(error != null){
                    Log.e("FIREBASE", "Error in snapshot: ${error.message}")
                    onUserChange(null)
                    return@addSnapshotListener
                }
                val user = snapshot?.toObject(UserData::class.java)
                Log.d("FIREBASE", "Fetched user: $user")
                onUserChange(user)
            }
    }

    suspend fun getUserById(userId: String): UserData? {
        val snapshot = firestore.collection("users")
            .document(userId)
            .get()
            .await()

        return if (snapshot.exists()) {
            snapshot.toObject(UserData::class.java)
        } else {
            null
        }
    }
}