package com.limegrass.wanicchou.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.limegrass.wanicchou.R

abstract class TextBlockFragment : Fragment(){
    lateinit var title : String
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val attachToRoot = false
        val view = inflater.inflate(R.layout.fragment_text_list,
                container,
                attachToRoot)
        view.findViewById<TextView>(R.id.tv_text_block_label).text = title
        return view
    }
}