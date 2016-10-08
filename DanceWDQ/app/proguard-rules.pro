# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/AndroidSDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.facebook.**
-keep enum com.facebook.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public interface com.facebook.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**
-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
-keep public class android.webkit.**
-keep class com.facebook.**
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
-keep public class [your_pkg].R$*{
    public static final int *;}


-printmapping  upload.map
-keepattributes Signature,InnerClasses
-keep class com.tencent.upload.network.base.ConnectionImpl
-keep class com.tencent.upload.network.base.ConnectionImpl {*;}
-keep class com.tencent.upload.UploadManager { *; }
-keep class com.tencent.upload.UploadManager$* { *; }
-keep class com.tencent.upload.Const {*;}
-keep class com.tencent.upload.Const$* { *; }
-keep class com.tencent.upload.task.** { *;}
-keep class com.tencent.upload.impl.** { * ; }
-keep class com.tencent.upload.utils.** { * ; }
-keepclasseswithmembers class com.tencent.upload.task.** { *; }
-keepclasseswithmembernames class com.tencent.upload.task.** { *; }
-keep class com.tencent.upload.task.ITask$* { *; }
-keep class com.tencent.upload.task.impl.FileDeleteTask$* { *; }
-keep class com.tencent.upload.task.impl.FileStatTask$* { *; }
-keep class com.tencent.upload.task.impl.FileCopyTask$* { *; }
-keep class com.tencent.upload.common.Global { *; }
-keep class com.tencent.upload.log.trace.TracerConfig { *; }
-keep class * extends com.qq.taf.jce.JceStruct { *; }
-printmapping  download.map
-keepattributes Signature,InnerClasses
-keep class com.tencent.download.Downloader { *; }
-keep class com.tencent.download.Downloader$* { *; }
-keep class com.tencent.download.core.DownloadResult { *; }
-keep class com.tencent.download.global.Global { *; }
-keep class com.tencent.download.module.log.trace.TracerConfig { *; }