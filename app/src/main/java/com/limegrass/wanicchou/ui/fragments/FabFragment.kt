package com.limegrass.wanicchou.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.limegrass.wanicchou.R
import com.limegrass.wanicchou.viewmodel.DefinitionViewModel
import com.limegrass.wanicchou.viewmodel.VocabularyViewModel
import data.arch.anki.AnkiDroidHelper
import data.room.VocabularyRepository

class FabFragment : Fragment() {
    private lateinit var floatingActionButton : FloatingActionButton

    private val ankiDroidHelper : AnkiDroidHelper by lazy {
        AnkiDroidHelper(context!!, repository)
    }
    companion object {
        private const val ANKI_PERMISSION_REQUEST_CALLBACK_CODE : Int = 420
    }

    private val repository : VocabularyRepository by lazy {
        VocabularyRepository.getInstance(activity!!.application)
    }

    private val vocabularyViewModel : VocabularyViewModel by lazy {
        ViewModelProviders.of(activity!!)
                          .get(VocabularyViewModel::class.java)
    }

    private val definitionViewModel : DefinitionViewModel by lazy {
        ViewModelProviders.of(activity!!)
                          .get(DefinitionViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
                val callbackActivity = activity!!
                ankiDroidHelper.requestPermission(callbackActivity,
                        ANKI_PERMISSION_REQUEST_CALLBACK_CODE)
            }
            val definition = definitionViewModel.definition

            val dictionaryName = repository.dictionaries.single {
                    it.dictionaryID == definition.dictionaryID
            }.dictionaryName

//            TODO: Properly include the notes and tags
            ankiDroidHelper.addUpdateNote(vocabularyViewModel.vocabulary,
                    definition,
                    dictionaryName,
                    listOf(),
                    mutableSetOf())
            //TODO: Use string resource
            Toast.makeText(context,
                          "${vocabularyViewModel.vocabulary.word} sent to AnkiDroid",
                          Toast.LENGTH_LONG).show()

        }
    }
    private fun setFABObserver(){
        val lifecycleOwner = this
        vocabularyViewModel.setObserver(lifecycleOwner){
            if(!definitionViewModel.value.isNullOrEmpty()
                    && definitionViewModel.value!![0].vocabularyID != 0L
                    && ankiDroidHelper.isApiAvailable()) {
                floatingActionButton.show()
            }
        }
    }
}
