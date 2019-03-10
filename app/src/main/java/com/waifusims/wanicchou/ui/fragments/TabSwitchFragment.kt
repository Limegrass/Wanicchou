package com.waifusims.wanicchou.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
        setWordButtonOnClick(view)
        setRelatedButtonOnClick(view)
        return view
    }

    private fun setWordButtonOnClick(view : View){
        val wordButton = view.findViewById<TextView>(R.id.btn_tab_word)
        wordButton.setOnClickListener {
            val definitionFragment = fragmentManager!!.findFragmentById(R.id.fragment_definition)
            if(definitionFragment == null || !definitionFragment.isVisible) {
                val transaction = fragmentManager!!.beginTransaction()
                transaction.replace(R.id.container_body,
                                    DefinitionFragment())
                transaction.commit()
            }
        }
    }

    private fun setRelatedButtonOnClick(view: View){
        val relatedButton = view.findViewById<TextView>(R.id.btn_tab_related)
        relatedButton.setOnClickListener {
            val relatedFragment = fragmentManager!!.findFragmentById(R.id.fragment_related)
            if(relatedFragment == null || !relatedFragment.isVisible){
                val transaction = fragmentManager!!.beginTransaction()
                transaction.replace(R.id.container_body,
                                    RelatedFragment())
                transaction.commit()
            }
        }
    }
}

