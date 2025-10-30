package com.yanisalfian.flutterphonedirectcaller;

import androidx.annotation.Keep;
import com.yanisalfian.flutterphonedirectcaller.FlutterPhoneDirectCallerPlugin;
import io.flutter.embedding.engine.plugins.FlutterPlugin;

@Keep
public final class GeneratedPluginRegistrant {
    public static void registerWith(FlutterPlugin.FlutterPluginBinding binding) {
        new FlutterPhoneDirectCallerPlugin().onAttachedToEngine(binding);
    }
}
