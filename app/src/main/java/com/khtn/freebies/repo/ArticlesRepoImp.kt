package com.khtn.freebies.repo

import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.gson.Gson
import com.khtn.freebies.di.ArticlesCollection
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

    override fun getSigleArticles(
        articlesId: String,
        result: (UiState<Articles>) -> Unit
    ) {
        articlesCollection
            .document(articlesId)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val articles = it.result.toObject(Articles::class.java)!!
                    result.invoke(UiState.Success(articles))
                }
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getListArticlesLiked(
        id: String,
        result: (UiState<List<Articles>>) -> Unit
    ) {
        followingCollection
            .document(id)
            .get()
            .addOnSuccessListener {
                val request = it.data
                if (request != null) {
                    val list = (request[FireStoreCollection.ARTICLE] ?: listOf<String>()) as List<String>

                    if (list.isNotEmpty())
                        getArticles(list) { state ->
                            result.invoke(state)
                        }
                }
                result.invoke(UiState.Success(listOf()))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    private fun getArticles(listId: List<String>, result: (UiState<List<Articles>>) -> Unit) {
        articlesCollection
            .whereIn(FireStoreCollection.ID, listId)
            .get()
            .addOnSuccessListener {
                val list: MutableList<Articles> = mutableListOf()
                for (document in it) {
                    val articles: Articles = document.toObject(Articles::class.java)
                    list.add(articles)
                }
                result.invoke(UiState.Success(list))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    override fun getSingleUser(userId: String, result: (UiState<User?>) -> Unit) {
        userCollection
            .document(userId)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = it.result.toObject(User::class.java)!!
                    result.invoke(UiState.Success(user))
                }
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
        result: (UiState<Pair<String, Boolean>>) -> Unit
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
                                result.invoke(UiState.Success(Pair(articlesId, true)))
                                return@addOnSuccessListener
                            }
                    }
                }
                result.invoke(UiState.Success(Pair(articlesId, false)))
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
        followingCollection
            .document(id)
            .get()
            .addOnSuccessListener {
                val request = it.data
                val map: HashMap<String, Boolean> = hashMapOf()

                for (articlesId in articlesIdList)
                    map[articlesId] = false

                if (request != null) {
                    val list: MutableList<String> = (request[FireStoreCollection.BOOK_MARK] ?: mutableListOf<String>()) as MutableList<String>

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
    override fun addSingleFavoriteArticles(
        id: String,
        articlesId: String,
        result: (UiState<Pair<String, Boolean>>) -> Unit
    ) {
        followerCollection
            .document(articlesId)
            .get()
            .addOnSuccessListener {
                var request = it.data
                val listId = mutableListOf<String>()
                listId.add(id)

                if (request != null) {
                    val list: MutableList<String> = (request[FireStoreCollection.FOLLOWER] ?: mutableListOf<String>()) as MutableList<String>
                    listId.addAll(list)
                } else {
                    request = hashMapOf()
                    request[FireStoreCollection.ID] = articlesId
                }

                request[FireStoreCollection.FOLLOWER] = listId
                followerCollection.document(articlesId).set(request)
                    .addOnSuccessListener {
                        addAccountFavorite(id, articlesId) { state ->
                            result.invoke(state)
                        }
                    }
                    .addOnFailureListener { ex ->
                        result.invoke(UiState.Failure(ex.localizedMessage))
                    }
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun addAccountFavorite(
        id: String,
        articlesId: String,
        result: (UiState<Pair<String, Boolean>>) -> Unit
    ) {
        followingCollection
            .document(id)
            .get()
            .addOnSuccessListener {
                var request = it.data
                val listArticlesId = mutableListOf<String>()
                listArticlesId.add(articlesId)

                if (request != null) {
                    val list: MutableList<String> = (request[FireStoreCollection.ARTICLE] ?: mutableListOf<String>()) as MutableList<String>
                    listArticlesId.addAll(list)
                } else {
                    request = hashMapOf()
                    request[FireStoreCollection.ID] = id
                }

                request[FireStoreCollection.ARTICLE] = listArticlesId
                followingCollection.document(id).set(request)
                    .addOnSuccessListener {
                        result.invoke(UiState.Success(Pair(articlesId, true)))
                    }
                    .addOnFailureListener { ex ->
                        result.invoke(UiState.Failure(ex.localizedMessage))
                    }
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun addSingleBookMarkArticles(
        id: String,
        articlesId: String,
        result: (UiState<Pair<String, Boolean>>) -> Unit
    ) {
        followingCollection
            .document(id)
            .get()
            .addOnSuccessListener {
                var request = it.data
                val listId = mutableListOf<String>()
                listId.add(articlesId)

                if (request != null) {
                    val list: MutableList<String> = (request[FireStoreCollection.BOOK_MARK] ?: mutableListOf<String>()) as MutableList<String>
                    listId.addAll(list)
                } else {
                    request = hashMapOf()
                    request[FireStoreCollection.ID] = id
                }

                request[FireStoreCollection.BOOK_MARK] = listId
                followingCollection.document(id).set(request)
                    .addOnSuccessListener {
                        result.invoke(UiState.Success(Pair(articlesId, true)))
                    }
                    .addOnFailureListener { ex ->
                        result.invoke(UiState.Failure(ex.localizedMessage))
                    }
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun removeSingleFavoriteArticles(
        id: String,
        articlesId: String,
        result: (UiState<Pair<String, Boolean>>) -> Unit
    ) {
        followerCollection
            .document(articlesId)
            .get()
            .addOnSuccessListener {
                var request = it.data
                var listId = mutableListOf<String>()

                if (request != null) {
                    val list: MutableList<String> = (request[FireStoreCollection.FOLLOWER] ?: mutableListOf<String>()) as MutableList<String>
                    list.remove(id)
                    listId = list
                } else {
                    request = hashMapOf()
                    request[FireStoreCollection.ID] = articlesId
                }

                request[FireStoreCollection.FOLLOWER] = listId
                followerCollection.document(articlesId).set(request)
                    .addOnSuccessListener {
                        removeSigleAccountFavorite(id, articlesId) { state ->
                            result.invoke(state)
                        }
                    }
                    .addOnFailureListener { ex ->
                        result.invoke(UiState.Failure(ex.localizedMessage))
                    }
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun removeSigleAccountFavorite(
        id: String,
        articlesId: String,
        result: (UiState<Pair<String, Boolean>>) -> Unit
    ) {
        followingCollection
            .document(id)
            .get()
            .addOnSuccessListener {
                var request = it.data
                var listArticlesId = mutableListOf<String>()

                if (request != null) {
                    val list: MutableList<String> = (request[FireStoreCollection.ARTICLE] ?: mutableListOf<String>()) as MutableList<String>
                    list.remove(articlesId)
                    listArticlesId = list
                } else {
                    request = hashMapOf()
                    request[FireStoreCollection.ID] = id
                }

                request[FireStoreCollection.ARTICLE] = listArticlesId
                followingCollection.document(id).set(request)
                    .addOnSuccessListener {
                        result.invoke(UiState.Success(Pair(articlesId, false)))
                    }
                    .addOnFailureListener { ex ->
                        result.invoke(UiState.Failure(ex.localizedMessage))
                    }
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun removeSingleBookMarkArticles(
        id: String,
        articlesId: String,
        result: (UiState<Pair<String, Boolean>>) -> Unit
    ) {
        followingCollection
            .document(id)
            .get()
            .addOnSuccessListener {
                var request = it.data
                var listId = mutableListOf<String>()

                if (request != null) {
                    val list: MutableList<String> = (request[FireStoreCollection.BOOK_MARK] ?: mutableListOf<String>()) as MutableList<String>
                    if (!list.remove(articlesId))
                        return@addOnSuccessListener
                    listId = list
                } else {
                    request = hashMapOf()
                    request[FireStoreCollection.ID] = id
                }

                request[FireStoreCollection.BOOK_MARK] = listId
                followingCollection.document(id).set(request)
                    .addOnSuccessListener {
                        result.invoke(UiState.Success(Pair(articlesId, false)))
                    }
                    .addOnFailureListener { it1 ->
                        result.invoke(UiState.Failure(it1.localizedMessage))
                    }
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    override fun removeAllFavoriteArticles(
        id: String,
        result: (UiState<String>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun removeAllBookMarkArticles(
        id: String,
        result: (UiState<Boolean>) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}