package com.cs388.socialsync

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

class EventsFragment : Fragment() {
    private lateinit var changeTimeButton : Button
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeTimeButton = view.findViewById(R.id.changeTimeButton)
        changeTimeButton.setOnClickListener {
            val intent = Intent(requireContext(), ChangeTimeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    companion object {
        fun newInstance(): EventsFragment {
            return EventsFragment()
        }
    }
}