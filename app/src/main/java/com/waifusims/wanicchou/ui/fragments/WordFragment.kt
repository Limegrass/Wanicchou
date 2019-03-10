package com.waifusims.wanicchou.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import com.waifusims.wanicchou.R
import com.waifusims.wanicchou.viewmodel.VocabularyViewModel

class WordFragment : Fragment() {
    companion object {
        private val TAG : String = WordFragment::class.java.simpleName
    }

    private val vocabularyViewModel : VocabularyViewModel by lazy {
        ViewModelProviders.of(activity!!)
                          .get(VocabularyViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val attachToRoot = false
        val view = inflater.inflate(R.layout.fragment_word,
                container,
                attachToRoot)
        setVocabularyListObserver(view)
        return view
    }

    private fun setVocabularyListObserver(view : View?){
        //TODO: Reset the wordIndex on new search
        val lifecycleOwner : LifecycleOwner = activity as LifecycleOwner
        vocabularyViewModel.setObserver(lifecycleOwner, ::setHeader, view)
    }

    private fun setHeader(view : View?){
        val tvWord = view!!.findViewById<TextView>(R.id.tv_word)
        val tvPronunciation = view.findViewById<TextView>(R.id.tv_pronunciation)
        tvWord.text = vocabularyViewModel.vocabulary.word
        tvPronunciation.text = vocabularyViewModel.vocabulary.pronunciation
    }
}

