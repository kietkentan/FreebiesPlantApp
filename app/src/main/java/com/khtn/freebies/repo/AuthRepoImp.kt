package com.khtn.freebies.repo

import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.khtn.freebies.helper.FireStoreCollection
import com.khtn.freebies.helper.SharedPrefConstants
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.User

class AuthRepoImp (
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val appPreferences: SharedPreferences,
    private val gson: Gson
) : AuthRepo {
    override fun registerUser(
        password: String,
        user: User,
        result: (UiState<String>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    user.id = it.result.user?.uid ?: ""
                    Log.i("TAG_U", "registerUser: ${user.id}")
                    updateUserInfo(user) { state ->
                        if (state is UiState.Success) {
                            storeSession(id = it.result.user?.uid ?: "") { it ->
                                if (it == null)
                                    result.invoke(UiState.Failure("User register successfully but session failed to store"))
                                else
                                    result.invoke(UiState.Success("User register successfully!"))
                            }
                        }
                        else if (state is UiState.Failure) result.invoke(UiState.Failure(state.error))
                    }
                } else {
                    try {
                        throw it.exception ?: java.lang.Exception("Invalid authentication")
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        result.invoke(UiState.Failure("Authentication failed, Password should be at least 11 characters"))
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        result.invoke(UiState.Failure("Authentication failed, Invalid email entered"))
                    } catch (e: FirebaseAuthUserCollisionException) {
                        result.invoke(UiState.Failure("Authentication failed, Email already registered."))
                    } catch (e: Exception) {
                        result.invoke(UiState.Failure(e.message))
                    }
                }
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    override fun updateUserInfo(user: User, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.USER).document(user.id)
        document
            .set(user)
            .addOnSuccessListener {
                result.invoke(UiState.Success("User has been update successfully"))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    override fun loginUser(email: String, password: String, result: (UiState<String>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storeSession(id = task.result.user?.uid ?: "") {
                        if (it == null)
                            result.invoke(UiState.Failure("Failed to store local session"))
                        else
                            result.invoke(UiState.Success("Login successfully!"))
                    }
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failure("Authentication failed, Check email and password"))
            }
    }

    override fun forgotPassword(email: String, result: (UiState<String>) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    result.invoke(UiState.Success("Email has been sent"))
                else
                    result.invoke(UiState.Failure(task.exception?.message))
            }.addOnFailureListener {
                result.invoke(UiState.Failure("Authentication failed, Check email"))
            }
    }

    override fun logout(result: () -> Unit) {
        auth.signOut()
        appPreferences.edit().putString(SharedPrefConstants.USER_SESSION, null).apply()
        result.invoke()
    }

    override fun storeSession(id: String, result: (User?) -> Unit) {
        database.collection(FireStoreCollection.USER).document(id)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val user = it.result.toObject(User::class.java)
                    appPreferences.edit().putString(SharedPrefConstants.USER_SESSION, gson.toJson(user)).apply()
                    result.invoke(user)
                } else
                    result.invoke(null)
            }
            .addOnFailureListener {
                result.invoke(null)
            }
    }

    override fun getSession(result: (User?) -> Unit) {
        val user_str = appPreferences.getString(SharedPrefConstants.USER_SESSION,null)
        if (user_str == null)
            result.invoke(null)
        else {
            val user = gson.fromJson(user_str, User::class.java)
            result.invoke(user)
        }
    }
}
