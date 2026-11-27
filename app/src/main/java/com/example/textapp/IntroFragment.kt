package com.example.textapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class IntroFragment : Fragment(R.layout.fragment_intro) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.btnContinue).setOnClickListener {
            findNavController().navigate(R.id.action_intro_to_ready)
        }
    }
}