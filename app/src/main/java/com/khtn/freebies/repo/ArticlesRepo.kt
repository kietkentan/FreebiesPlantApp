package com.khtn.freebies.repo

import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Articles
import com.khtn.freebies.module.User

interface ArticlesRepo {
    fun getSession(result: (User?) -> Unit)
    fun getArticles(result: (UiState<MutableList<Articles>>) -> Unit)
    fun getUser(listUserStr: List<String>, result: (UiState<MutableList<User>>) -> Unit)
    fun checkSingleFavoriteArticles(id: String, articlesId: String, result: (UiState<Boolean>) -> Unit)
    fun checkMultiFavoriteArticles(id: String, articlesIdList: List<String>, result: (UiState<Map<String, Boolean>>) -> Unit)
    fun checkMultiBookMarkArticles(id: String, articlesIdList: List<String>, result: (UiState<Map<String, Boolean>>) -> Unit)
}