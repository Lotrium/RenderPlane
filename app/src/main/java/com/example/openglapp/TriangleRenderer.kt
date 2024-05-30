package com.example.openglapp

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TriangleRenderer : GLSurfaceView.Renderer {

    private lateinit var triangle: Triangle
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private var rotationAngle = 0.0f
    private var scaleFactor = 1.0f

    init {
        Matrix.setIdentityM(modelMatrix, 0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        triangle = Triangle()
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.scaleM(modelMatrix, 0, scaleFactor, scaleFactor, 1f)
        Matrix.rotateM(modelMatrix, 0, rotationAngle, 0f, 0f, 1f)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)

        triangle.draw(mvpMatrix)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    fun setRotation(angle: Float) {
        rotationAngle = angle
    }

    fun setScale(factor: Float) {
        scaleFactor = factor
    }
}
