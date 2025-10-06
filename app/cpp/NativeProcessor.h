#include <jni.h>
#include <opencv2/opencv.hpp>
#include <android/log.h>

#define LOG_TAG "NativeProcessor"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C"
JNIEXPORT jstring JNICALL
Java_com_edgeviewer_app_ui_MainActivity_stringFromJNI(JNIEnv* env, jobject /* this */) {
    return env->NewStringUTF("JNI Works!");
}

// Example function: Process frame (grayscale)
extern "C"
JNIEXPORT void JNICALL
Java_com_edgeviewer_app_camera_CameraController_processFrame(JNIEnv* env, jobject /* this */,
jlong matAddr) {
cv::Mat &frame = *(cv::Mat *) matAddr;

if (!frame.empty()) {
cv::cvtColor(frame, frame, cv::COLOR_RGBA2GRAY);
cv::Canny(frame, frame, 80, 100);
LOGI("Frame processed in native code");
}
}
