package edu.put.szlaki.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import edu.put.szlaki.R
import edu.put.szlaki.database.StageDatabaseHandler
import edu.put.szlaki.database.TrialDatabaseHandler
import edu.put.szlaki.databinding.FragmentSecondBinding
import edu.put.szlaki.src.Stage


class TrialDetail : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private var id: String? = null
    private lateinit var name: String
    private lateinit var trialName: TextView
    private lateinit var trialImage: ImageView
    private lateinit var trialLength: TextView
    private lateinit var trialComment: TextView
    private lateinit var tableStage: TableLayout
    private lateinit var context: Context

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireContext()
        name = arguments?.getString("name").toString()
        trialName = binding.root.findViewById(R.id.trialName)
        trialImage = binding.root.findViewById(R.id.trialImage)
        trialLength = binding.root.findViewById(R.id.trialLength)
        tableStage = binding.root.findViewById(R.id.tableStage)
        trialComment = binding.root.findViewById(R.id.trialComment)
        trialComment.addTextChangedListener(object: TextWatcher {
            //Cause there is an error without those two first
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){}

            override fun afterTextChanged(text: Editable?){
                val dbHandler = TrialDatabaseHandler(context, null, null, 1)
                dbHandler.addComment(name, trialComment.text.toString())
            }
        })

        val stageButton = binding.root.findViewById<Button>(R.id.stageButton)
        stageButton.setOnClickListener {
            showAddStageDialog()
        }
        generatePage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun generatePage() {
        trialName.let { textView ->
            textView.text = name
        }
        val dbHandler = TrialDatabaseHandler(context, null, null, 1)
        val trial = dbHandler.getTrial(context, name)
        Glide.with(this)// load images from directory
            .load(trial?.image)
            .into(trialImage)
        trialComment.text = trial?.comment
        id = trial?.id
        Log.d("CAT", id.toString())
        trialLength.text = "Dlugosc: 0m"
        refreshStage()
    }

    private fun showAddStageDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_stage, null)
        dialogBuilder.setView(dialogView)

        val nameText = dialogView.findViewById<EditText>(R.id.nameText)
        val lengthText = dialogView.findViewById<EditText>(R.id.lengthText)
        val timeText = dialogView.findViewById<EditText>(R.id.timeText)

        dialogBuilder.setTitle("Nowy etap")
        dialogBuilder.setMessage("Wprowadź szczegółowe dane etapu:")
        dialogBuilder.setPositiveButton("Add") { _, _ ->
            val name = nameText.text.toString()
            val length = lengthText.text.toString()
            val time = timeText.text.toString()

            if (length.floatOrString() and time.floatOrString()){
                val dbHandler = StageDatabaseHandler(context, null, null, 1)
                val stage = Stage(id, name, length, time)
                dbHandler.addStage(context, stage)
                refreshStage()
            }
            else {
                Toast.makeText(context, "Wprowadzone dane są niepoprawne", Toast.LENGTH_SHORT).show()
            }

        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun String.floatOrString(): Boolean {
        val v = toFloatOrNull()
        return when(v) {
            null -> false
            else -> true
        }
    }

    private fun showDeleteStageDialog(context: Context, onYesClicked: () -> Unit, onNoClicked: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Na pewno chcesz usunąć ten etap?")
            .setCancelable(false)
            .setPositiveButton("Tak") { dialog, id ->
                onYesClicked.invoke()
                dialog.dismiss()
            }
            .setNegativeButton("Nie") { dialog, id ->
                onNoClicked.invoke()
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun refreshStage() {
        val dbHandler = StageDatabaseHandler(context, null, null, 1)
        var stages: MutableList<Stage>? = null
        stages = dbHandler.passStages(id)
        tableStage.removeAllViews()

        //create header row
        generateHeader()

        if (stages != null) {
            val rows = stages.count()
            var length = 0F
            for (i in 0 until rows) {
                val row = TableRow(context)

                val rowNumber = TextView(context)
                rowNumber.text = (i+1).toString()
                row.addView(rowNumber)

                val stageName = TextView(context)
                stageName.text = stages[i].name
                row.addView(stageName)

                val stageLength = TextView(context)
                stageLength.text = stages[i].length
                row.addView(stageLength)

                val stageTime = TextView(context)
                stageTime.text = stages[i].time
                row.addView(stageTime)

                row.setOnLongClickListener {
                    showDeleteStageDialog (
                        context = context,
                        onYesClicked = {
                            dbHandler.deleteStage(id, stageName.text.toString())
                            refreshStage()
                        },
                        onNoClicked = {
                            Toast.makeText(context, "BBBBBB", Toast.LENGTH_SHORT).show()
                        }
                            )
                    true
                }
                tableStage.addView(row)

                length += stages[i].length!!.toFloat()
            }
            trialLength.text = "Dlugosc: $length m"
        }
    }

    private fun generateHeader() {
        val headerRow = TableRow(context)
        val fontSize = 20F

        val headerNumber = TextView(context)
        headerNumber.text = "LP."
        headerNumber.setTextSize(fontSize)
        headerNumber.setPadding(10, 10, 10, 10)
        headerRow.addView(headerNumber)

        val headerName = TextView(context)
        headerName.text = "Nazwa"
        headerName.setTextSize(fontSize)
        headerName.setPadding(10, 10, 10, 10)
        headerRow.addView(headerName)

        val headerLength = TextView(context)
        headerLength.text = "Dlugosc"
        headerLength.setTextSize(fontSize)
        headerLength.setPadding(10, 10, 10, 10)
        headerRow.addView(headerLength)

        val headerTime = TextView(context)
        headerTime.text = "Czas"
        headerTime.setTextSize(fontSize)
        headerTime.setPadding(10, 10, 10, 10)
        headerRow.addView(headerTime)

        val trParamsSep = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.WRAP_CONTENT)
        trParamsSep.setMargins(5, 5, 5, 5)
        val layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT
        )
        headerRow.layoutParams = trParamsSep

        tableStage.addView(headerRow)

    }


}