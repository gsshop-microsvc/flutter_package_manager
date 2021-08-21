package com.gsshop.mobile.flutter

import androidx.annotation.NonNull

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.ActivityNotFoundException
import android.util.Log

import android.net.Uri
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.net.URISyntaxException

import android.content.pm.PackageInfo
import android.content.pm.ResolveInfo
import android.content.pm.PackageManager

import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding

/** PackageManagerPlugin */
class PackageManagerPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {

    private var registrar: Registrar? = null
    private var mContext: Context? = null
    private var methodChannel: MethodChannel? = null
    private var mActivity: Activity? = null

    companion object {
        private var registrar: PluginRegistry.Registrar? = null

        private fun setRegistrar(_registrar: PluginRegistry.Registrar) {
            registrar = _registrar
        }
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        mContext = flutterPluginBinding.applicationContext
        methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.gsshop.mobile.flutter.package_manager")
        methodChannel?.setMethodCallHandler(this)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        mActivity = binding.activity
    }

    override fun onDetachedFromActivity() {
        TODO("Not yet implemented")
    }

    override fun onDetachedFromActivityForConfigChanges() {
        TODO("Not yet implemented")
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        TODO("Not yet implemented")
    }

    private fun startIntent(uri: String?, result: Result) {
        var packagename: String? = ""
        var message: String? = ""
        var intent: Intent? = null

        try {
            if ( uri != null && uri.startsWith("intent:") ) {
                try {
                    intent = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME)
                } catch (ex: URISyntaxException) {
                    message = "URISyntaxException"
                    return
                }

                if (intent != null) {
                    if (null != intent.getPackage()) {
                        packagename = intent.getPackage()
                    }
                    Log.d("--intent---", intent.getDataString().toString())
                    var androidIntent = Intent(Intent.ACTION_VIEW, Uri.parse(intent.getDataString()))
                    mActivity?.startActivity(androidIntent)
                }
            } else {
                var androidIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                mActivity?.startActivity(androidIntent)
            }
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            message = "NotInstalled"
        }

        result.success(object: HashMap<String, Object>() {
            init {
                put("packageName", packagename as Object)
                put("message", message as Object)
            }
        })
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "getPackageInfo") {
            var pi: PackageInfo? = null
            var packageName: String? = call.argument("packageName")
            try {
                if (packageName != null) {
                    pi = mContext?.packageManager?.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS )
                }
            } catch(e: Exception) {
                print(e.localizedMessage)
            }
            
            if (pi != null) {
                result.success(object: HashMap<String, Object>() {
                    init {
                        put("versionName", pi.versionName as Object)
                        put("packageName", pi.packageName as Object)
                        put("versionCode", pi.versionCode as Object)
                    }
                })
            } else {
                result.success(null)
            }
        } else if (call.method == "startIntent") {
            var uri: String? = call.argument("uri")
            startIntent(uri, result)
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        methodChannel?.setMethodCallHandler(null)
    }
}