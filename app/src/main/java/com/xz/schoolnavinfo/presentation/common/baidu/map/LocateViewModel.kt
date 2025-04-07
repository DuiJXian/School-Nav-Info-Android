package com.xz.schoolnavinfo.presentation.common.baidu.map

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
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class LocateViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application), SensorEventListener {
    private val _deviceState = MutableStateFlow(DeviceState())
    val deviceState: StateFlow<DeviceState> = _deviceState

    private val sensorManager =
        application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private val _moveMap = MutableSharedFlow<Unit>()
    val moveMap:SharedFlow<Unit> = _moveMap.asSharedFlow()

    init {
        startListening()
    }


    fun locateEvent(event: LocateEvent) {
        when (event) {
            is LocateEvent.GpsChange -> {
                _deviceState.value = deviceState.value.copy(
                    isOpenGps = event.res
                )
            }

            is LocateEvent.PermissionChange -> {
                _deviceState.value = deviceState.value.copy(
                    isGrantedPermission = event.res
                )
            }
        }
    }

    //差值 减少重组
    private var prePoint = LatLng(0.0, 0.0)
    private val pointDiff = 0.00001
    private val locationListener = object : BDAbstractLocationListener() {
        private var flag = true;
        override fun onReceiveLocation(location: BDLocation?) {
            location?.addrStr?.let {
                val latLng = LatLng(location.latitude, location.longitude)
                if (
                    abs(location.latitude - prePoint.latitude) > pointDiff ||
                    abs(location.longitude - prePoint.longitude) > pointDiff
                ) {
                    _deviceState.value = deviceState.value.copy(
                        locationPoint = latLng
                    )
                }
                prePoint = LatLng(location.latitude, location.longitude)

                if (flag){
                    flag = false
                    viewModelScope.launch {
                        _moveMap.emit(Unit)
                    }
                }
            }
        }
    }
    private val locationClient: LocationClient = LocationClient(application).apply {
        locOption = LocationClientOption().apply {
            coorType = "bd09ll"
            scanSpan = 1000
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

    fun stopLocation() {
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
    }
}