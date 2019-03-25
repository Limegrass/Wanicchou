package com.waifusims.wanicchou.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.waifusims.wanicchou.R
import com.waifusims.wanicchou.ui.adapter.TextBlockRecyclerViewAdapter
import com.waifusims.wanicchou.util.InputAlertDialogBuilderFactory
import com.waifusims.wanicchou.viewmodel.DefinitionNoteViewModel

class DefinitionNoteFragment : Fragment() {
    companion object {
        private val TAG : String = DefinitionNoteFragment::class.java.simpleName
    }

    private val notesViewModel : DefinitionNoteViewModel by lazy {
        //TODO: Make sure this assert isn't problematic
        ViewModelProviders.of(activity!!)
                .get(DefinitionNoteViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val attachToRoot = false
        val view = inflater.inflate(R.layout.fragment_text_block_list,
                container,
                attachToRoot)
        view.findViewById<TextView>(R.id.tv_text_block_label).text = "Definition Notes"

        setRelatedObserver(view)
        setAddTagButtonOnClick(view, container)
        return view
    }

    private fun setRelatedObserver(view : View){
        val lifecycleOwner : LifecycleOwner = activity as LifecycleOwner
        notesViewModel.setObserver(lifecycleOwner, ::setTags, view)
    }

    // Create an observer that is generic. Construct with a TV to change the text of on update,
    // and auto remove and assign to new LiveData if it changes.
    // For things that could change on the fly like RelatedWords and Tags.
    // (Async operations in the background to insert into the DB and the autoupdate)
    // Keep Vocabulary and Definition as they are since they won't change without a search anyways?

    private fun setAddTagButtonOnClick(view : View, container: ViewGroup?) {
        view.findViewById<AppCompatImageButton>(R.id.iv_btn_add).setOnClickListener {
            val context = context!!
            val title = "Add Definition Note"
            val message = null
            val dialogBuilder = InputAlertDialogBuilderFactory(context,
                    view as ViewGroup,
                    title,
                    message).get()

            dialogBuilder.setButton(AlertDialog.BUTTON_POSITIVE, "Add") { dialog, which ->
                dialog.dismiss()
            }
            dialogBuilder.show()
        }
    }

    private fun setTags(view : View?) {
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.rv_text_block_contents)
        Log.v(TAG, "LiveData emitted.")
        val notes = notesViewModel.notes.map {
            it.noteText
        }
        if(!notes.isNullOrEmpty()){
            Log.v(TAG, "Result size: [${notes.size}].")
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.SPACE_AROUND

            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = TextBlockRecyclerViewAdapter(notes)
        }
    }
}
