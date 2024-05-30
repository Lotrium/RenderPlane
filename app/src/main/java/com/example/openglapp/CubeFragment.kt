package com.example.openglapp

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup

class CubeFragment : Fragment() {

    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var renderer: CubeRenderer
    private lateinit var gestureDetector: GestureDetector
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    private var scaleFactor = 1.0f
    private var rotationX = 0.0f
    private var rotationY = 0.0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        glSurfaceView = GLSurfaceView(requireContext())
        glSurfaceView.setEGLContextClientVersion(2)
        renderer = CubeRenderer(glSurfaceView)
        glSurfaceView.setRenderer(renderer)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        gestureDetector = GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                rotationX += distanceY * 0.5f
                rotationY += distanceX * 0.5f
                renderer.setRotation(rotationX, rotationY)
                return true
            }
        })

        scaleGestureDetector = ScaleGestureDetector(requireContext(), object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f))
                renderer.setScale(scaleFactor)
                return true
            }
        })

        glSurfaceView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            scaleGestureDetector.onTouchEvent(event)
            true
        }

        return glSurfaceView
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }
}