package edu.put.szlaki.src

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.*

class TimerService : Service() {
    private val timer = Timer()

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val time = intent.getDoubleExtra(TIME_EXTRA, 0.0)
        Log.d("TimerService", "Service started with time: $time")
        timer.scheduleAtFixedRate(TimeTask(time), 0, 1000)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }

    private inner class TimeTask(private var time: Double) : TimerTask() {
        override fun run() {
            time++
            Log.d("TimerService", "Time updated: $time")
            val intent = Intent(TIMER_UPDATED)
            intent.putExtra(TIME_EXTRA, time)
            sendBroadcast(intent)
        }
    }

    companion object {
        const val TIMER_UPDATED = "timerUpdated"
        const val TIME_EXTRA = "timeExtra"
    }
}
