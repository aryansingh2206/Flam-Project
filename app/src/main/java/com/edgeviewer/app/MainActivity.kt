package com.edgeviewer.app.ui

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.edgeviewer.app.camera.CameraController
import com.edgeviewer.app.gl.GLRenderer
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {

    private lateinit var cameraController: CameraController
    private lateinit var glView: GLSurfaceView
    private lateinit var glRenderer: GLRenderer

    private val mainHandler = Handler(Looper.getMainLooper())
    private val frameInterval: Long = 66 // ~15 FPS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = FrameLayout(this)
        setContentView(container)

        // Initialize OpenGL view
        glView = GLSurfaceView(this)
        glView.setEGLContextClientVersion(2)
        glRenderer = GLRenderer()
        glView.setRenderer(glRenderer)
        glView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        container.addView(glView)

        // Initialize CameraController
        cameraController = CameraController(this)

        // Request camera permission
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                startCameraFlow()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun startCameraFlow() {
        // Use TextureView internally in CameraController for frame capture
        cameraController.setTextureView(glRendererTextureView())
    }

    // Hidden TextureView for frame capture (not displayed)
    private fun glRendererTextureView(): android.view.TextureView {
        val tv = android.view.TextureView(this)
        tv.surfaceTextureListener = object : android.view.TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: android.graphics.SurfaceTexture, width: Int, height: Int) {
                cameraController.startCamera()
                startFrameLoop()
            }

            override fun onSurfaceTextureSizeChanged(surface: android.graphics.SurfaceTexture, width: Int, height: Int) {}
            override fun onSurfaceTextureDestroyed(surface: android.graphics.SurfaceTexture): Boolean {
                cameraController.stopCamera()
                stopFrameLoop()
                return true
            }

            override fun onSurfaceTextureUpdated(surface: android.graphics.SurfaceTexture) {}
        }
        return tv
    }

    // Frame update loop
    private val frameRunnable = object : Runnable {
        override fun run() {
            val bitmap: Bitmap? = cameraController.getFrame()
            bitmap?.let {
                cameraController.processCurrentFrame() // JNI/OpenCV
                glRenderer.updateFrame(it)
                glView.requestRender()
            }
            mainHandler.postDelayed(this, frameInterval)
        }
    }

    private fun startFrameLoop() {
        mainHandler.post(frameRunnable)
    }

    private fun stopFrameLoop() {
        mainHandler.removeCallbacks(frameRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopFrameLoop()
        cameraController.stopCamera()
    }
}
