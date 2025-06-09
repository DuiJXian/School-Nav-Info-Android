package com.xz.schoolnavinfo.presentation.campus.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.common.utils.JsonUtils
import com.xz.schoolnavinfo.data.dao.remote.request.ArticleRequest
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.domain.use_case.ArticleUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActivityUiState(
    val activities: List<ArticleDTO> = emptyList(),
    val banners: List<ArticleDTO> = emptyList(),
    val isRefreshing: Boolean = false
)

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val articleUseCases: ArticleUseCases,
    private val netExceptionManager: NetExceptionManager
) : ViewModel() {


    private val _uiState = MutableStateFlow(ActivityUiState())
    val uiState: StateFlow<ActivityUiState> = _uiState.asStateFlow()

    private var activityRequest = ArticleRequest()

    private var hasMore = true
    private val addedArticleIds = mutableSetOf<String>()

    init {
        refreshData()
    }

    fun refreshData() {
        _uiState.update {
            it.copy(
                activities = emptyList(),
                banners = emptyList(),
                isRefreshing = true
            )
        }
        firstGetActivityArticles()
        getBanner()
    }

    private fun firstGetActivityArticles() {
        _uiState.update { it.copy(activities = emptyList()) }
        activityRequest.pageNum = 1
        addedArticleIds.clear()
        getActivityArticles()
    }

    fun getMoreActivityArticles() {
        if (hasMore) {
            activityRequest.pageNum++
        }
        getActivityArticles()
    }

    private fun getBanner() {
        viewModelScope.launch {
            netExceptionManager.safeApiCall {
                val resp = articleUseCases.getActivityBanner()
                if (resp.code == "success") {
                    _uiState.update { it.copy(banners = resp.data) }
                }
            }

        }
    }

    private fun getActivityArticles() {
        viewModelScope.launch {
            netExceptionManager.safeApiCall {
                val respData = articleUseCases.getActivityArticleList(activityRequest).data

                val addArticles: MutableList<ArticleDTO> = mutableListOf()
                respData.list.map {
                    if (!addedArticleIds.contains(it.article?.id) && it.article?.id != null) {
                        addArticles.add(it)
                        addedArticleIds.add(it.article.id)
                    }
                }

                _uiState.update {
                    it.copy(
                        activities = uiState.value.activities + addArticles,
                        isRefreshing = false
                    )
                }

                hasMore = (respData.pageNum * respData.pageSize < respData.total)
            }
        }
    }
}