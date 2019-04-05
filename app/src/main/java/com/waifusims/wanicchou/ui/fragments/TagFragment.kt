package com.waifusims.wanicchou.ui.fragments

import android.os.Bundle
import android.util.Log
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
import com.waifusims.wanicchou.R
import com.waifusims.wanicchou.ui.adapter.TextSpanRecyclerViewAdapter
import com.waifusims.wanicchou.util.InputAlertDialogBuilder
import com.waifusims.wanicchou.viewmodel.TagViewModel
import com.waifusims.wanicchou.viewmodel.VocabularyViewModel
import data.room.VocabularyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// Builder or Factory for the fragment
// TODO: onCreate is very repeated between tags, notes. Maybe abstract and inherit
class TagFragment : TextBlockFragment("Tags") {
    companion object {
        private val TAG : String = TagFragment::class.java.simpleName
    }

    private val tagViewModel : TagViewModel by lazy {
        //TODO: Make sure this assert isn't problematic
        ViewModelProviders.of(parentFragmentActivity)
                .get(TagViewModel::class.java)
    }

    private val vocabularyViewModel : VocabularyViewModel by lazy {
        ViewModelProviders.of(parentFragmentActivity)
                          .get(VocabularyViewModel::class.java)
    }

    private val repository : VocabularyRepository by lazy {
        VocabularyRepository(parentFragmentActivity.application)
    }

    private lateinit var parentFragmentActivity : FragmentActivity

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        parentFragmentActivity = activity!!
        setObserver(view)
        setAddTagButtonOnClick(view)
        GlobalScope.launch(Dispatchers.IO){
            refreshTagViewModel()
        }
        return view
    }

    private fun refreshTagViewModel(){
        val dbTags = repository.getTags(vocabularyViewModel.vocabulary.vocabularyID)
        parentFragmentActivity.runOnUiThread {
            tagViewModel.value = dbTags
        }
    }



    private fun setObserver(view : View){
        val lifecycleOwner : LifecycleOwner = this
        val context = context!!
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_text_block_contents)
        Log.v(TAG, "LiveData emitted.")
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.SPACE_AROUND
        recyclerView.layoutManager = layoutManager
        val onClickListener = View.OnClickListener { v ->
            Log.v(TAG, "OnClick")
            val position = recyclerView.getChildLayoutPosition(v!!)
            val tag = tagViewModel.value!![position]
            val title = getString(R.string.edit_tag_title)
            val dialogBuilder = InputAlertDialogBuilder(context,
                    view as ViewGroup,
                    title,
                    "" )

            dialogBuilder.input.setText(tag.tagText)
            dialogBuilder.setPositiveButton(getString(R.string.save_button_text)){ dialog, _ ->
                tag.tagText = dialogBuilder.input.text.toString()
                GlobalScope.launch(Dispatchers.IO){
                    repository.updateTag(tag)
                    refreshTagViewModel()
                }
                dialog.dismiss()
            }

            dialogBuilder.setNeutralButton(getString(R.string.delete_button_text)){ dialog, _ ->
                val vocabularyID = vocabularyViewModel.vocabulary.vocabularyID
                val tagID = tagViewModel.value!![position].tagID
                GlobalScope.launch(Dispatchers.IO){
                    repository.deleteVocabularyTag(vocabularyID, tagID)
                    refreshTagViewModel()
                }
                dialog.dismiss()
            }
            dialogBuilder.show()
        }

        vocabularyViewModel.setObserver(lifecycleOwner){
            GlobalScope.launch(Dispatchers.IO){
                refreshTagViewModel()
            }
        }

        tagViewModel.setObserver(lifecycleOwner){
            val tags = tagViewModel.value!!.map{ it.tagText }
            Log.v(TAG, "Result size: [${tags.size}].")
            recyclerView.adapter = TextSpanRecyclerViewAdapter(tags, onClickListener)
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
            val title = getString(R.string.add_tag_title)
            val message = null
            val dialogBuilder = InputAlertDialogBuilder(context,
                                                        view as ViewGroup,
                                                        title,
                                                        message)

            dialogBuilder.setPositiveButton(getString(R.string.add_button_text)) { dialog, _ ->
                val tagText = dialogBuilder.input.text
                val vocabularyID = vocabularyViewModel.vocabulary.vocabularyID
                GlobalScope.launch (Dispatchers.IO){
                    repository.addVocabularyTag(tagText.toString(), vocabularyID)
                    refreshTagViewModel()
                }
                dialog.dismiss()
            }
            dialogBuilder.show()
        }
    }
}
