package com.waifusims.wanicchou.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.waifusims.wanicchou.R
import com.waifusims.wanicchou.viewmodel.DefinitionViewModel
import com.waifusims.wanicchou.viewmodel.VocabularyViewModel
import data.arch.anki.AnkiDroidHelper
import data.room.entity.Dictionary

class FabFragment : Fragment() {
    private lateinit var floatingActionButton : FloatingActionButton
    private lateinit var dictionaries : ArrayList<Dictionary>

    private val ankiDroidHelper : AnkiDroidHelper by lazy {
        AnkiDroidHelper(context!!)
    }
    companion object {
        private const val ANKI_PERMISSION_REQUEST_CALLBACK_CODE : Int = 420
        const val DICTIONARIES_BUNDLE_KEY = "key_dictionary" // TODO: Move to strings
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
        dictionaries = arguments!!.getParcelableArrayList<Dictionary>(DICTIONARIES_BUNDLE_KEY)!!
        setFABOnClick()
        setFABObserver(view)
        return view
    }

    private fun setFABOnClick() {
        floatingActionButton.setOnClickListener {
            if(ankiDroidHelper.shouldRequestPermission()){
                val callbackActivity = activity!!
                ankiDroidHelper.requestPermission(callbackActivity,
                        ANKI_PERMISSION_REQUEST_CALLBACK_CODE)
            }

            val dictionaryNames = definitionViewModel.definitionList.map {
                dictionaries.single {
                    it.dictionaryID == it.dictionaryID
                }.dictionaryName
            }

//            TODO: Properly include the notes and tags
            ankiDroidHelper.addUpdateNote(vocabularyViewModel.vocabulary,
                    definitionViewModel.definitionList,
                    dictionaryNames,
                    listOf(),
                    mutableSetOf())
        }
    }
    private fun setFABObserver(view: View?){
        val lifecycleOwner = this
        vocabularyViewModel.setObserver(lifecycleOwner, ::showFAB, view)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun showFAB(view : View?){
        if(!definitionViewModel.definitionList.isNullOrEmpty()
                && definitionViewModel.definitionList[0].vocabularyID != 0L
                && ankiDroidHelper.isApiAvailable()) {
            floatingActionButton.show()
        }
    }
}
