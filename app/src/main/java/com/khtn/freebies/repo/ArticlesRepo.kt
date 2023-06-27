package com.khtn.freebies.repo

import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Articles
import com.khtn.freebies.module.User

interface ArticlesRepo {
    fun getSession(result: (User?) -> Unit)
    fun getArticles(result: (UiState<MutableList<Articles>>) -> Unit)
    fun getSigleArticles(articlesId: String, result: (UiState<Articles>) -> Unit)
    fun getListArticlesLiked(id: String, result: (UiState<List<Articles>>) -> Unit)
    fun getSingleUser(userId: String, result: (UiState<User?>) -> Unit)
    fun getUser(listUserStr: List<String>, result: (UiState<MutableList<User>>) -> Unit)
    fun checkSingleFavoriteArticles(id: String, articlesId: String, result: (UiState<Pair<String, Boolean>>) -> Unit)
    fun checkMultiFavoriteArticles(id: String, articlesIdList: List<String>, result: (UiState<Map<String, Boolean>>) -> Unit)
    fun checkMultiBookMarkArticles(id: String, articlesIdList: List<String>, result: (UiState<Map<String, Boolean>>) -> Unit)
    fun addSingleFavoriteArticles(id: String, articlesId: String, result: (UiState<Pair<String, Boolean>>) -> Unit)
    fun addSingleBookMarkArticles(id: String, articlesId: String, result: (UiState<Pair<String, Boolean>>) -> Unit)
    fun removeSingleFavoriteArticles(id: String, articlesId: String, result: (UiState<Pair<String, Boolean>>) -> Unit)
    fun removeSingleBookMarkArticles(id: String, articlesId: String, result: (UiState<Pair<String, Boolean>>) -> Unit)
    fun removeAllFavoriteArticles(id: String, result: (UiState<String>) -> Unit)
    fun removeAllBookMarkArticles(id: String, result: (UiState<Boolean>) -> Unit)
}