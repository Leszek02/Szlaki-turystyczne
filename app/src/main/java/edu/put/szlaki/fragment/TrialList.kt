package edu.put.szlaki.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.put.szlaki.src.CaptionedImagesAdapter
import edu.put.szlaki.R
import edu.put.szlaki.src.Trial
import edu.put.szlaki.database.TrialDatabaseHandler
import edu.put.szlaki.databinding.FragmentFirstBinding


class TrialList : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var trialView: RecyclerView
    private lateinit var adapter: CaptionedImagesAdapter

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trialView = binding.root.findViewById(R.id.TrialRecycler)

        showTrials2()
    }

    private fun showTrials2(){
        var trials: MutableList<Trial>? = null
        var context = requireContext()
        val dbHandler = TrialDatabaseHandler(context, null, null, 1)
        trials = dbHandler.passTrials()

        val rows = dbHandler.trialRowsNumber()
        val trialNames = Array<String>(rows) {""}
        val trialImages = Array<String>(rows) {""}
        if (trials != null) {
            for (i in trialNames.indices) {
                trialNames[i] = trials[i].name.toString()
            }
            for (i in trialImages.indices) {
                trialImages[i] = trials[i].image.toString()
            }
            adapter = CaptionedImagesAdapter(trialNames, trialImages)
            trialView.adapter = adapter

            val gridLayoutManager = GridLayoutManager(requireActivity(), 2)
            trialView.layoutManager = gridLayoutManager

            adapter.setListener(object: CaptionedImagesAdapter.Listener {
                override fun onClick(name: String){
                    val bundle = Bundle()
                    bundle.putString("name", name)
                    findNavController().navigate(
                        R.id.action_FirstFragment_to_SecondFragment,
                        bundle
                    )
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}