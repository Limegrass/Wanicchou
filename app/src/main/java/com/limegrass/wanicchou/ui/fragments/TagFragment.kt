package com.limegrass.wanicchou.ui.fragments

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
import com.limegrass.wanicchou.R
import com.limegrass.wanicchou.ui.adapter.TextSpanRecyclerViewAdapter
import com.limegrass.wanicchou.util.InputAlertDialogBuilder
import com.limegrass.wanicchou.viewmodel.TagViewModel
import com.limegrass.wanicchou.viewmodel.DictionaryEntryViewModel
import data.arch.models.ITaggedItem
import data.arch.models.IVocabulary
import data.arch.util.IRepository
import data.arch.util.ISearchProvider
import data.models.TaggedItem
import data.room.database.WanicchouDatabase
import data.room.repository.VocabularyTagRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// Builder or Factory for the fragment
// TODO: onCreate is very repeated between tags, notes. Maybe abstract and inherit
class TagFragment : TextBlockFragment() {
    companion object {
        private val TAG : String = TagFragment::class.java.simpleName
    }

    private val tagViewModel : TagViewModel by lazy {
        //TODO: Make sure this assert isn't problematic
        ViewModelProviders.of(parentFragmentActivity)
                .get(TagViewModel::class.java)
    }

    private val dictionaryEntryViewModel : DictionaryEntryViewModel by lazy {
        ViewModelProviders.of(parentFragmentActivity)
                          .get(DictionaryEntryViewModel::class.java)
    }

    private val tagRepository : IRepository<ITaggedItem<IVocabulary>, IVocabulary> by lazy {
        val database = WanicchouDatabase(parentFragmentActivity.application)
        VocabularyTagRepository(database)
    }

    private lateinit var parentFragmentActivity : FragmentActivity

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        title = getString(R.string.lbl_fragment_tag_title)
        parentFragmentActivity = activity!!
        setObserver(view)
        setAddTagButtonOnClick(view)
        GlobalScope.launch(Dispatchers.IO){
            refreshTagViewModel()
        }
        return view
    }

    private suspend fun refreshTagViewModel(){
        val dictionaryEntry = dictionaryEntryViewModel.value
        if (dictionaryEntry != null){
            val dbTags = tagRepository.search(dictionaryEntry.vocabulary)
            parentFragmentActivity.runOnUiThread {
                tagViewModel.value = dbTags
            }
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

            dialogBuilder.input.setText(tag.tag)
            dialogBuilder.setPositiveButton(getString(R.string.save_button_text)){ dialog, _ ->
                val updatedTag = TaggedItem(tag.item, dialogBuilder.input.text.toString())
                GlobalScope.launch(Dispatchers.IO){
                    tagRepository.update(tag, updatedTag)
                    refreshTagViewModel()
                }
                dialog.dismiss()
            }

            dialogBuilder.setNeutralButton(getString(R.string.delete_button_text)){ dialog, _ ->
                GlobalScope.launch(Dispatchers.IO){
                    tagRepository.delete(tag)
                    refreshTagViewModel()
                }
                dialog.dismiss()
            }
            dialogBuilder.show()
        }

        dictionaryEntryViewModel.setObserver(lifecycleOwner){
            GlobalScope.launch(Dispatchers.IO){
                refreshTagViewModel()
            }
        }

        tagViewModel.setObserver(lifecycleOwner){
            val tags = tagViewModel.value!!.map{ it.tag }
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
                val tagText = dialogBuilder.input.text.toString()
                val dictionaryEntry = dictionaryEntryViewModel.value
                if(dictionaryEntry != null){
                    val vocabulary = dictionaryEntry.vocabulary
                    GlobalScope.launch (Dispatchers.IO){
                        val tag = TaggedItem(vocabulary, tagText)
                        tagRepository.insert(tag)
                        refreshTagViewModel()
                    }
                }
                dialog.dismiss()
            }
            dialogBuilder.show()
        }
    }
}
