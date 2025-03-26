package com.xz.schoolnavinfo.presentation.common.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat


object LocateUtils {
    // 检查 GPS 是否开启
    fun isGpsEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // 跳转到 GPS 设置页面
    fun openGpsSettings(context: Context) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }

    // 检查是否已经拥有定位权限
    fun isGrantedLocationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // 在 Android 6.0（API 23）之前，默认权限已被授予
        }
    }

    fun metersToKilometers(distance: Int): String {
        val kilometers = distance / 1000.0
        return String.format("%.1f", kilometers)
    }
}
