package com.limegrass.wanicchou.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.limegrass.wanicchou.R
import com.limegrass.wanicchou.ui.adapter.TextBlockRecyclerViewAdapter
import com.limegrass.wanicchou.util.InputAlertDialogBuilder
import com.limegrass.wanicchou.viewmodel.DefinitionNoteViewModel
import com.limegrass.wanicchou.viewmodel.DictionaryEntryViewModel
import data.arch.models.IDefinition
import data.arch.models.INote
import data.arch.util.IRepository
import data.models.Note
import data.room.database.WanicchouDatabase
import data.room.repository.DefinitionNoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DefinitionNoteFragment : TextBlockFragment() {
    private lateinit var parentFragmentActivity : FragmentActivity

    private val repository : IRepository<INote<IDefinition>, IDefinition> by lazy {
        val database = WanicchouDatabase(parentFragmentActivity.application)
        DefinitionNoteRepository(database)
    }

    private val dictionaryEntryViewModel : DictionaryEntryViewModel by lazy {
        ViewModelProviders.of(parentFragmentActivity)
                .get(DictionaryEntryViewModel::class.java)
    }

    private val notesViewModel : DefinitionNoteViewModel by lazy {
        //TODO: Make sure this assert isn't problematic
        ViewModelProviders.of(parentFragmentActivity)
                .get(DefinitionNoteViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        title = getString(R.string.lbl_fragment_definition_note_title)
        parentFragmentActivity = activity!!
        setRelatedObserver(view)
        setAddTagButtonOnClick(view)
        GlobalScope.launch(Dispatchers.IO){
            refreshNotesViewModel()
        }
        return view
    }

    private suspend fun refreshNotesViewModel(){
        val dictionaryEntry = dictionaryEntryViewModel.value
        if(dictionaryEntry != null) {
            val dbNotes = repository.search(dictionaryEntry.definitions[0])
            parentFragmentActivity.runOnUiThread {
                notesViewModel.value = dbNotes
            }
        }
    }

    private fun setRelatedObserver(view : View){
        val context = context!!
        val lifecycleOwner : LifecycleOwner = this
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_text_block_contents)
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.SPACE_AROUND
        recyclerView.layoutManager = layoutManager

        val onClickListener = View.OnClickListener { v ->
            Log.v(TAG, "OnClick")
            val position = recyclerView.getChildLayoutPosition(v!!)
            val note = notesViewModel.value!![position]
            val title = getString(R.string.edit_note_title)

            val dialogBuilder = InputAlertDialogBuilder(context,
                    view as ViewGroup,
                    title,
                    "" )

            dialogBuilder.input.setText(note.noteText)
            dialogBuilder.setPositiveButton(getString(R.string.save_button_text)){ dialog, _ ->
                val updatedNote = Note(note.topic, dialogBuilder.input.text.toString())
                GlobalScope.launch(Dispatchers.IO){
                    repository.update(note, updatedNote)
//                    Not taking advantage of RecyclerView animations, but it simplifies the code.
                    refreshNotesViewModel()
                }
                dialog.dismiss()
            }

            dialogBuilder.setNeutralButton(getString(R.string.delete_button_text)){ dialog, _ ->
                GlobalScope.launch(Dispatchers.IO) {
                    repository.delete(note)
                    refreshNotesViewModel()
                }
                dialog.dismiss()
            }

            dialogBuilder.input.setSingleLine(false)
            dialogBuilder.input.setLines(1)
            dialogBuilder.input.maxLines = 5
            dialogBuilder.input.gravity = (Gravity.START.or(Gravity.TOP))
            dialogBuilder.input.setHorizontallyScrolling(false)
            dialogBuilder.show()
        }

        dictionaryEntryViewModel.setObserver(lifecycleOwner){
            GlobalScope.launch(Dispatchers.IO){
                refreshNotesViewModel()
            }
        }

        notesViewModel.setObserver(lifecycleOwner){
            val notes = notesViewModel.value!!.map{ it.noteText }
            Log.v(TAG, "Result size: [${notes.size}].")
            recyclerView.adapter = TextBlockRecyclerViewAdapter(notes, onClickListener)
        }
    }

    private fun setAddTagButtonOnClick(view : View) {
        val context = context!!
        view.findViewById<AppCompatImageButton>(R.id.iv_btn_add).setOnClickListener {
            val title = getString(R.string.add_definition_note_title)
            val message = null
            val dialogBuilder = InputAlertDialogBuilder(context,
                    view as ViewGroup,
                    title,
                    message)
            dialogBuilder.setPositiveButton(getString(R.string.add_button_text)) { dialog, _ ->
                val dictionaryEntry = dictionaryEntryViewModel.value
                if(dictionaryEntry != null) {
                    val tagText = dialogBuilder.input.text.toString()
                    val definition = dictionaryEntry.definitions[0]
                    GlobalScope.launch (Dispatchers.IO){
                        val note = Note(definition, tagText)
                        repository.insert(note)
                        refreshNotesViewModel()
                    }
                }
                dialog.dismiss()
            }
            dialogBuilder.input.setSingleLine(false)
            dialogBuilder.input.setLines(1)
            dialogBuilder.input.maxLines = 5
            dialogBuilder.input.gravity = (Gravity.START.or(Gravity.TOP))
            dialogBuilder.input.setHorizontallyScrolling(false)
            dialogBuilder.show()
        }
    }

    companion object {
        private val TAG : String = DefinitionNoteFragment::class.java.simpleName
    }
}
