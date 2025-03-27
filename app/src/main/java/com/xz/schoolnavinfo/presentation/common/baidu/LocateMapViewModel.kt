package com.xz.schoolnavinfo.presentation.common.baidu

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class LocationMapViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application), SensorEventListener {
    private val _deviceState = MutableStateFlow(DeviceState())
    val deviceState: StateFlow<DeviceState> = _deviceState

    private val _moveMap = MutableSharedFlow<LatLng>()
    val moveMap: SharedFlow<LatLng> = _moveMap

    private val sensorManager =
        application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)


    init {
        startListening()
    }


    fun locateEvent(event: LocateEvent) {
        when (event) {
            is LocateEvent.GpsChange -> {
                _deviceState.value = deviceState.value.copy(
                    isGpsOpen = event.res
                )
            }

            is LocateEvent.PermissionChange -> {
                _deviceState.value = deviceState.value.copy(
                    isGrantedPermission = event.res
                )
            }

            is LocateEvent.LocationChange -> {
                _deviceState.value = deviceState.value.copy(
                    locationPoint = event.latLng
                )
            }
        }
    }

    fun uiEvent(event: MapUiEvent) {
        when (event) {
            is MapUiEvent.MoveNowLocation -> {
                viewModelScope.launch {
                    _moveMap.emit(_deviceState.value.locationPoint)
                }
            }

            is MapUiEvent.MoveToLocation -> {
                viewModelScope.launch {
                    _moveMap.emit(event.latLng)
                }
            }

            is MapUiEvent.ShowSnackBar -> {

            }

            is MapUiEvent.ShowOpenGpsDialog -> {

            }
        }
    }


    //差值 减少重组
    private var prePoint = LatLng(0.0, 0.0)
    private val pointDiff = 0.00001
    private val locationListener = object : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation?) {
            location?.addrStr?.let {
                var latLng = LatLng(location.latitude, location.longitude)
                if (
                    abs(location.latitude - prePoint.latitude) > pointDiff ||
                    abs(location.longitude - prePoint.longitude) > pointDiff
                ) {
                    _deviceState.value = deviceState.value.copy(
                        locationPoint = latLng
                    )
                    BDMapSetting.myLocation = latLng
                }
                prePoint = LatLng(location.latitude, location.longitude)
            }
        }
    }
    private val locationClient: LocationClient = LocationClient(application).apply {
        locOption = LocationClientOption().apply {
            coorType = "bd09ll"
            scanSpan = 3000 // 每 3 秒获取一次定位
            isOpenGnss = true
            setIgnoreKillProcess(false)
            setIsNeedAddress(true)
            registerLocationListener(locationListener)
        }
    }

    fun startLocation() {
        if (!locationClient.isStarted) {
            viewModelScope.launch {
                locationClient.start()
            }
        }
    }

    private fun stopLocation() {
        viewModelScope.launch {
            locationClient.stop()
            locationClient.unRegisterLocationListener(locationListener)
        }
    }

    private fun startListening() {
        rotationSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }


    private var preDirection = 0f
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val rotationMatrix = FloatArray(9)
            val orientationVals = FloatArray(3)

            SensorManager.getRotationMatrixFromVector(rotationMatrix, it.values)
            SensorManager.getOrientation(rotationMatrix, orientationVals)

            val azimuth = Math.toDegrees(orientationVals[0].toDouble()).toFloat() // 0 - 360°
            val positiveAzimuth = (azimuth + 360) % 360
            //差值 减少重组次数
            if (abs(positiveAzimuth - preDirection) > 1) {
                _deviceState.value = deviceState.value.copy(
                    direction = positiveAzimuth
                )
            }
            preDirection = positiveAzimuth
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}


    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
        stopLocation()
    }
}

sealed class MapUiEvent {
    data class ShowSnackBar(val message: String) : MapUiEvent()
    data object ShowOpenGpsDialog : MapUiEvent()
    data object MoveNowLocation : MapUiEvent()
    data class MoveToLocation(val latLng: LatLng) : MapUiEvent()
}