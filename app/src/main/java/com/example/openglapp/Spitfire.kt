package com.example.openglapp

import android.content.Context
import android.opengl.GLES20
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Spitfire(context: Context) {

    private val vertexShaderCode =
        "attribute vec4 vPosition;" +
                "uniform mat4 uMVPMatrix;" +
                "void main() {" +
                "  gl_Position = uMVPMatrix * vPosition;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    private val vertexBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer
    private val mProgram: Int

    private var vertices: FloatArray = floatArrayOf()
    private var indices: ShortArray = shortArrayOf()

    private val vertexStride = 3 * 4  // 4 bytes per vertex

    init {
        loadModel(context, "plane.obj")

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

        val positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition").also {
            if (it == -1) throw RuntimeException("Could not get attrib location for vPosition")
        }

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        val colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor").also {
            if (it == -1) throw RuntimeException("Could not get uniform location for vColor")
        }
        GLES20.glUniform4f(colorHandle, 0.5f, 0.5f, 0.5f, 1.0f)  // Set color to gray

        val mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix").also {
            if (it == -1) throw RuntimeException("Could not get uniform location for uMVPMatrix")
        }
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            indices.size,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
        )

        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun loadModel(context: Context, filename: String) {
        val inputStream = context.assets.open(filename)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val verticesList = mutableListOf<Float>()
        val indicesList = mutableListOf<Short>()

        reader.useLines { lines ->
            lines.forEach { line ->
                val parts = line.split(" ")
                when (parts[0]) {
                    "v" -> {
                        verticesList.add(parts[1].toFloat())
                        verticesList.add(parts[2].toFloat())
                        verticesList.add(parts[3].toFloat())
                    }
                    "f" -> {
                        indicesList.add((parts[1].split("/")[0].toShort() - 1).toShort())
                        indicesList.add((parts[2].split("/")[0].toShort() - 1).toShort())
                        indicesList.add((parts[3].split("/")[0].toShort() - 1).toShort())
                    }
                }
            }
        }

        vertices = verticesList.toFloatArray()
        indices = indicesList.toShortArray()
    }
}
