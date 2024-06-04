package edu.put.szlaki.src

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import edu.put.szlaki.R
import edu.put.szlaki.databinding.FragmentStoperBinding


class Stoper : Fragment() {
    private lateinit var timeView: TextView
    private var _binding: FragmentStoperBinding? = null
    private var seconds = 0
    private var running = false
    private var wasRunning = false

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        SavedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoperBinding.inflate(inflater, container, false)
        runStoper(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds")
            running = savedInstanceState.getBoolean("running")
            wasRunning = savedInstanceState.getBoolean("wasRunning")
        }
        timeView = binding.root.findViewById(R.id.time_view)
    }

    override fun onPause() {
        super.onPause()
        wasRunning = running
        running = false
    }

    override fun onResume() {
        super.onResume()
        if (wasRunning) {
            running = true
        }
    }

    fun onClickStart(view: View?) {
        running = true
    }

    fun onClickStop(view: View?) {
        running = false
    }

    fun onClickReset(view: View?) {
        running = false
        seconds = 0
    }

    private fun runStoper(view: View) {
        val handler = Handler()
        handler.post(object : Runnable {
            override fun run() {
                val hours = seconds / 3600
                val minutes = seconds % 3600 / 60
                val secs = seconds % 60
                val time = String.format("%d:%02d:%02d", hours, minutes, secs)
                timeView.text = time
                if (running) {
                    seconds++
                }
                handler.postDelayed(this, 1000)
            }
        })
    }
}