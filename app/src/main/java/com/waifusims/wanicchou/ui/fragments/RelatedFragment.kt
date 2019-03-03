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
import com.waifusims.wanicchou.ui.adapter.RelatedVocabularyAdapter
import com.waifusims.wanicchou.viewmodel.SearchViewModel
import data.room.entity.VocabularyInformation

class RelatedFragment : Fragment() {
    companion object {
        private val TAG : String = RelatedFragment::class.java.simpleName
    }
    private val searchViewModel : SearchViewModel by lazy {
        //TODO: Make sure this assert isn't problematic
        ViewModelProviders.of(activity!!)
                .get(SearchViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val attachToRoot = false
        val view = inflater.inflate(R.layout.fragment_related,
                container,
                attachToRoot)
        setRelatedObserver(view)
        return view
    }

    private fun setRelatedObserver(view : View){
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_related)
        val definitionObserver = Observer<List<VocabularyInformation>>{
            Log.v(TAG, "LiveData emitted.")
            if(it != null && it.isNotEmpty()){
                Log.v(TAG, "Result size: [${it.size}].")
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = RelatedVocabularyAdapter(searchViewModel.relatedWords)
            }
        }

        val lifecycleOwner : LifecycleOwner = activity as LifecycleOwner
        searchViewModel.setRelatedWordObserver(lifecycleOwner, definitionObserver)
    }

}