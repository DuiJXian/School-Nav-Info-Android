package com.xz.schoolnavinfo.presentation.campus.discuss

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xz.schoolnavinfo.common.net.AuthInterceptor
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.common.request.ArticleRequest
import com.xz.schoolnavinfo.domain.model.dto.ArticleDTO
import com.xz.schoolnavinfo.domain.use_case.ArticleUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscussViewModel @Inject constructor(
    private val articleUseCases: ArticleUseCases,
    private val authInterceptor: AuthInterceptor,
    private val networkErrorManager: NetExceptionManager,
) : ViewModel() {

    private val _articleInfo = mutableStateOf<List<ArticleDTO>>(emptyList())
    val articleInfo = _articleInfo

    init {
        viewModelScope.launch {
            authInterceptor.loadToken()
            networkErrorManager.safeApiCall {
                val response = articleUseCases.getArticles(ArticleRequest())
                _articleInfo.value = response.data.list
            }
        }
    }
}