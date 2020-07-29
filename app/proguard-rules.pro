# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
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
-dontwarn javax.inject.**
-dontwarn javax.annotation.**
-keep class javax.inject.**
-keep class javax.annotation.**
-keepclassmembers class * {
	@javax.inject.Inject <init>(...);
	@javax.inject.Inject <init>();
	@javax.inject.Inject <fields>;
	public <init>(...);
}
-keepnames @toothpick.InjectConstructor class *
-keepclasseswithmembernames class * { toothpick.ktp.delegate.* *; }
-keepclassmembers class * {
    toothpick.ktp.delegate.* *;
}

-keep class com.google.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.apache.**
-dontwarn org.w3c.dom.**

-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.

-keep class dsl_json.json.** { *; }
-dontwarn com.dslplatform.json.**
-keep @com.dslplatform.json.CompiledJson class *
-keep @com.dslplatform.json.JsonConverter class *

-keep class com.catp.model.** { *; }

-keep class com.dslplatform.json.** { *; }
-keep class kotlin._Pair_DslJsonConverter { *; }
-keep class kotlin.Pair { *; }
-keepnames class com.google.android.gms.** {*;}
