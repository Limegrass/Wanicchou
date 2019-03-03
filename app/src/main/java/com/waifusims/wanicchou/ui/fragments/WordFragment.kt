package com.waifusims.wanicchou.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.waifusims.wanicchou.R
import com.waifusims.wanicchou.viewmodel.SearchViewModel
import data.room.entity.VocabularyInformation

class WordFragment : Fragment() {
    companion object {
        private val TAG : String = WordFragment::class.java.simpleName
    }

    private val searchViewModel : SearchViewModel by lazy {
        ViewModelProviders.of(activity!!)
                .get(SearchViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val attachToRoot = false
        val view = inflater.inflate(R.layout.fragment_word,
                container,
                attachToRoot)
        setWordObserver(view)
        return view
    }

    private fun setWordObserver(view : View){
        val tvWord = view.findViewById<TextView>(R.id.tv_word)
        val tvPronunciation = view.findViewById<TextView>(R.id.tv_pronunciation)

        //TODO: Reset the wordIndex on new search
        val wordObserver = Observer<List<VocabularyInformation>>{
            Log.v(TAG, "LiveData emitted.")
            if(it != null && it.isNotEmpty()){
                Log.i(TAG, "Result size: [${it.size}].")
                tvWord.text = it[searchViewModel.getWordIndex()].vocabulary!!.word
                tvPronunciation.text = it[searchViewModel.getWordIndex()].vocabulary!!.pronunciation
            }
        }

        val lifecycleOwner : LifecycleOwner = activity as LifecycleOwner
        searchViewModel.setVocabularyInformationObserver(lifecycleOwner, wordObserver)
    }
}

