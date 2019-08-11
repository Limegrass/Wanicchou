package com.limegrass.wanicchou.ui.fragments

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
import com.limegrass.wanicchou.ui.adapter.TextBlockRecyclerViewAdapter
import com.limegrass.wanicchou.viewmodel.DictionaryEntryViewModel

class DefinitionFragment : Fragment() {
    companion object {
        private val TAG : String = DefinitionFragment::class.java.simpleName
    }

    private lateinit var dictionaryEntryViewModel : DictionaryEntryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val attachToRoot = false
        return inflater.inflate(R.layout.fragment_definition,
                                container,
                                attachToRoot)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = activity!!
        dictionaryEntryViewModel = ViewModelProviders.of(activity).get(DictionaryEntryViewModel::class.java)
        setDefinitionObserver(view)
        super.onViewCreated(view, savedInstanceState)
    }


    private fun setDefinitionObserver(view: View){
        val lifecycleOwner : LifecycleOwner = this
        dictionaryEntryViewModel.setObserver(lifecycleOwner){
            val recyclerView = view.findViewById<RecyclerView>(R.id.rv_definitions)
            Log.v(TAG, "LiveData emitted.")
            val dictionaryEntry = dictionaryEntryViewModel.value
            if (dictionaryEntry != null){
                val definitionList = dictionaryEntry.definitions.map {
                    it.definitionText
                }
                if(definitionList.isNotEmpty()){
                    Log.v(TAG, "Result size: [${definitionList.size}].")
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.adapter = TextBlockRecyclerViewAdapter(definitionList)
                }
            }
        }
    }
}
