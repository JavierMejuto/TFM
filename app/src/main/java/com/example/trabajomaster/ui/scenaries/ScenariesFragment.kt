package com.example.trabajomaster.ui.scenaries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.trabajomaster.databinding.FragmentScenariesBinding

class ScenariesFragment : Fragment() {

    private lateinit var scenariesViewModel: ScenariesViewModel
    private var _binding: FragmentScenariesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        scenariesViewModel =
            ViewModelProvider(this).get(ScenariesViewModel::class.java)

        _binding = FragmentScenariesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        scenariesViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}