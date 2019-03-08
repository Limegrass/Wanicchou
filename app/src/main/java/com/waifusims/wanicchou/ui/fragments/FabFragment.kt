package com.waifusims.wanicchou.ui.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.waifusims.wanicchou.R
import com.waifusims.wanicchou.viewmodel.VocabularyViewModel
import data.arch.anki.AnkiDroidHelper
import data.room.entity.Dictionary
import data.room.entity.VocabularyInformation

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

    override fun onResume() {
//        if(vocabularyViewModel.definition.definitionID == 0L){
//            floatingActionButton.hide()
//        }

        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val attachToRoot = false
        val view = inflater.inflate(R.layout.floating_action_button,
                container,
                attachToRoot)
        floatingActionButton = view.findViewById(R.id.fab)
        dictionaries = arguments!!.getParcelableArrayList<Dictionary>(DICTIONARIES_BUNDLE_KEY)!!
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
//            val dictionaryName = dictionaries.single {
//                it.dictionaryID == vocabularyViewModel.definition.dictionaryID
//            }.dictionaryName

            //TODO: Properly include the notes and tags
//            ankiDroidHelper.addUpdateNote(vocabularyViewModel.vocabulary,
//                    vocabularyViewModel.definition,
//                    dictionaryName,
//                    listOf(),
//                    mutableSetOf())
        }
    }
    private fun setFABObserver(){
        val wordObserver = Observer<List<VocabularyInformation>> {
            Log.v(TAG, "LiveData emitted.")
            if(it != null
                    && it.isNotEmpty()
                    && ankiDroidHelper.isApiAvailable()) {
                Log.v(TAG, "Result size: [${it.size}].")
                floatingActionButton.show()
            }
        }
        val lifecycleOwner = this
//        vocabularyViewModel.setVocabularyObserver(lifecycleOwner, wordObserver)
    }
}
