EdgeViewer App

Real Time Edge Detection Viewer built with Android, OpenCV (C++), OpenGL ES, and a TypeScript web viewer.

---

Features Implemented

Android App
- Camera Integration: Captures live frames using Camera2 API.
- OpenCV C++ Processing: Frames are processed in native code via JNI.
  - Supports Canny Edge Detection and Grayscale filter.
- OpenGL ES 2.0 Rendering: Smoothly renders processed frames as textures.
- Toggle View (Bonus): Switch between raw camera feed and processed output.
- FPS Counter: Logs frame processing speed.

Web Viewer (TypeScript)
- Minimal TypeScript + HTML page.
- Displays a sample processed frame.
- Shows frame stats like FPS and resolution.

---

Architecture Overview

CameraController.kt  → Captures frames from Camera2 API
      ↓
JNI Bridge          → Sends frame to C++ OpenCV processing
      ↓
Native C++ code     → Applies edge detection or grayscale filter
      ↓
GLRenderer.kt       → Renders the processed frame using OpenGL ES
      ↓
Web Viewer          → Displays static sample frame + stats (TypeScript)

- Modular project structure:
/app          → Android Kotlin/Java code
/jni          → C++ OpenCV processing
/gl           → OpenGL ES renderer
/web          → TypeScript web viewer
/src/main/jniLibs → OpenCV native libraries (.so)

---

[Camera Feed] screenshots/screenshot1.png
[Edge Detection] screenshots/screenshot2.png
[Web Viewer] screenshots/screenshot_web.png

---

Setup Instructions

1. Install Android Studio with NDK support.
2. Clone the repository:
   git clone https://github.com/yourusername/edgeviewer-app.git
3. Open the project in Android Studio.
4. Make sure OpenCV SDK module is imported under /app/opencv-4.12.0/sdk/java.
5. Ensure jniLibs folder exists under /app/src/main/jniLibs containing:
   armeabi-v7a/
   arm64-v8a/
   x86/
   x86_64/
6. Sync Gradle and Build project.
7. Launch on an Android device or emulator.

---

Notes

- All OpenCV logic is in C++; Java/Kotlin handles camera & UI.
- JNI is used for communication between Kotlin and C++.
- OpenGL ES 2.0 ensures smooth real-time rendering (≥ 15 FPS).
- TypeScript web viewer demonstrates ability to bridge native output to a web layer.

---

Project Structure

EdgeViewer/
 ├─ app/
 │   ├─ src/main/java/com/edgeviewer/app  → Kotlin code
 │   ├─ src/main/jniLibs                  → OpenCV .so files
 │   ├─ opencv-4.12.0/sdk/java            → OpenCV module
 │   └─ gl/                               → OpenGL renderer classes
 ├─ jni/                                  → C++ OpenCV processing
 └─ web/                                  → TypeScript web viewer

---

Submission Checklist

- [x] Android app working with Camera + OpenCV + OpenGL
- [x] TypeScript web viewer displaying processed frame
- [x] Proper Git commit history (modular, meaningful commits)
- [x] Screenshots or GIF in /screenshots
- [x] README.md fully documented
