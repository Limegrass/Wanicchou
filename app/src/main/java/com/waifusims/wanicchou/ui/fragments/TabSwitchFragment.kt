package com.waifusims.wanicchou.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.waifusims.wanicchou.R

class TabSwitchFragment : Fragment() {
    companion object {
        private val TAG : String = TabSwitchFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.v(TAG, "Fragment: TabSwitch")
        val attachToRoot = false
        val view = inflater.inflate(R.layout.fragment_tab_switch,
                                    container,
                                    attachToRoot)
        return view
    }
}

