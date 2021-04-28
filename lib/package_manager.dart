import 'dart:async';

import 'package:flutter/services.dart';

class PackageManager {
  static const MethodChannel _channel =
      const MethodChannel('com.gsshop.mobile.flutter.package_manager');

  static Future<String?> getPackageInfo(String packageName) async {
    final String? version =
        await _channel.invokeMethod('getPackageInfo', <String, dynamic>{
      'packageName': packageName,
    });
    return version;
  }
}
