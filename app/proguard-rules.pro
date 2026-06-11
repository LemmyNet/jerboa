# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# With R8 full mode generic signatures are stripped for classes that are not 
# kept. Suspend functions are wrapped in continuations where the type argument 
# is used. 
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation


# Some prettytime stuff
-keep class com.ocpsoft.pretty.time.i18n.**
-keep class org.ocpsoft.prettytime.i18n.**
-keepnames class ** implements org.ocpsoft.prettytime.TimeUnit

# Ktor needs this
-dontwarn org.slf4j.impl.StaticLoggerBinder

# Until https://issuetracker.google.com/issues/425120571 is fixed
-keepclassmembers class androidx.compose.ui.graphics.layer.view.ViewLayerContainer {
    protected void dispatchGetDisplayList();
}