package edu.put.szlaki.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import edu.put.szlaki.R
import edu.put.szlaki.database.TrialDatabaseHandler
import edu.put.szlaki.databinding.FragmentStoperBinding
import edu.put.szlaki.src.TimerService
import edu.put.szlaki.src.Trial
import kotlin.math.roundToInt
import edu.put.szlaki.database.TimeDatabaseHandler

class Stoper : Fragment() {
    private var _binding: FragmentStoperBinding? = null
    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private lateinit var timeText: TextView
    private lateinit var startButton: Button
    private lateinit var restartButton: Button
    private lateinit var trialSelect: Spinner
    private var time = 0.0

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoperBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startButton = binding.root.findViewById(R.id.startStopButton)
        restartButton = binding.root.findViewById(R.id.resetButton)
        timeText = binding.root.findViewById(R.id.time)
        trialSelect = binding.root.findViewById(R.id.trialSpinner)

        startButton.setOnClickListener { startStopTimer() }
        restartButton.setOnClickListener { resetTimer() }

        serviceIntent = Intent(requireActivity(), TimerService::class.java)
        requireActivity().registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

        initializeSpinner(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        requireActivity().unregisterReceiver(updateTime)
    }

    private fun resetTimer() {
        stopTimer()
        val selectedOption = trialSelect.selectedItem.toString()
        if (selectedOption.equals("Wybierz szlak", true)) {
            Toast.makeText(requireContext(), "Wybierz szlak, do którego chcesz wstawić czas", Toast.LENGTH_SHORT).show();
        }
        else {
            val dbHandler = TimeDatabaseHandler(requireContext(), null, null, 1)
            dbHandler.addTrialTime(requireContext(), selectedOption, time.toString())
            time = 0.0
            timeText.text = getTimeStringFromDouble(time)
        }
    }

    private fun startStopTimer() {
        if (timerStarted)
            stopTimer()
        else
            startTimer()
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        requireActivity().startService(serviceIntent)
        startButton.text = "Stop"
        timerStarted = true
    }

    private fun stopTimer() {
        requireActivity().stopService(serviceIntent)
        startButton.text = "Start"
        timerStarted = false
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            timeText.text = getTimeStringFromDouble(time)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60
        return makeTimeString(hours, minutes, seconds)
    }


    private fun makeTimeString(hour: Int, min: Int, sec: Int): String =
        String.format("%02d:%02d:%02d", hour, min, sec)


    private fun initializeSpinner(context: Context) {
        val initialOptions = mutableListOf("Wybierz szlak")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, initialOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        trialSelect.adapter = adapter

        var trials: MutableList<Trial>? = null
        val dbHandler = TrialDatabaseHandler(context, null, null, 1)
        trials = dbHandler.passTrials()

        val rows = dbHandler.trialRowsNumber()
        val trialNames = Array<String>(rows) { "" }
        for (i in trialNames.indices) {
            adapter?.add(trials?.get(i)?.name.toString())
            adapter?.notifyDataSetChanged()
        }
    }
}
