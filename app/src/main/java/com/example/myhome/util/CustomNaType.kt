package com.example.myhome.util

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.example.myhome.domain.automation.Automation
import com.example.myhome.domain.device.Device
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
object CustomNaType {

    val AutomationType = object : NavType<Automation>(
        isNullableAllowed = true
    ) {
        override fun get(bundle: Bundle, key: String): Automation? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): Automation {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun serializeAsValue(value: Automation): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun put(bundle: Bundle, key: String, value: Automation) {
            bundle.putString(key, Json.encodeToString(value))
        }
    }

    val DeviceType = object : NavType<Device>(
        isNullableAllowed = true
    ) {
        override fun get(bundle: Bundle, key: String): Device? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): Device {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun serializeAsValue(value: Device): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun put(bundle: Bundle, key: String, value: Device) {
            bundle.putString(key, Json.encodeToString(value))
        }
    }
}