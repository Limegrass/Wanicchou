package com.waifusims.wanicchou.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.waifusims.wanicchou.R
import com.waifusims.wanicchou.ui.adapter.DefinitionAdapter
import com.waifusims.wanicchou.viewmodel.DefinitionViewModel
import com.waifusims.wanicchou.viewmodel.VocabularyViewModel
import data.room.entity.Definition
import data.room.entity.VocabularyInformation

class DefinitionFragment : Fragment() {
    companion object {
        private val TAG : String = DefinitionFragment::class.java.simpleName
    }
    private val definitionViewModel : DefinitionViewModel by lazy {
        //TODO: Make sure this assert isn't problematic
        ViewModelProviders.of(activity!!)
                .get(DefinitionViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val attachToRoot = false
        val view = inflater.inflate(R.layout.fragment_definition,
                                    container,
                                    attachToRoot)
        setDefinitionObserver(view)
        return view
    }


    private fun setDefinitionObserver(view: View){
        val lifecycleOwner : LifecycleOwner = activity as LifecycleOwner
        definitionViewModel.setObserver(lifecycleOwner){
            val recyclerView = view.findViewById<RecyclerView>(R.id.rv_definitions)
            Log.v(TAG, "LiveData emitted.")
            val definitionList = definitionViewModel.list
            if(!definitionList.isNullOrEmpty()){
                Log.v(TAG, "Result size: [${definitionList.size}].")
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = DefinitionAdapter(definitionList)
            }
        }
    }
}
