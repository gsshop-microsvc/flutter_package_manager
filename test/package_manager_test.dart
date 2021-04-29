import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:package_manager/package_manager.dart';

void main() {
  const MethodChannel channel = MethodChannel('package_manager');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {});

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPackageInfo', () async {
    var info = await PackageManager.getPackageInfo("com.kakao.talk");
    expect(info?.packageName, 'com.kakao.talk');
  });
}
