package com.project.projectalgi001.admin.ui.listdo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.projectalgi001.databinding.ListDOFragmentBinding

class ListDOFragment : Fragment() {
    private var _binding: ListDOFragmentBinding? = null
    private val binding get() = _binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ListDOFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }
}