package edu.put.szlaki.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import edu.put.szlaki.R
import edu.put.szlaki.src.Trial
import edu.put.szlaki.database.TrialDatabaseHandler
import edu.put.szlaki.databinding.FragmentNewTrialBinding
import java.io.File
import java.io.FileOutputStream

class NewTrial : Fragment() {

    private var _binding: FragmentNewTrialBinding? = null
    private lateinit var button: Button
    private lateinit var context: Context
    private lateinit var nameText : EditText
    private lateinit var trialPicture: ImageView
    private lateinit var imageDirectory: File
    private var imagePath: String? = null

    private val binding get() = _binding!!

    private var mGetContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { result ->
        if (result != null) {
            val input = requireContext().contentResolver.openInputStream(result)
            val output = FileOutputStream(newImageName())
            imagePath = result.toString()
            Log.d("PICTUREEEEE", imagePath.toString())
            input?.copyTo(output)
            input?.close()
            output.close()
            loadImage()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNewTrialBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button = binding.root.findViewById(R.id.ButtonNewTrial)
        nameText = binding.root.findViewById(R.id.TrialName)
        trialPicture = binding.root.findViewById(R.id.TrialPicture)
        context = requireContext()

        val directory = context.filesDir
        imageDirectory = File("$directory/trialPictures")
        if (!imageDirectory.exists()) imageDirectory.mkdir()

        button.setOnClickListener{
            newTrial()
        }

        trialPicture.setOnClickListener{
            mGetContent.launch("image/*")
        }

    }

    fun newTrial(){
        context = requireContext()
        val dbHandler = TrialDatabaseHandler(context, null, null, 1)
        val id = dbHandler.trialRowsNumber() + 1
        var trial = Trial(id.toString(), nameText.text.toString(), "", imagePath.toString(), "")
        dbHandler.addTrial(context, trial)
        findNavController().navigate(R.id.action_newTrial_to_FirstFragment)
    }

    private fun newImageName(): File {
        val files = imageDirectory.listFiles()
        val count = files?.size ?: 0
        val newName = "image$count"
        return File(imageDirectory, newName)
    }

    private fun loadImage() {
        Glide.with(this)
            .load(imagePath.toString())
            .into(trialPicture)
        trialPicture.setPadding(0, 5, 0, 5)
    }
}