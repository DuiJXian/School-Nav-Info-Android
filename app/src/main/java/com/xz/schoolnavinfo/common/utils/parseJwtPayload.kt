package com.xz.schoolnavinfo.common.utils

import android.util.Base64
import org.json.JSONObject

fun parseJwtPayload(token: String): JSONObject? {
    return try {
        val parts = token.split(".")
        if (parts.size != 3) return null

        val payload = parts[1]
        val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        JSONObject(String(decodedBytes))
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
