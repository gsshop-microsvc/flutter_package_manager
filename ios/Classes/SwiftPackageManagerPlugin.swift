import Flutter
import UIKit

public class SwiftPackageManagerPlugin: NSObject, FlutterPlugin {
  var result: FlutterResult?
    
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "com.gsshop.mobile.flutter.package_manager", binaryMessenger: registrar.messenger())
    let instance = SwiftPackageManagerPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    self.result = result
    if call.method == "startIntent" {
      if let arguments = call.arguments as? [String:Any] {
        var dic:Dictionary<String,String> = ["packageName":"", "message":""]

        let uri = arguments["uri"] as? String ?? ""

        let targetUri = NSURL(string: uri)
        dic["packageName"] = targetUri?.scheme
        let isOpen = UIApplication.shared.openURL(targetUri as! URL)
        if (!isOpen) {
          dic["message"] = "NotInstalled"
        }
        self.result?(dic)
      }
    }
  }
}
