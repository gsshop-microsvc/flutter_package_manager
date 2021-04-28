package com.gsshop.mobile.flutter

import androidx.annotation.NonNull

import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

import android.content.pm.PackageInfo
import android.content.pm.PackageManager

/** PackageManagerPlugin */
class PackageManagerPlugin: FlutterPlugin, MethodCallHandler {

    private var registrar: Registrar? = null
    private var mContext: Context? = null
    private var methodChannel: MethodChannel? = null

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val instance = PackageManagerPlugin()
            instance.registrar = registrar
            instance.onAttachedToEngine(registrar.context(), registrar.messenger())
        }
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        onAttachedToEngine(flutterPluginBinding.getApplicationContext(), flutterPluginBinding.getBinaryMessenger());
    }

    private fun onAttachedToEngine(applicationContext: Context, binaryMessenger: BinaryMessenger) {
        mContext = applicationContext
        methodChannel = MethodChannel(binaryMessenger, "com.gsshop.mobile.flutter.package_manager")
        methodChannel?.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when {
            call.method == "getPackageInfo" -> {
                var pi: PackageInfo? = null
                var packageName: String? = call.argument("packageName")
                try {
                    if (packageName != null) {
                      pi = mContext?.packageManager?.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS )
                    }
                } catch(e: Exception) {
                    print(e.localizedMessage)
                }
                result.success(pi?.versionName)
            }
            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        methodChannel?.setMethodCallHandler(null)
    }
}