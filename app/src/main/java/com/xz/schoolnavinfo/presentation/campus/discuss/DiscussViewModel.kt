package com.xz.schoolnavinfo.presentation.campus.discuss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.data.dao.remote.request.ArticleRequest
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.domain.use_case.ArticleUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DiscussViewModel @Inject constructor(
    private val articleUseCases: ArticleUseCases,
    private val networkErrorManager: NetExceptionManager,
) : ViewModel() {

    private var _discussArticles = MutableStateFlow(emptyList<ArticleDTO>())
    val discussArticles = _discussArticles.asStateFlow()

    private var requestParameter = ArticleRequest()
    private var hasMore = true
    private val addedArticleIds = mutableSetOf<String>()

    private var job: Job? = null;

    init {
        refreshData()
    }

    fun searchDiscussArticles(text: String) {
        job?.cancel()
        job = viewModelScope.launch {
            delay(200)
            networkErrorManager.safeApiCall {
                val resp = articleUseCases.searchActivityArticleList(text)
                if (resp.code == "success") {
                    _discussArticles.update { resp.data }
                }
            }
        }
    }

    fun refreshData() {
        requestParameter.pageNum = 1
        addedArticleIds.clear()
        _discussArticles.update { emptyList() }
        getDiscussArticles(requestParameter)
    }

    fun getMoreDiscussArticles() {
        if (hasMore) {
            requestParameter.pageNum++
            getDiscussArticles(requestParameter)
        }
    }

    private fun getDiscussArticles(request: ArticleRequest) {
        viewModelScope.launch {
            networkErrorManager.safeApiCall {
                val respData = articleUseCases.getDiscussArticleList(request).data
                val addArticles: MutableList<ArticleDTO> = mutableListOf()
                respData.list.map {
                    if (!addedArticleIds.contains(it.article?.id) && it.article?.id != null) {
                        addArticles.add(it)
                        addedArticleIds.add(it.article.id)
                    }
                }
                _discussArticles.update { it + addArticles }
                hasMore = respData.pageNum * respData.pageSize < respData.total
            }
        }
    }
}