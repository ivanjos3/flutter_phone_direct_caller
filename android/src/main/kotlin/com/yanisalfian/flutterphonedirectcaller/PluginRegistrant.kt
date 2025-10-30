package com.yanisalfian.flutterphonedirectcaller

import io.flutter.embedding.engine.plugins.FlutterPlugin

object PluginRegistrant {
    fun registerWith(binding: FlutterPlugin.FlutterPluginBinding) {
        val plugin = FlutterPhoneDirectCallerPlugin()
        plugin.onAttachedToEngine(binding)
    }
}
