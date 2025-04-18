package com.xz.schoolnavinfo.presentation.campus.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.domain.data.dto.CommentDTO
import com.xz.schoolnavinfo.domain.data.entity.Comment
import com.xz.schoolnavinfo.domain.data.type.ArticleType
import com.xz.schoolnavinfo.domain.use_case.ArticleUseCases
import com.xz.schoolnavinfo.domain.use_case.CommentUserCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val commentUserCases: CommentUserCases,
    private val articleUseCases: ArticleUseCases,
    private val netExceptionManager: NetExceptionManager
) : ViewModel() {

    private val _commentDTOList = MutableStateFlow(listOf<CommentDTO>())
    val commentDTOList: StateFlow<List<CommentDTO>> = _commentDTOList


    fun onGetComments(articleId: String) {
        viewModelScope.launch {
            getComments(articleId)
        }
    }

    private suspend fun getComments(articleId: String) {
        netExceptionManager.safeApiCall {
            val res = commentUserCases.getCommentById(articleId)
            if (res.code == "success") {
                _commentDTOList.value = res.data
            }
        }
    }

    fun onDeleteArticle(articleId: String, articleType: ArticleType) {
        viewModelScope.launch {
            netExceptionManager.safeApiCall {
                val resp = if (articleType == ArticleType.Activity) {
                    articleUseCases.deleteActivityArticle(articleId)
                } else {
                    articleUseCases.deleteDiscussArticle(articleId)
                }
                if (resp.code == "success") {

                }
            }
        }
    }

    fun onSendComments(articleId: String, content: String) {
        viewModelScope.launch {
            netExceptionManager.safeApiCall {
                val comment = Comment(articleId, content, null)
                val res = commentUserCases.createComment(comment)
                if (res.code == "success") {
                    getComments(articleId)
                }
            }
        }
    }
}