-dontwarn android.support.**

-dontoptimize
-optimizations !code/simplification/arithmetic
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-keepparameternames
-renamesourcefileattribute SourceFile

-keepclassmembers class * extends android.support.v7.app.AppCompatActivity {
   public void *(android.view.View);
}

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

-keep class android.support.v7.widget.** { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}
