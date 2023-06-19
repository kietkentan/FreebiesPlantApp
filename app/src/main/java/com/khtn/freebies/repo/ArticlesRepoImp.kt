package com.khtn.freebies.repo

import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.gson.Gson
import com.khtn.freebies.di.ArticlesCollection
import com.khtn.freebies.di.BookMarkCollection
import com.khtn.freebies.di.FollowerCollection
import com.khtn.freebies.di.FollowingCollection
import com.khtn.freebies.di.UserCollection
import com.khtn.freebies.helper.FireStoreCollection
import com.khtn.freebies.helper.SharedPrefConstants
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Articles
import com.khtn.freebies.module.User

class ArticlesRepoImp(
    @ArticlesCollection private val articlesCollection: CollectionReference,
    @UserCollection private val userCollection: CollectionReference,
    @FollowingCollection private val followingCollection: CollectionReference,
    @FollowerCollection private val followerCollection: CollectionReference,
    @BookMarkCollection private val bookMarkCollection: CollectionReference,
    private val appPreferences: SharedPreferences,
    private val gson: Gson
): ArticlesRepo {
    override fun getSession(result: (User?) -> Unit) {
        val user_str = appPreferences.getString(SharedPrefConstants.USER_SESSION,null)
        if (user_str == null)
            result.invoke(null)
        else {
            val user = gson.fromJson(user_str, User::class.java)
            result.invoke(user)
        }
    }

    override fun getArticles(result: (UiState<MutableList<Articles>>) -> Unit) {
        articlesCollection
            .get()
            .addOnSuccessListener {
                val articlesList: MutableList<Articles> = arrayListOf()
                Log.i("TAG_U", "getArticles: ${it.documents}")
                for (document in it) {
                    val articles = document.toObject(Articles::class.java)
                    articlesList.add(articles)
                }
                result.invoke(UiState.Success(articlesList))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    override fun getUser(listUserStr: List<String>, result: (UiState<MutableList<User>>) -> Unit) {
        userCollection
            .whereIn(FireStoreCollection.ID, listUserStr)
            .get()
            .addOnSuccessListener {
                val list: MutableList<User> = mutableListOf()
                for (document in it) {
                    val user = document.toObject(User::class.java)
                    list.add(user)
                }
                result.invoke(UiState.Success(list))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun checkSingleFavoriteArticles(
        id: String,
        articlesId: String,
        result: (UiState<Boolean>) -> Unit
    ) {
        followerCollection
            .document(articlesId)
            .get()
            .addOnSuccessListener {
                val map = it.data
                if (map != null) {
                    val request = map[FireStoreCollection.FOLLOWER]
                    if (request != null) {
                        val list: List<String> = request as List<String>

                        for (pid in list)
                            if (pid == id) {
                                result.invoke(UiState.Success(true))
                                return@addOnSuccessListener
                            }
                    }
                }
                result.invoke(UiState.Success(false))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun checkMultiFavoriteArticles(
        id: String,
        articlesIdList: List<String>,
        result: (UiState<Map<String, Boolean>>) -> Unit
    ) {
        followingCollection
            .document(id)
            .get()
            .addOnSuccessListener {
                val request = it.data
                val map: HashMap<String, Boolean> = hashMapOf()

                for (articlesId in articlesIdList)
                    map[articlesId] = false

                if (request != null) {
                    val list: MutableList<String> = (request[FireStoreCollection.ARTICLE] ?: mutableListOf<String>()) as MutableList<String>

                    for (aId in list)
                        if (map.containsKey(aId))
                            map[aId] = true
                }
                result.invoke(UiState.Success(map))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun checkMultiBookMarkArticles(
        id: String,
        articlesIdList: List<String>,
        result: (UiState<Map<String, Boolean>>) -> Unit
    ) {
        bookMarkCollection
            .document(id)
            .get()
            .addOnSuccessListener {
                val request = it.data
                val map: HashMap<String, Boolean> = hashMapOf()

                for (articlesId in articlesIdList)
                    map[articlesId] = false

                if (request != null) {
                    val list: MutableList<String> = (request[FireStoreCollection.ARTICLE] ?: mutableListOf<String>()) as MutableList<String>

                    for (aId in list)
                        if (map.containsKey(aId))
                            map[aId] = true
                }
                result.invoke(UiState.Success(map))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }
}