package com.limegrass.wanicchou.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.limegrass.wanicchou.R
import com.limegrass.wanicchou.ui.adapter.DefinitionAdapter
import com.limegrass.wanicchou.viewmodel.DefinitionViewModel
import com.limegrass.wanicchou.viewmodel.VocabularyViewModel
import data.room.VocabularyRepository

class DefinitionFragment : Fragment() {
    companion object {
        private val TAG : String = DefinitionFragment::class.java.simpleName
    }
    private lateinit var definitionViewModel : DefinitionViewModel

    private lateinit var repository : VocabularyRepository

    private lateinit var vocabularyViewModel : VocabularyViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val attachToRoot = false
        return inflater.inflate(R.layout.fragment_definition,
                                    container,
                                    attachToRoot)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = activity!!
        definitionViewModel = ViewModelProviders.of(activity).get(DefinitionViewModel::class.java)
        repository = VocabularyRepository.getInstance(activity.application)
        vocabularyViewModel = ViewModelProviders.of(activity).get(VocabularyViewModel::class.java)
        setDefinitionObserver(view)
        super.onViewCreated(view, savedInstanceState)
    }


    private fun setDefinitionObserver(view: View){
        val lifecycleOwner : LifecycleOwner = this
        definitionViewModel.setObserver(lifecycleOwner){
            val recyclerView = view.findViewById<RecyclerView>(R.id.rv_definitions)
            Log.v(TAG, "LiveData emitted.")
            val definitionList = definitionViewModel.value
            if(!definitionList.isNullOrEmpty()){
                Log.v(TAG, "Result size: [${definitionList.size}].")
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = DefinitionAdapter(definitionList)
            }
        }

    }
}
