import 'dart:async';

import 'package:flutter/services.dart';

import 'package:equatable/equatable.dart';

class PackageInfo extends Equatable {
  final String packageName;
  final String versionName;
  final int versionCode;

  PackageInfo(this.packageName, this.versionName, this.versionCode);

  @override
  List<Object?> get props => [];

  static PackageInfo fromJson(Map<String, dynamic> json) {
    var flexible = PackageInfo(
      json['packageName'] as String,
      json['versionName'] as String,
      json['versionCode'] as int,
    );

    return flexible;
  }
}

class PackageManager {
  static const MethodChannel _channel =
      const MethodChannel('com.gsshop.mobile.flutter.package_manager');

  static Future<PackageInfo?> getPackageInfo(String packageName) async {
    PackageInfo? result;
    final Map<dynamic, dynamic>? info =
        await _channel.invokeMethod('getPackageInfo', <String, dynamic>{
      'packageName': packageName,
    });

    if (info != null) {
      result = PackageInfo.fromJson(info.cast<String, dynamic>());
    }

    return result;
  }

  static Future<void> startIntent(String uri) async {
    await _channel.invokeMethod('startIntent', <String, dynamic>{
      'uri': uri,
    });
  }
}
