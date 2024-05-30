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

class TriangleFragment : Fragment() {

    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var gestureDetector: GestureDetector
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    private var scaleFactor = 1.0f
    private var rotationAngle = 0.0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_triangle, container, false)

        glSurfaceView = rootView.findViewById(R.id.glSurfaceView)
        glSurfaceView.setEGLContextClientVersion(2)
        val renderer = TriangleRenderer()
        glSurfaceView.setRenderer(renderer)

        gestureDetector = GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                e1?.let { e1 ->
                    e2?.let { e2 ->
                        rotationAngle += distanceX / 2
                        glSurfaceView.queueEvent {
                            renderer.setRotation(rotationAngle)
                        }
                        glSurfaceView.requestRender()
                        return true
                    }
                }
                return false
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                scaleFactor *= 1.5f
                glSurfaceView.queueEvent {
                    renderer.setScale(scaleFactor)
                }
                glSurfaceView.requestRender()
                return true
            }
        })

        scaleGestureDetector = ScaleGestureDetector(requireContext(), object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                glSurfaceView.queueEvent {
                    renderer.setScale(scaleFactor)
                }
                glSurfaceView.requestRender()
                return true
            }
        })

        glSurfaceView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            gestureDetector.onTouchEvent(event)
            true
        }

        return rootView
    }
}