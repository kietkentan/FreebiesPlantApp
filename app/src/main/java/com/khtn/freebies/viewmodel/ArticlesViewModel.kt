package com.khtn.freebies.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Articles
import com.khtn.freebies.module.User
import com.khtn.freebies.repo.ArticlesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val articlesRepo: ArticlesRepo
): ViewModel() {
    private val _articles = MutableLiveData<UiState<Articles>>()
    val articles: LiveData<UiState<Articles>>
        get() = _articles

    private val _getArticles = MutableLiveData<UiState<List<Articles>>>()
    val getArticles: LiveData<UiState<List<Articles>>>
        get() = _getArticles

    private val _getUser = MutableLiveData<UiState<User?>>()
    val getUser: LiveData<UiState<User?>>
        get() = _getUser

    private val _getUsersList = MutableLiveData<UiState<List<User>>>()
    val getUsersList: LiveData<UiState<List<User>>>
        get() = _getUsersList

    private val _checkMultiFavorite = MutableLiveData<UiState<Map<String, Boolean>>>()
    val checkMultiFavorite: LiveData<UiState<Map<String, Boolean>>>
        get() = _checkMultiFavorite

    private val _checkMultiBookMark = MutableLiveData<UiState<Map<String, Boolean>>>()
    val checkMultiBookMark: LiveData<UiState<Map<String, Boolean>>>
        get() = _checkMultiBookMark

    private val _articlesLiked = MutableLiveData<UiState<List<Articles>>>()
    val articlesLiked: LiveData<UiState<List<Articles>>>
        get() = _articlesLiked

    private val _updateSingleFavorite = MutableLiveData<UiState<Pair<String, Boolean>>>()
    val updateSingleFavorite: LiveData<UiState<Pair<String, Boolean>>>
        get() = _updateSingleFavorite

    private val _updateSigleBookMark = MutableLiveData<UiState<Pair<String, Boolean>>>()
    val updateSigleBookMark: LiveData<UiState<Pair<String, Boolean>>>
        get() = _updateSigleBookMark

    fun getSession(result: (User?) -> Unit){
        articlesRepo.getSession(result)
    }

    fun getArticles() {
        _getArticles.value = UiState.Loading
        articlesRepo.getArticles {
            _getArticles.value = it
        }
    }

    fun getArticles(articlesId: String) {
        _articles.value = UiState.Loading
        articlesRepo.getSigleArticles(articlesId) {
            _articles.value = it
        }
    }

    fun getListArticlesLiked(id: String) {
        _articlesLiked.value = UiState.Loading
        articlesRepo.getListArticlesLiked(id) {
            _articlesLiked.value = it
        }
    }

    fun getSingleUser(userId: String) {
        _getUser.value = UiState.Loading
        articlesRepo.getSingleUser(userId) {
            _getUser.value = it
        }
    }

    fun getUser(listUserStr: List<String>) {
        _getUsersList.value = UiState.Loading
        articlesRepo.getUser(listUserStr) {
            _getUsersList.value = it
        }
    }

    fun checkSingleFavoriteArticles(
        id: String,
        articlesId: String
    ) {
        _updateSingleFavorite.value = UiState.Loading
        articlesRepo.checkSingleFavoriteArticles(
            id = id,
            articlesId = articlesId
        ) {
            _updateSingleFavorite.value = it
        }
    }
    fun checkMultiFavorite(id: String, listArticles: List<String>) {
        _checkMultiFavorite.value = UiState.Loading
        articlesRepo.checkMultiFavoriteArticles(id, listArticles) {
            _checkMultiFavorite.value = it
        }
    }

    fun checkMultiBookMark(id: String, listArticles: List<String>) {
        _checkMultiBookMark.value = UiState.Loading
        articlesRepo.checkMultiBookMarkArticles(id, listArticles) {
            _checkMultiBookMark.value = it
        }
    }

    fun addSingleFavoritePlant(
        id: String,
        articlesId: String
    ) {
        articlesRepo.addSingleFavoriteArticles(
            id = id,
            articlesId = articlesId
        ) {
            if (it is UiState.Success)
                _updateSingleFavorite.value = it
        }
    }

    fun removeSingleFavoriteArticles(
        id: String,
        articlesId: String
    ) {
        articlesRepo.removeSingleFavoriteArticles(
            id = id,
            articlesId = articlesId
        ) {
            if (it is UiState.Success)
                _updateSingleFavorite.value = it
        }
    }

    fun addSingleBookMarkArticles(
        id: String,
        articlesId: String
    ) {
        articlesRepo.addSingleBookMarkArticles(
            id = id,
            articlesId = articlesId
        ) {
            if (it is UiState.Success)
                _updateSigleBookMark.value = it
        }
    }

    fun removeSingleBookMarkArticles(
        id: String,
        articlesId: String
    ) {
        articlesRepo.removeSingleBookMarkArticles(
            id = id,
            articlesId = articlesId
        ) {
            if (it is UiState.Success)
                _updateSigleBookMark.value = it
        }
    }
}