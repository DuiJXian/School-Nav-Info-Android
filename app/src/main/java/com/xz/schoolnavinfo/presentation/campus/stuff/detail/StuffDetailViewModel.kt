package com.xz.schoolnavinfo.presentation.campus.stuff.detail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xz.schoolnavinfo.common.flow.GlobalFlow
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.domain.data.dto.StuffDTO
import com.xz.schoolnavinfo.domain.use_case.StuffUseCases
import com.xz.schoolnavinfo.presentation.campus.CampusMenu
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StuffDetailViewModel @Inject constructor(
    private val stuffUseCases: StuffUseCases,
    private val netExceptionManager: NetExceptionManager,
    private val globalFlow: GlobalFlow,
) : ViewModel() {

    private val _stuffDTO = mutableStateOf<StuffDTO?>(null)
    val stuffDTO get() = _stuffDTO.value



    fun getStuffById(id: String){
        viewModelScope.launch {
            netExceptionManager.safeApiCall {
                val resp = stuffUseCases.getStuffById(id)
                if (resp.code == "success"){
                    _stuffDTO.value = resp.data
                }
            }
        }
    }

    fun updateStatus(id: String) {
        viewModelScope.launch {
            netExceptionManager.safeApiCall {
                val resp = stuffUseCases.updateStatus(id)
                if(resp.code == "success"){
                    getStuffById(id)
                }
            }
        }
    }

    fun deleteStuff(id: String) {
        viewModelScope.launch {
            netExceptionManager.safeApiCall {
                val resp = stuffUseCases.deleteById(id)
                if (resp.code == "success"){
                    globalFlow.onRefreshDataEvent(CampusMenu.Stuff)
                }
            }
        }
    }
}