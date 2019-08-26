package com.limegrass.wanicchou.ui.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ichi2.anki.FlashCardsContract
import com.limegrass.wanicchou.R
import com.limegrass.wanicchou.enums.AutoDelete
import com.limegrass.wanicchou.util.AnkiDroidHelper
import com.limegrass.wanicchou.util.WanicchouSharedPreferenceHelper
import com.limegrass.wanicchou.util.WanicchouToast
import com.limegrass.wanicchou.viewmodel.DefinitionNoteViewModel
import com.limegrass.wanicchou.viewmodel.DictionaryEntryViewModel
import com.limegrass.wanicchou.viewmodel.TagViewModel
import com.limegrass.wanicchou.viewmodel.VocabularyNoteViewModel
import data.anki.AnkiDroidApi
import data.anki.AnkiDroidConfig
import data.anki.IAnkiDroidApi
import data.anki.WanicchouAnkiEntry
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

    private val ankiDroidApi : IAnkiDroidApi by lazy {
        AnkiDroidApi(parentContext)
    }

    private val ankiDroidHelper : AnkiDroidHelper by lazy {
        AnkiDroidHelper(ankiDroidApi, AnkiDroidConfig)
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

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        when(requestCode){
            AnkiDroidApi.ANKI_READ_WRITE_PERMISSION_CALLBACK_CODE -> {
                if(grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val dictionaryEntry = dictionaryEntryViewModel.value
                    if (dictionaryEntry != null) {
                        val ankiEntry = WanicchouAnkiEntry(dictionaryEntry.vocabulary,
                                dictionaryEntry.definitions[0],
                                getNotes())
                        val tags = tagViewModel.value!!.map { it.tag }.toSet()
                        ankiDroidHelper.addUpdateNote(ankiEntry, tags)
                        if (sharedPreferences.autoDelete == AutoDelete.ON_ANKI_IMPORT) {
                            GlobalScope.launch(Dispatchers.IO) {
                                repository.delete(dictionaryEntry)
                            }
                        }
                    }
                }
                else {
                    WanicchouToast.toast?.cancel()
                    val message = (getString(R.string.permissions_denied_toast))
                    WanicchouToast.toast = Toast.makeText(parentContext, message, Toast.LENGTH_LONG)
                    WanicchouToast.toast!!.show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun setFABOnClick() {
        floatingActionButton.setOnClickListener {
            val dictionaryEntry = dictionaryEntryViewModel.value
            if(dictionaryEntry != null){
                val ankiEntry = WanicchouAnkiEntry(dictionaryEntry.vocabulary,
                                                   dictionaryEntry.definitions[0],
                                                   getNotes())
                val tags = tagViewModel.value!!.map{ it.tag }.toSet()
                if(!ankiDroidApi.hasAnkiReadWritePermission){
                    requestPermissions(arrayOf(FlashCardsContract.READ_WRITE_PERMISSION),
                            AnkiDroidApi.ANKI_READ_WRITE_PERMISSION_CALLBACK_CODE)
                }
                else {
                    ankiDroidHelper.addUpdateNote(ankiEntry, tags)
                    if (sharedPreferences.autoDelete == AutoDelete.ON_ANKI_IMPORT){
                        GlobalScope.launch (Dispatchers.IO) {
                            repository.delete(dictionaryEntry)
                        }
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
            if(ankiDroidApi.hasAvailableApi) {
                floatingActionButton.show()
            }
        }
    }
}