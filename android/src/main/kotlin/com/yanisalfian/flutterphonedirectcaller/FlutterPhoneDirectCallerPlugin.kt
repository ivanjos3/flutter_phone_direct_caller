package com.yanisalfian.flutterphonedirectcaller

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener

/** FlutterPhoneDirectCallerPlugin */
class FlutterPhoneDirectCallerPlugin: FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware, RequestPermissionsResultListener {

    private lateinit var channel: MethodChannel
    private var activity: Activity? = null
    private var pendingResult: MethodChannel.Result? = null
    private var phoneNumber: String? = null

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(binding.binaryMessenger, "flutter_phone_direct_caller")
        channel.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addRequestPermissionsResultListener(this)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addRequestPermissionsResultListener(this)
    }

    override fun onDetachedFromActivity() {
        activity = null
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        if (call.method == "callNumber") {
            phoneNumber = call.argument("number") ?: ""
            pendingResult = result

            val formattedNumber = "tel:${phoneNumber!!.replace("#", "%23")}"

            if (ContextCompat.checkSelfPermission(activity!!, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity!!, arrayOf(CALL_PHONE), CALL_REQ_CODE)
            } else {
                result.success(makeCall(formattedNumber))
            }
        } else {
            result.notImplemented()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray): Boolean {
        if (requestCode == CALL_REQ_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                phoneNumber?.let {
                    pendingResult?.success(makeCall("tel:${it.replace("#", "%23")}"))
                }
            } else {
                pendingResult?.success(false)
            }
            return true
        }
        return false
    }

    private fun makeCall(number: String): Boolean {
        return try {
            val intent = Intent(if (isTelephonyEnabled()) Intent.ACTION_CALL else Intent.ACTION_VIEW)
            intent.data = Uri.parse(number)
            activity?.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e("Caller", "Error: ${e.message}")
            false
        }
    }

    private fun isTelephonyEnabled(): Boolean {
        val tm = activity?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.phoneType != TelephonyManager.PHONE_TYPE_NONE
    }

    companion object {
        private const val CALL_REQ_CODE = 0
        private const val CALL_PHONE = Manifest.permission.CALL_PHONE
    }
}
