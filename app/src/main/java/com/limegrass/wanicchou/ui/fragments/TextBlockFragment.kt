package com.limegrass.wanicchou.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.limegrass.wanicchou.R
import kotlin.properties.Delegates

abstract class TextBlockFragment : Fragment() {
    protected var title by Delegates.observable(""){
        _, _, newValue ->
        fragmentView.findViewById<TextView>(R.id.tv_text_block_label).text = newValue
    }
    private lateinit var fragmentView : View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val attachToRoot = false
        fragmentView = inflater.inflate(R.layout.fragment_text_list,
                container,
                attachToRoot)
        fragmentView.findViewById<TextView>(R.id.tv_text_block_label).text = title
        return fragmentView
    }
}