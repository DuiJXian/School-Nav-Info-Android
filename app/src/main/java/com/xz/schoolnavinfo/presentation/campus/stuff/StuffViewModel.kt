package com.xz.schoolnavinfo.presentation.campus.stuff

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.domain.data.dto.StuffDTO
import com.xz.schoolnavinfo.domain.use_case.StuffUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StuffViewModel @Inject constructor(
    private val stuffUseCases: StuffUseCases,
    private val netExceptionManager: NetExceptionManager,
) : ViewModel() {

    private val _stuffs = MutableStateFlow<List<StuffDTO>>(emptyList())
    val stuffs:MutableStateFlow<List<StuffDTO>> = _stuffs

    init {
        getStuff()
    }

    fun searchStuff(text:String){
        viewModelScope.launch {
            netExceptionManager.safeApiCall {
                val resp = stuffUseCases.searchStuffList(text)
                if (resp.code == "success") {
                    _stuffs.update { resp.data }
                }
            }
        }
    }

    fun getStuff() {
        viewModelScope.launch {
            netExceptionManager.safeApiCall {
                val resp = stuffUseCases.getStuffList()
                if (resp.code == "success") {
                    _stuffs.update { resp.data }
                }
            }
        }
    }
}