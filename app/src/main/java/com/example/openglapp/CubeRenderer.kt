package com.example.openglapp

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CubeRenderer(private val glSurfaceView: GLSurfaceView) : GLSurfaceView.Renderer {

    private lateinit var cube: Cube

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    var scaleFactor = 1.0f
    var rotationX = 0.0f
    var rotationY = 0.0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        cube = Cube()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -5f, 0f, 0f, 0f, 0f, 1f, 0f)

        Matrix.setIdentityM(modelMatrix, 0)

        Matrix.rotateM(modelMatrix, 0, rotationX, 1.0f, 0.0f, 0.0f)
        Matrix.rotateM(modelMatrix, 0, rotationY, 0.0f, 1.0f, 0.0f)
        Matrix.scaleM(modelMatrix, 0, scaleFactor, scaleFactor, scaleFactor)

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

        cube.draw(mvpMatrix)
    }

    fun setScale(scale: Float) {
        scaleFactor = scale
    }

    fun setRotation(x: Float, y: Float) {
        rotationX = x
        rotationY = y
    }
}
