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
import com.limegrass.wanicchou.viewmodel.DefinitionViewModel
import com.limegrass.wanicchou.viewmodel.VocabularyViewModel
import data.room.VocabularyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DefinitionNoteFragment : TextBlockFragment() {
    companion object {
        private val TAG : String = DefinitionNoteFragment::class.java.simpleName
    }
    private lateinit var parentFragmentActivity : FragmentActivity

    //TODO: Refactor to lateinit
    private val repository : VocabularyRepository by lazy {
        VocabularyRepository(parentFragmentActivity.application)
    }

    private val vocabularyViewModel : VocabularyViewModel by lazy {
        ViewModelProviders.of(parentFragmentActivity)
                .get(VocabularyViewModel::class.java)
    }

    private val definitionViewModel : DefinitionViewModel by lazy {
        ViewModelProviders.of(parentFragmentActivity)
                .get(DefinitionViewModel::class.java)
    }

    private val notesViewModel : DefinitionNoteViewModel by lazy {
        //TODO: Make sure this assert isn't problematic
        ViewModelProviders.of(parentFragmentActivity)
                .get(DefinitionNoteViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        title = getString(R.string.lbl_fragment_definition_note_title)
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        parentFragmentActivity = activity!!
        setRelatedObserver(view)
        setAddTagButtonOnClick(view)
        GlobalScope.launch(Dispatchers.IO){
            refreshNotesViewModel()
        }
        return view
    }

    private fun refreshNotesViewModel(){
        val dbNotes = repository.getDefinitionNotes(definitionViewModel.definition.definitionID)
        parentFragmentActivity.runOnUiThread {
            notesViewModel.value = dbNotes
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
                note.noteText = dialogBuilder.input.text.toString()
                GlobalScope.launch(Dispatchers.IO){
                    repository.updateDefinitionNote(note)
                    //Not taking advantage of RecyclerView animations, but it simplifies the code.
                    refreshNotesViewModel()
                }
                dialog.dismiss()
            }

            dialogBuilder.setNeutralButton(getString(R.string.delete_button_text)){ dialog, _ ->
                GlobalScope.launch(Dispatchers.IO) {
                    repository.deleteDefinitionNote(note)
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

        vocabularyViewModel.setObserver(lifecycleOwner){
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

    // Create an observer that is generic. Construct with a TV to change the text of on update,
    // and auto remove and assign to new LiveData if it changes.
    // For things that could change on the fly like RelatedWords and Tags.
    // (Async operations in the background to insert into the DB and the autoupdate)
    // Keep Vocabulary and Definition as they are since they won't change without a search anyways?

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
                val tagText = dialogBuilder.input.text
                val definitionID = definitionViewModel.definition.definitionID
                GlobalScope.launch (Dispatchers.IO){
                    repository.addDefinitionNote(tagText.toString(), definitionID)
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
    }
}
