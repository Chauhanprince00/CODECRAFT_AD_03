package com.example.stopwatch

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stopwatch.R.color.black
import com.example.stopwatch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var isRunning = false
    private var startTime: Long = 0L
    private var timeBuffer: Long = 0L
    private var isExpanded = false
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.playButton.setOnClickListener {

            if (!isRunning) {
                // Start the stopwatch
                startStopwatch()
                toggleButtons()
                isRunning = true
                binding.resetButton.isEnabled = false  // Reset button disabled when running
            }
        }

        binding.pouseButton.setOnClickListener {

            if (isRunning) {
                pauseStopwatch()
                isRunning = false

                resetButtons()

                binding.resetButton.isEnabled = true  // Reset button enabled when paused
            }
        }

        binding.resetButton.setOnClickListener {
            if (!isRunning) {
                resetStopwatch()
            }

        }


    }

    private fun resetStopwatch() {
        isRunning = false
        startTime = 0L
        timeBuffer = 0L
        handler.removeCallbacks(updateTimer)

        binding.time.text = "00:00:00"

        binding.playButton.visibility = View.VISIBLE
        binding.pouseButton.visibility = View.GONE
    }
    private fun pauseStopwatch() {
        if (isRunning) {
            timeBuffer += SystemClock.elapsedRealtime() - startTime
            handler.removeCallbacks(updateTimer)
            isRunning = false

           binding.playButton.visibility = View.VISIBLE
            binding.pouseButton.visibility = View.GONE
            binding.resetButton.setImageResource(R.drawable.restartbright)
        }
    }

    private val updateTimer: Runnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                val timeNow = SystemClock.elapsedRealtime() - startTime
                val updatedTime = timeBuffer + timeNow

                val seconds = (updatedTime / 1000) % 60
                val minutes = (updatedTime / (1000 * 60)) % 60
                val milliseconds = (updatedTime % 1000) / 10  // 2-digit milliseconds


                val formattedTime = String.format("%02d:%02d:%02d", minutes,seconds,milliseconds)
               binding.time.text = formattedTime

                handler.postDelayed(this, 10) // Update every 10 milliseconds
            }
        }
    }

    private fun startStopwatch() {
        if (!isRunning) {
            startTime = SystemClock.elapsedRealtime()
            handler.post(updateTimer)
            isRunning = true

            binding.playButton.visibility = View.GONE
            binding.pouseButton.visibility = View.VISIBLE
            binding.resetButton.setImageResource(R.drawable.restartlight)}
    }

    private fun toggleButtons() {
        if (!isExpanded) {
            binding.resetButton.visibility = View.VISIBLE

            // Animate Reset Button (Right Side)
            ObjectAnimator.ofFloat(binding.resetButton, "translationX", 300f).apply {
                duration = 300
                start()
            }


            isExpanded = true
        }
    }

    private fun resetButtons() {
        if (startTime == 0L) { // Check if time is 0
            // Animate Reset Button back to original position
            ObjectAnimator.ofFloat(binding.resetButton, "translationX", 0f).apply {
                duration = 300
                start()
            }


            binding.resetButton.postDelayed({
                binding.resetButton.visibility = View.GONE
            }, 300)

            isExpanded = false
        }
    }


}