#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_edgeviewer_app_ui_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++ (JNI connected!)";
    return env->NewStringUTF(hello.c_str());
}
