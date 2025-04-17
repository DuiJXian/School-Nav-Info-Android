package com.xz.schoolnavinfo.presentation.campus.stuff

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.domain.data.dto.StuffDTO
import com.xz.schoolnavinfo.domain.use_case.StuffUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StuffViewModel @Inject constructor(
    private val stuffUseCases: StuffUseCases,
    private val netExceptionManager: NetExceptionManager,
) : ViewModel() {

    private val _stuffList = mutableStateOf<List<StuffDTO>>(emptyList())
    val stuffList get() = _stuffList.value

    init {
        getStuff()
    }

     fun getStuff() {
        viewModelScope.launch {
            netExceptionManager.safeApiCall {
                val resp = stuffUseCases.getStuffList()
                if (resp.code == "success") {
                    _stuffList.value = resp.data
                }
            }
        }
    }
}