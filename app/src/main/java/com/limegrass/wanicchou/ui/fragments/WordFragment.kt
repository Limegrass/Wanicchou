package com.limegrass.wanicchou.ui.fragments

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
import com.limegrass.wanicchou.util.WanicchouSharedPreferenceHelper
import com.limegrass.wanicchou.viewmodel.DefinitionViewModel
import com.limegrass.wanicchou.viewmodel.VocabularyViewModel
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

    private lateinit var definitionViewModel : DefinitionViewModel

    private val sharedPreferenceHelper : WanicchouSharedPreferenceHelper by lazy {
        WanicchouSharedPreferenceHelper(context!!)
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
        definitionViewModel = ViewModelProviders.of(activity!!).get(DefinitionViewModel::class.java)
        setVocabularyListObserver(view)
        getLastSearched()
        setOnClickListener(view)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setOnClickListener(view: View){
        val tvPronunciation = view.findViewById<TextView>(R.id.tv_pronunciation)
        val context = context
        tvPronunciation.setOnClickListener {
            Toast.makeText(context,
                           "Pitch: ${vocabularyViewModel.vocabulary.pitch}",
                           Toast.LENGTH_SHORT).show()

        }
    }

    private fun getLastSearched(){
        val activity = activity!!
        GlobalScope.launch(Dispatchers.IO) {
            val vocabularyList = repository.getVocabulary(sharedPreferenceHelper.lastSearchedVocabularyID)
            //TODO: Maybe refactor to just give the DICTIONARY_ID
            // (or when I figure out dynamic settings pref)
            activity.runOnUiThread {
                vocabularyViewModel.value = vocabularyList
            }
        }
    }

    private fun setVocabularyListObserver(view : View?){
        //TODO: Reset the wordIndex on new search
        val activity = activity!!
        val lifecycleOwner : LifecycleOwner = this
        vocabularyViewModel.setObserver(lifecycleOwner){
            val tvWord = view!!.findViewById<TextView>(R.id.tv_word)
            val tvPronunciation = view.findViewById<TextView>(R.id.tv_pronunciation)
            val vocabulary = vocabularyViewModel.vocabulary
            tvWord.text = vocabulary.word
            tvPronunciation.text = vocabulary.pronunciation
        }

        vocabularyViewModel.setObserver(lifecycleOwner){
            GlobalScope.launch(Dispatchers.IO) {
                val vocabularyID = vocabularyViewModel.vocabulary.vocabularyID
                val definition = repository.getDefinition(vocabularyID,
                        sharedPreferenceHelper.definitionLanguageID,
                        sharedPreferenceHelper.dictionary)
                if(definition != null){
                    activity.runOnUiThread{
                        definitionViewModel.value = listOf(definition)
                    }
                }
            }
        }
    }
}

