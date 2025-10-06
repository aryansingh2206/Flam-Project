package com.edgeviewer.app.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.TextureView
import android.widget.Toast
import org.opencv.android.Utils
import org.opencv.core.Mat

class CameraController(private val context: Context) {

    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var backgroundHandler: Handler? = null
    private var textureView: TextureView? = null
    private lateinit var cameraManager: CameraManager

    // Native JNI function
    external fun processFrame(matAddr: Long)

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    fun setTextureView(tv: TextureView) {
        textureView = tv
    }

    fun startCamera() {
        val tv = textureView ?: return
        val surfaceTexture = tv.surfaceTexture ?: return

        // Set default buffer size to avoid green frames
        surfaceTexture.setDefaultBufferSize(640, 480)
        val surface = Surface(surfaceTexture)

        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList[0] // back camera

        val handlerThread = HandlerThread("CameraBackground")
        handlerThread.start()
        backgroundHandler = Handler(handlerThread.looper)

        try {
            cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    startPreview(surface)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                    cameraDevice = null
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    camera.close()
                    cameraDevice = null
                    Toast.makeText(context, "Camera error: $error", Toast.LENGTH_SHORT).show()
                }
            }, backgroundHandler)
        } catch (e: SecurityException) {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startPreview(surface: Surface) {
        val previewRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        previewRequestBuilder?.addTarget(surface)

        cameraDevice?.createCaptureSession(listOf(surface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    captureSession = session
                    previewRequestBuilder?.build()?.let { request ->
                        captureSession?.setRepeatingRequest(request, null, backgroundHandler)
                    }
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Toast.makeText(context, "Camera configuration failed", Toast.LENGTH_SHORT).show()
                }
            }, backgroundHandler)
    }

    fun stopCamera() {
        captureSession?.close()
        cameraDevice?.close()
    }

    // Returns current frame as Bitmap
    fun getFrame(): Bitmap? {
        return textureView?.bitmap
    }

    // Capture current frame and send to JNI/OpenCV
    fun processCurrentFrame() {
        val bitmap = getFrame() ?: return
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)
        processFrame(mat.nativeObjAddr)
        Utils.matToBitmap(mat, bitmap)
    }
}
