package com.limegrass.wanicchou.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import com.limegrass.wanicchou.R
import com.limegrass.wanicchou.util.cancelSetAndShowWanicchouToast
import com.limegrass.wanicchou.viewmodel.DictionaryEntryViewModel

class WordFragment : Fragment() {
    companion object {
        private val TAG : String = WordFragment::class.java.simpleName
    }

    private val dictionaryEntryViewModel : DictionaryEntryViewModel by lazy {
        ViewModelProviders.of(activity!!)
                          .get(DictionaryEntryViewModel::class.java)
    }
    private lateinit var parentContext : Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val attachToRoot = false
        return inflater.inflate(R.layout.fragment_word,
                container,
                attachToRoot)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setVocabularyListObserver(view)
        setOnClickListener(view)
        parentContext = context!!
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setOnClickListener(view: View){
        val tvPronunciation = view.findViewById<TextView>(R.id.tv_pronunciation)
        tvPronunciation.setOnClickListener {
            val dictionaryEntry = dictionaryEntryViewModel.value
            if (dictionaryEntry != null){
                val toastText = getString(R.string.toast_pitch, dictionaryEntry.vocabulary.pitch)
                cancelSetAndShowWanicchouToast(parentContext, toastText, Toast.LENGTH_SHORT)
            }

        }
    }

    private fun setVocabularyListObserver(view : View?){
        //TODO: Reset the wordIndex on new search
        val lifecycleOwner : LifecycleOwner = this
        dictionaryEntryViewModel.setObserver(lifecycleOwner){
            val tvWord = view!!.findViewById<TextView>(R.id.tv_word)
            val tvPronunciation = view.findViewById<TextView>(R.id.tv_pronunciation)
            val dictionaryEntry = dictionaryEntryViewModel.value
            if (dictionaryEntry != null){
                tvWord.text = dictionaryEntry.vocabulary.word
                tvPronunciation.text = dictionaryEntry.vocabulary.pronunciation
            }
        }
    }
}

