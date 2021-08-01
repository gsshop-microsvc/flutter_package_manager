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
        try {
            var intent: Intent? = null
            var ri: ResolveInfo? = null

            if ( uri != null && uri.startsWith("intent:") ) {
                try {
                    intent = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME)
                } catch (ex: URISyntaxException) {
                    result.success("uri not match")
                    return
                }

                if (intent != null) {
                    var packagename = intent.getPackage()
                    var scheme = intent.getScheme()

                    try {
                        var androidIntent = Intent(Intent.ACTION_VIEW, Uri.parse(intent.getDataString()))
                        mActivity?.startActivity(androidIntent)
                        //Log.d("---777---", "start activity")
                        result.success("start activity")
                    } catch (e: Exception) {
                        // market 으로 이동
                        var androidIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:" + packagename))
                        mActivity?.startActivity(androidIntent)
                        //Log.d("---777---", "start market")
                        result.success("start market")
                    }

                    /* TODO 동작을 안함
                    if (packagename != null) {
                        Log.d("---777---", packagename)
                        ri = mContext?.packageManager?.resolveActivity(intent, 0)
                    }

                    if (ri != null) {
                        var androidIntent = Intent(Intent.ACTION_VIEW, Uri.parse(intent.getDataString()))
                        mActivity?.startActivity(androidIntent)
                        Log.d("---777---", "start activity")
                        result.success("start activity")
                    } else {
                        // not installed
                        Log.d("---777---", "start market")
                        if (packagename != null && scheme !=null ) {
                            var androidIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:" + packagename))
                            mActivity?.startActivity(androidIntent)
                            result.success("start market")
                        }
                    }
                    */
                }
            } else {
                var androidIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                mActivity?.startActivity(androidIntent)
                result.success("start activity")
            }
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            result.success("activity not found")
        }
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