#import "PackageManagerPlugin.h"
#if __has_include(<package_manager/package_manager-Swift.h>)
#import <package_manager/package_manager-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "package_manager-Swift.h"
#endif

@implementation PackageManagerPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftPackageManagerPlugin registerWithRegistrar:registrar];
}
@end
