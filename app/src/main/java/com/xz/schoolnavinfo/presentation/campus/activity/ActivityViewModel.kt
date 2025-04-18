package com.xz.schoolnavinfo.presentation.campus.activity

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
class ActivityViewModel @Inject constructor(
    private val articleUseCases: ArticleUseCases,
    private val netExceptionManager: NetExceptionManager
) : ViewModel() {


    private val _activityList = mutableStateListOf<ArticleDTO>()
    val activityList = _activityList

    private val _bannerList = mutableStateListOf<ArticleDTO>()
    val bannerList = _bannerList

    private var discussRequest = ArticleRequest()
    var hasMore = true
    private val addedArticleIds = mutableSetOf<String>()

    init {
        getActivity(discussRequest)
        getBanner()
    }

    fun onGetBannerEvent(){
        getBanner()
    }

    fun onGetActivityEvent() {
        discussRequest.pageNum = 1
        addedArticleIds.clear()
        _bannerList.clear()
        getActivity(discussRequest)
    }

    fun onGetMoreActivityEvent() {
        if (hasMore) {
            discussRequest.pageNum++
            getActivity(discussRequest)
        }
    }

    private fun getBanner() {
        viewModelScope.launch {
            netExceptionManager.safeApiCall {
                val resp = articleUseCases.getActivityBanner()
                if (resp.code == "success") {
                    _bannerList.clear()
                    _bannerList.addAll(resp.data)
                }
            }

        }
    }

    private fun getActivity(request: ArticleRequest) {
        viewModelScope.launch {
            netExceptionManager.safeApiCall {
                val data = articleUseCases.getActivityArticleList(request).data
                val newDataList =
                    if (request.pageNum == 1) data.list.reversed() else data.list //始终将最新数据放到前面
                for (item in newDataList) {
                    if ((item.article?.id?.isNotBlank() == true) && addedArticleIds.add(item.article.id)) {
                        if (data.pageNum == 1) {
                            _activityList.add(0, item) // 刷新时，直接将新的数据添加到头部
                        } else {
                            _activityList.add(item) // 添加到尾部
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