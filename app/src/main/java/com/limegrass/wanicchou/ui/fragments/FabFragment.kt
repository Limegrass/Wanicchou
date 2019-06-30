package com.limegrass.wanicchou.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.limegrass.wanicchou.R
import com.limegrass.wanicchou.viewmodel.*
import data.arch.anki.AnkiDroidHelper
import data.room.VocabularyRepository

class FabFragment : Fragment() {
    private lateinit var floatingActionButton : FloatingActionButton

    private val ankiDroidHelper : AnkiDroidHelper by lazy {
        AnkiDroidHelper(parentContext)
    }
    companion object {
        private const val ANKI_PERMISSION_REQUEST_CALLBACK_CODE : Int = 420
    }

    private val repository : VocabularyRepository by lazy {
        VocabularyRepository.getInstance(parentActivity.application)
    }

    private val vocabularyViewModel : VocabularyViewModel by lazy {
        ViewModelProviders.of(parentActivity)
                          .get(VocabularyViewModel::class.java)
    }

    private val definitionViewModel : DefinitionViewModel by lazy {
        ViewModelProviders.of(parentActivity)
                          .get(DefinitionViewModel::class.java)
    }
    private val vocabularyNoteViewModel : VocabularyNoteViewModel by lazy {
        ViewModelProviders.of(parentActivity)
                .get(VocabularyNoteViewModel::class.java)
    }
    private val definitionNoteViewModel : DefinitionNoteViewModel by lazy {
        ViewModelProviders.of(parentActivity)
                .get(DefinitionNoteViewModel::class.java)
    }
    private val tagViewModel : TagViewModel by lazy {
        ViewModelProviders.of(parentActivity)
                .get(TagViewModel::class.java)
    }

    private lateinit var parentActivity : FragmentActivity
    private lateinit var parentContext : Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parentActivity = activity!!
        parentContext = context!!
        val attachToRoot = false
        val view = inflater.inflate(R.layout.floating_action_button,
                container,
                attachToRoot)
        floatingActionButton = view.findViewById(R.id.fab)
        setFABOnClick()
        setFABObserver()
        return view
    }

    private fun setFABOnClick() {
        floatingActionButton.setOnClickListener {
            if(ankiDroidHelper.shouldRequestPermission()){
                ankiDroidHelper.requestPermission(parentActivity,
                        ANKI_PERMISSION_REQUEST_CALLBACK_CODE)
            }
            val vocabulary = vocabularyViewModel.vocabulary
            val definition = definitionViewModel.definition
            val dictionaryName = repository.dictionaries.single {
                    it.dictionaryID == definition.dictionaryID
            }.dictionaryName
            val notes = getNotes()
            val tags = tagViewModel.value!!.map{ it.tagText }.toSet()
            val wordLangaugeCode = repository.languages.single{
                it.languageID == vocabulary.languageID
            }.languageCode
            val definitionLanguageCode = repository.languages.single{
                it.languageID == definition.languageID
            }.languageCode
            ankiDroidHelper.addUpdateNote(vocabulary.word,
                                          vocabulary.pronunciation,
                                          vocabulary.pitch,
                                          wordLangaugeCode,
                                          definition.definitionText,
                                          definitionLanguageCode,
                                          dictionaryName,
                                          notes,
                                          tags)
            val word = vocabularyViewModel.vocabulary.word
            val message = getString(R.string.anki_added_toast, word)
            Toast.makeText(context,
                          message,
                          Toast.LENGTH_LONG).show()
        }
    }
    private fun getNotes() : List<String>{
        val notes = vocabularyNoteViewModel.value!!.map{
            it.noteText
        }.toMutableList()
        notes.addAll(definitionNoteViewModel.value!!.map{ it.noteText })
        return notes
    }
    private fun setFABObserver(){
        val lifecycleOwner = this
        vocabularyViewModel.setObserver(lifecycleOwner){
            if(!vocabularyViewModel.value.isNullOrEmpty()
                    && ankiDroidHelper.isApiAvailable()) {
                floatingActionButton.show()
            }
        }
    }
}
