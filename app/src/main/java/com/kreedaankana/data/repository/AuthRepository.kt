package com.kreedaankana.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.kreedaankana.data.local.dao.UserDao
import com.kreedaankana.data.local.entity.UserEntity
import com.kreedaankana.util.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore
) {
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val isUserLoggedIn: Boolean
        get() = currentUser != null

    fun getAuthState(): Flow<Resource<FirebaseUser?>> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(Resource.Success(firebaseAuth.currentUser))
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun signInWithEmail(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                saveUserToLocal(user.uid, email, "customer")
                Resource.Success(user)
            } ?: Resource.Error("Login failed")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed")
        }
    }

    suspend fun signUpWithEmail(email: String, password: String, name: String, role: String): Resource<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                saveUserToLocal(user.uid, email, role, name)
                saveUserToFirestore(user.uid, email, name, role)
                Resource.Success(user)
            } ?: Resource.Error("Signup failed")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Signup failed")
        }
    }

    suspend fun signInWithGoogle(idToken: String): Resource<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            result.user?.let { user ->
                saveUserToLocal(user.uid, user.email ?: "", "customer")
                Resource.Success(user)
            } ?: Resource.Error("Google sign-in failed")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Google sign-in failed")
        }
    }

    fun signOut() {
        auth.signOut()
    }

    private suspend fun saveUserToLocal(userId: String, email: String, role: String, name: String = "") {
        val user = UserEntity(
            userId = userId,
            name = name.ifEmpty { email.substringBefore("@") },
            email = email,
            role = role
        )
        userDao.insert(user)
    }

    private suspend fun saveUserToFirestore(userId: String, email: String, name: String, role: String) {
        val userMap = hashMapOf(
            "userId" to userId,
            "email" to email,
            "name" to name,
            "role" to role,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("users").document(userId).set(userMap).await()
    }
}