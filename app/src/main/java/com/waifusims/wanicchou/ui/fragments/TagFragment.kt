package com.waifusims.wanicchou.ui.fragments

import android.os.Bundle
import android.util.Log
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
import com.waifusims.wanicchou.ui.adapter.TextSpanRecyclerViewAdapter
import com.waifusims.wanicchou.util.InputAlertDialogBuilder
import com.waifusims.wanicchou.viewmodel.TagViewModel
import com.waifusims.wanicchou.viewmodel.VocabularyViewModel
import data.room.VocabularyRepository
import data.room.entity.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Builder or Factory for the fragment
// TODO: onCreate is very repeated between tags, notes. Maybe abstract and inherit
class TagFragment : TextBlockFragment("Tags") {
    companion object {
        private val TAG : String = TagFragment::class.java.simpleName
    }

    private val tagViewModel : TagViewModel by lazy {
        //TODO: Make sure this assert isn't problematic
        ViewModelProviders.of(activity!!)
                .get(TagViewModel::class.java)
    }

    private val vocabularyViewModel : VocabularyViewModel by lazy {
        ViewModelProviders.of(activity!!)
                          .get(VocabularyViewModel::class.java)
    }

    private val repository : VocabularyRepository by lazy {
        VocabularyRepository(activity!!.application)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        setObserver(view)
        setAddTagButtonOnClick(view)
        return view
    }

    private fun setObserver(view : View){
        val lifecycleOwner : LifecycleOwner = context as LifecycleOwner
        vocabularyViewModel.setObserver(lifecycleOwner){
            runBlocking (Dispatchers.IO){
                val dbTags = repository.getTags(vocabularyViewModel.vocabulary.vocabularyID)
                activity!!.runOnUiThread {
                    tagViewModel.value = dbTags
                }
            }
            val recyclerView = view.findViewById<RecyclerView>(R.id.rv_text_block_contents)
            Log.v(TAG, "LiveData emitted.")
            val tags = tagViewModel.value!!.map{ it.tagText }
            Log.v(TAG, "Result size: [${tags.size}].")
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.SPACE_AROUND
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = TextSpanRecyclerViewAdapter(tags)
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
            val title = "Add Tag"
            val message = null
            val dialogBuilder = InputAlertDialogBuilder(context,
                                                        view as ViewGroup,
                                                        title,
                                                        message)

            dialogBuilder.setPositiveButton("Add") { dialog, _ ->
                val tagText = dialogBuilder.input.text
                val vocabularyID = vocabularyViewModel.vocabulary.vocabularyID
                GlobalScope.launch (Dispatchers.IO){
                    repository.addVocabularyTag(tagText.toString(), vocabularyID)
                }
                val tag = Tag(tagText.toString())
                val tags = tagViewModel.value!!.toMutableList()
                tags.add(tag)
                tagViewModel.value = tags
                val recyclerViewAdapter = view.findViewById<RecyclerView>(R.id.rv_text_block_contents)
                                              .adapter!! as TextSpanRecyclerViewAdapter
                recyclerViewAdapter.list.add(tagText.toString())
                recyclerViewAdapter.notifyItemInserted(tags.size)
                dialog.dismiss()
            }
            dialogBuilder.show()
        }
    }
}
