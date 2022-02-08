package com.project.projectalgi001.admin.ui.settingsAdmin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import com.project.projectalgi001.R
import com.project.projectalgi001.databinding.FragmentAdminSettingsBinding

class AdminSettingsFragment : Fragment() {

    private var _binding: FragmentAdminSettingsBinding? = null
    private val binding get() = _binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentAdminSettingsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (childFragmentManager.findFragmentById(R.id.fragment_container_admin) == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_admin, SettingPreferenceAdmin())
                .commit()
        }
    }

    class SettingPreferenceAdmin : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preference_admin, rootKey)
        }
    }
}