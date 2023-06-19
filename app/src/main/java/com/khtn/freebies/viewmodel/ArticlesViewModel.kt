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
    private val _getArticles = MutableLiveData<UiState<List<Articles>>>()
    val getArticles: LiveData<UiState<List<Articles>>>
        get() = _getArticles

    private val _getUser = MutableLiveData<UiState<List<User>>>()
    val getUser: LiveData<UiState<List<User>>>
        get() = _getUser

    private val _checkMultiFavorite = MutableLiveData<UiState<Map<String, Boolean>>>()
    val checkMultiFavorite: LiveData<UiState<Map<String, Boolean>>>
        get() = _checkMultiFavorite

    private val _checkMultiBookMark = MutableLiveData<UiState<Map<String, Boolean>>>()
    val checkMultiBookMark: LiveData<UiState<Map<String, Boolean>>>
        get() = _checkMultiBookMark

    fun getSession(result: (User?) -> Unit){
        articlesRepo.getSession(result)
    }

    fun getArticles() {
        _getArticles.value = UiState.Loading
        articlesRepo.getArticles {
            _getArticles.value = it
        }
    }

    fun getUser(listUserStr: List<String>) {
        _getUser.value = UiState.Loading
        articlesRepo.getUser(listUserStr) {
            _getUser.value = it
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
}