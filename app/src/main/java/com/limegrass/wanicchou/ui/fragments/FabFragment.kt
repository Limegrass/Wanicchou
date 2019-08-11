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
import com.limegrass.wanicchou.enums.AutoDelete
import com.limegrass.wanicchou.util.WanicchouSharedPreferenceHelper
import com.limegrass.wanicchou.viewmodel.*
import data.anki.AnkiDroidHelper
import data.arch.models.IDictionaryEntry
import data.arch.search.SearchRequest
import data.arch.util.IRepository
import data.room.database.WanicchouDatabase
import data.room.repository.DictionaryEntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FabFragment : Fragment() {
    private lateinit var floatingActionButton : FloatingActionButton

    private val ankiDroidHelper : AnkiDroidHelper by lazy {
        AnkiDroidHelper(parentContext)
    }
    companion object {
        private const val ANKI_PERMISSION_REQUEST_CALLBACK_CODE : Int = 420
    }

    private val sharedPreferences : WanicchouSharedPreferenceHelper by lazy {
        WanicchouSharedPreferenceHelper(parentContext)
    }
    private val repository : IRepository<IDictionaryEntry, SearchRequest> by lazy {
        val database = WanicchouDatabase(parentContext)
        DictionaryEntryRepository(database)
    }

    private val dictionaryEntryViewModel : DictionaryEntryViewModel by lazy {
        ViewModelProviders.of(parentActivity)
                          .get(DictionaryEntryViewModel::class.java)
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
            val dictionaryEntry = dictionaryEntryViewModel.value
            if(dictionaryEntry != null){
                val definition = dictionaryEntry.definitions[0]
                val vocabulary = dictionaryEntry.vocabulary
                val dictionaryName = definition.dictionary.dictionaryName
                val notes = getNotes()
                val tags = tagViewModel.value!!.map{ it.tag }.toSet()
                val wordLangaugeCode = vocabulary.language.languageCode
                val definitionLanguageCode = definition.language.languageCode
                ankiDroidHelper.addUpdateNote(vocabulary.word,
                        vocabulary.pronunciation,
                        vocabulary.pitch,
                        wordLangaugeCode,
                        definition.definitionText,
                        definitionLanguageCode,
                        dictionaryName,
                        notes,
                        tags)
                val word = vocabulary.word
                val message = getString(R.string.anki_added_toast, word)
                Toast.makeText(context,
                        message,
                        Toast.LENGTH_LONG).show()

                if (sharedPreferences.autoDelete == AutoDelete.ON_ANKI_IMPORT){
                    GlobalScope.launch (Dispatchers.IO) {
                        repository.delete(dictionaryEntry)
                    }
                }
            }
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
        dictionaryEntryViewModel.setObserver(lifecycleOwner){
            if(ankiDroidHelper.isApiAvailable()) {
                floatingActionButton.show()
            }
        }
    }
}