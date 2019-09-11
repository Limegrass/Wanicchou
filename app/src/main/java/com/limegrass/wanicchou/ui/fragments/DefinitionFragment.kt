package com.limegrass.wanicchou.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.limegrass.wanicchou.R
import com.limegrass.wanicchou.ui.adapter.TextBlockRecyclerViewAdapter
import com.limegrass.wanicchou.util.InputAlertDialogBuilder
import com.limegrass.wanicchou.viewmodel.DictionaryEntryViewModel
import data.architecture.IRepository
import data.enums.MatchType
import data.models.Definition
import data.models.DictionaryEntry
import data.models.IDictionaryEntry
import data.search.SearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import room.database.WanicchouDatabase
import room.repository.DictionaryEntryRepository

class DefinitionFragment : Fragment() {
    companion object {
        private val TAG : String = DefinitionFragment::class.java.simpleName
    }

    private lateinit var parentFragmentActivity : FragmentActivity
    private val repository : IRepository<IDictionaryEntry, SearchRequest> by lazy {
        val database = WanicchouDatabase(parentFragmentActivity.application)
        DictionaryEntryRepository(database)
    }

    private lateinit var dictionaryEntryViewModel : DictionaryEntryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val attachToRoot = false
        return inflater.inflate(R.layout.fragment_definition,
                                container,
                                attachToRoot)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        parentFragmentActivity = activity!!
        dictionaryEntryViewModel = ViewModelProviders.of(parentFragmentActivity)
                .get(DictionaryEntryViewModel::class.java)
        setDefinitionObserver(view)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setDefinitionObserver(view: View){
        val lifecycleOwner : LifecycleOwner = this
        val context = context!!
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_definitions)
        val onClickListener = View.OnClickListener { v ->
            Log.v(TAG, "OnClick")
            val position = recyclerView.getChildLayoutPosition(v!!)
            val selectedEntry = dictionaryEntryViewModel.value!!
            val title = getString(R.string.edit_definition_title)

            val dialogBuilder = InputAlertDialogBuilder(context,
                    view as ViewGroup,
                    title,
                    "" )
            val selectedDefinition = selectedEntry.definitions[position]

            dialogBuilder.input.setText(selectedDefinition.definitionText)
            dialogBuilder.setPositiveButton(getString(R.string.save_button_text)){ dialog, _ ->
                val updatedDefinitionText = dialogBuilder.input.text.toString()
                if(updatedDefinitionText != selectedDefinition.definitionText){
                    val updatedDefinitions = selectedEntry.definitions.map {
                        if (it == selectedDefinition) {
                            Definition(updatedDefinitionText,
                                    it.language,
                                    it.dictionary)
                        }
                        else {
                            it
                        }
                    }

                    val updatedEntry = DictionaryEntry(selectedEntry.vocabulary,
                            updatedDefinitions)
                    GlobalScope.launch(Dispatchers.IO){
                        runBlocking {
                            repository.update(selectedEntry, updatedEntry)
                        }
                        val searchRequest = SearchRequest(dialogBuilder.input.text.toString(),
                                selectedEntry.vocabulary.language,
                                selectedDefinition.language,
                                MatchType.DEFINITION_CONTAINS)
                        val updatedEntity = repository.search(searchRequest)

                        parentFragmentActivity.runOnUiThread {
                            dictionaryEntryViewModel.availableDictionaryEntries = updatedEntity
                        }
                    }
                }
                dialog.dismiss()
            }

            dialogBuilder.input.setSingleLine(false)
            dialogBuilder.input.setLines(1)
            dialogBuilder.input.maxLines = 5
            dialogBuilder.input.gravity = (Gravity.START or Gravity.TOP)
            dialogBuilder.input.setHorizontallyScrolling(false)
            dialogBuilder.show()
        }
        dictionaryEntryViewModel.setObserver(lifecycleOwner){
            Log.v(TAG, "LiveData emitted.")
            val dictionaryEntry = dictionaryEntryViewModel.value
            if (dictionaryEntry != null){
                val definitionList = dictionaryEntry.definitions.map {
                    it.definitionText
                }
                if(definitionList.isNotEmpty()){
                    Log.v(TAG, "Result size: [${definitionList.size}].")
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.adapter = TextBlockRecyclerViewAdapter(definitionList, onClickListener)
                }
            }
        }
    }
}
