package com.waifusims.wanicchou.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.waifusims.wanicchou.R
import com.waifusims.wanicchou.ui.adapter.TextBlockRecyclerViewAdapter
import com.waifusims.wanicchou.util.InputAlertDialogBuilder
import com.waifusims.wanicchou.viewmodel.VocabularyNoteViewModel
import com.waifusims.wanicchou.viewmodel.VocabularyViewModel
import data.room.VocabularyRepository
import data.room.entity.VocabularyNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class VocabularyNoteFragment : TextBlockFragment("Vocabulary Notes") {
    companion object {
        private val TAG : String = VocabularyNoteFragment::class.java.simpleName
    }

    private val repository : VocabularyRepository by lazy {
        VocabularyRepository(activity!!.application)
    }

    private val vocabularyViewModel : VocabularyViewModel by lazy {
        ViewModelProviders.of(activity!!)
                .get(VocabularyViewModel::class.java)
    }

    private val notesViewModel : VocabularyNoteViewModel by lazy {
        //TODO: Make sure this assert isn't problematic
        ViewModelProviders.of(activity!!)
                .get(VocabularyNoteViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        setRelatedObserver(view)
        setAddTagButtonOnClick(view)
        return view
    }

    private fun setRelatedObserver(view : View){
        val context = context
        val lifecycleOwner : LifecycleOwner = context as LifecycleOwner
        val activity = activity!!
        vocabularyViewModel.setObserver(lifecycleOwner){
            runBlocking (Dispatchers.IO){
                val dbNotes = repository.getVocabularyNotes(vocabularyViewModel.vocabulary.vocabularyID)
                activity.runOnUiThread {
                    notesViewModel.value = dbNotes
                }
            }
            val recyclerView = view.findViewById<RecyclerView>(R.id.rv_text_block_contents)
            Log.v(TAG, "LiveData emitted.")
            val notes = notesViewModel.value!!.map{ it.noteText }
            Log.v(TAG, "Result size: [${notes.size}].")
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.SPACE_AROUND
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = TextBlockRecyclerViewAdapter(notes)
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
            val title = "Add Vocabulary Note"
            val message = null
            val dialogBuilder = InputAlertDialogBuilder(context,
                    view as ViewGroup,
                    title,
                    message)
            dialogBuilder.setPositiveButton("Add") { dialog, _ ->
                val tagText = dialogBuilder.input.text
                val vocabularyID = vocabularyViewModel.vocabulary.vocabularyID
                GlobalScope.launch (Dispatchers.IO){
                    repository.addVocabularyNote(tagText.toString(), vocabularyID)
                }
                val tag = VocabularyNote(tagText.toString(), vocabularyID)
                val tags = notesViewModel.value!!.toMutableList()
                tags.add(tag)
                notesViewModel.value = tags
                val recyclerViewAdapter = view.findViewById<RecyclerView>(R.id.rv_text_block_contents)
                        .adapter!! as TextBlockRecyclerViewAdapter
                recyclerViewAdapter.list.add(tagText.toString())
                recyclerViewAdapter.notifyItemInserted(tags.size)
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
