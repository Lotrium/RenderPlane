package com.example.openglapp

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Triangle {
    private val vertexShaderCode =
        "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = uMVPMatrix * vPosition;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    private val triangleCoords = floatArrayOf(
        0.0f,  0.622008459f, 0.0f,
        -0.5f, -0.311004243f, 0.0f,
        0.5f, -0.311004243f, 0.0f
    )

    private val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    private val vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(triangleCoords)
                position(0)
            }
        }

    private val program: Int = GLES20.glCreateProgram().also {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        GLES20.glAttachShader(it, vertexShader)
        GLES20.glAttachShader(it, fragmentShader)
        GLES20.glLinkProgram(it)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition").also {
            GLES20.glEnableVertexAttribArray(it)
            GLES20.glVertexAttribPointer(
                it,
                3,
                GLES20.GL_FLOAT,
                false,
                12,
                vertexBuffer
            )
        }

        val colorHandle = GLES20.glGetUniformLocation(program, "vColor").also {
            GLES20.glUniform4fv(it, 1, color, 0)
        }

        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix").also {
            GLES20.glUniformMatrix4fv(it, 1, false, mvpMatrix, 0)
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)

        GLES20.glDisableVertexAttribArray(positionHandle)
    }
}
