package com.xz.schoolnavinfo.presentation.campus.discuss

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.data.dao.remote.request.ArticleRequest
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.domain.use_case.ArticleUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscussViewModel @Inject constructor(
    private val articleUseCases: ArticleUseCases,
    private val networkErrorManager: NetExceptionManager,
) : ViewModel() {

    private var _discussList = mutableStateListOf<ArticleDTO>()
    val discussList = _discussList

    private var discussRequest = ArticleRequest()
    var hasMore = true
    private val addedArticleIds = mutableSetOf<String>()

//    private val _searchText = mutableStateOf("")
//    val searchText = _searchText.value

    init {
        getArticles(discussRequest)
    }

    fun onSearchArticle(text: String) {
        viewModelScope.launch {
            networkErrorManager.safeApiCall {
                val resp = articleUseCases.searchActivityArticleList(text)
                if (resp.code == "success") {
                    _discussList.clear()
                    _discussList.addAll(resp.data)
                }
            }
        }
    }

    fun onGetArticlesEvent() {
        discussRequest.pageNum = 1
        _discussList.clear()
        addedArticleIds.clear()
        getArticles(discussRequest)
    }

    fun onGetMoreArticlesEvent() {
        if (hasMore) {
            discussRequest.pageNum++
            getArticles(discussRequest)
        }
    }

    private fun getArticles(request: ArticleRequest) {
        viewModelScope.launch {
            networkErrorManager.safeApiCall {
                val data = articleUseCases.getDiscussArticleList(request).data
                val newDataList =
                    if (request.pageNum == 1) data.list.reversed() else data.list //始终将最新数据放到前面
                for (item in newDataList) {
                    if ((item.article?.id?.isNotBlank() == true) && addedArticleIds.add(item.article.id)) {
                        if (data.pageNum == 1) {
                            _discussList.add(0, item) // 刷新时，直接将新的数据添加到头部
                        } else {
                            _discussList.add(item) // 添加到尾部
                        }
                    }
                }
                if (data.pageNum * data.pageSize >= data.total) {
                    hasMore = false
                }
            }
        }
    }
}