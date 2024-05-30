package com.example.openglapp

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Cube {

    private val vertexShaderCode =
        "attribute vec4 vPosition;" +
                "attribute vec4 vColor;" +
                "uniform mat4 uMVPMatrix;" +
                "varying vec4 fColor;" +
                "void main() {" +
                "  gl_Position = uMVPMatrix * vPosition;" +
                "  fColor = vColor;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "varying vec4 fColor;" +
                "void main() {" +
                "  gl_FragColor = fColor;" +
                "}"

    private val vertices = floatArrayOf(
        -1.0f, -1.0f, -1.0f,  0.0f, 0.0f, 0.0f, 1.0f,
        1.0f, -1.0f, -1.0f,  1.0f, 0.0f, 0.0f, 1.0f,
        1.0f, 1.0f, -1.0f,  1.0f, 1.0f, 0.0f, 1.0f,
        -1.0f, 1.0f, -1.0f,  0.0f, 1.0f, 0.0f, 1.0f,
        -1.0f, -1.0f, 1.0f,  0.0f, 0.0f, 1.0f, 1.0f,
        1.0f, -1.0f, 1.0f,  1.0f, 0.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,  1.0f, 1.0f, 1.0f, 1.0f,
        -1.0f, 1.0f, 1.0f,  0.0f, 1.0f, 1.0f, 1.0f
    )

    private val indices = shortArrayOf(
        0, 1, 2, 0, 2, 3,
        4, 5, 6, 4, 6, 7,
        0, 4, 5, 0, 5, 1,
        3, 7, 6, 3, 6, 2,
        0, 3, 7, 0, 7, 4,
        1, 5, 6, 1, 6, 2
    )

    private val vertexBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer

    private val mProgram: Int

    private val vertexCount = vertices.size / 7
    private val vertexStride = 7 * 4

    init {
        val vertexByteBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
        vertexByteBuffer.order(ByteOrder.nativeOrder())
        vertexBuffer = vertexByteBuffer.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        val indexByteBuffer = ByteBuffer.allocateDirect(indices.size * 2)
        indexByteBuffer.order(ByteOrder.nativeOrder())
        indexBuffer = indexByteBuffer.asShortBuffer()
        indexBuffer.put(indices)
        indexBuffer.position(0)

        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(mProgram)

        val positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        val colorHandle = GLES20.glGetAttribLocation(mProgram, "vColor")
        GLES20.glEnableVertexAttribArray(colorHandle)
        GLES20.glVertexAttribPointer(
            colorHandle,
            4,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer.position(3)
        )

        val mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            indices.size,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
        )

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(colorHandle)
    }
}