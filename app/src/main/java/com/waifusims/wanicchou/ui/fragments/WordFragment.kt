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
import data.room.VocabularyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WordFragment : Fragment() {
    companion object {
        private val TAG : String = WordFragment::class.java.simpleName
    }

    private val repository : VocabularyRepository by lazy {
        VocabularyRepository(activity!!.application)
    }

    private val vocabularyViewModel : VocabularyViewModel by lazy {
        ViewModelProviders.of(activity!!)
                          .get(VocabularyViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val attachToRoot = false
        return inflater.inflate(R.layout.fragment_word,
                container,
                attachToRoot)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setVocabularyListObserver(view)
        getLatest()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getLatest(){
        GlobalScope.launch(Dispatchers.IO) {
            val vocabularyList = repository.getLatest()
            //TODO: Maybe refactor to just give the DICTIONARY_ID
            // (or when I figure out dynamic settings pref)
            activity!!.runOnUiThread {
                vocabularyViewModel.value = vocabularyList
            }
        }
    }

    private fun setVocabularyListObserver(view : View?){
        //TODO: Reset the wordIndex on new search
        val lifecycleOwner : LifecycleOwner = context as LifecycleOwner
        vocabularyViewModel.setObserver(lifecycleOwner){
            val tvWord = view!!.findViewById<TextView>(R.id.tv_word)
            val tvPronunciation = view.findViewById<TextView>(R.id.tv_pronunciation)
            tvWord.text = vocabularyViewModel.vocabulary.word
            tvPronunciation.text = vocabularyViewModel.vocabulary.pronunciation
        }
    }
}

