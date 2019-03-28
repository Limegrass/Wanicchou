package com.waifusims.wanicchou.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.waifusims.wanicchou.R
import com.waifusims.wanicchou.ui.adapter.RelatedVocabularyAdapter
import com.waifusims.wanicchou.util.WanicchouSharedPreferenceHelper
import com.waifusims.wanicchou.viewmodel.DefinitionViewModel
import com.waifusims.wanicchou.viewmodel.RelatedVocabularyViewModel
import com.waifusims.wanicchou.viewmodel.VocabularyViewModel
import data.room.VocabularyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class RelatedFragment : Fragment() {
    companion object {
        private val TAG : String = RelatedFragment::class.java.simpleName
    }
    private val vocabularyViewModel : VocabularyViewModel by lazy {
        //TODO: Make sure this assert isn't problematic
        ViewModelProviders.of(activity!!)
            .get(VocabularyViewModel::class.java)
    }
    private val relatedVocabularyViewModel : RelatedVocabularyViewModel by lazy {
        //TODO: Make sure this assert isn't problematic
        ViewModelProviders.of(activity!!)
                .get(RelatedVocabularyViewModel::class.java)
    }
    private val definitionViewModel : DefinitionViewModel by lazy {
        ViewModelProviders.of(activity!!)
                          .get(DefinitionViewModel::class.java)
    }
    private val repository : VocabularyRepository by lazy {
        VocabularyRepository(activity!!.application)
    }
    private val sharedPreferenceHelper : WanicchouSharedPreferenceHelper by lazy {
        WanicchouSharedPreferenceHelper(context!!)
    }
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val attachToRoot = false
        val view = inflater.inflate(R.layout.fragment_related,
                container,
                attachToRoot)
        setRelatedObserver(view)
        return view
    }

    private fun setRelatedObserver(view : View){
        val lifecycleOwner : LifecycleOwner = activity as LifecycleOwner
        relatedVocabularyViewModel.setObserver(lifecycleOwner){
            val recyclerView = view.findViewById<RecyclerView>(R.id.rv_related)
            Log.v(TAG, "LiveData emitted.")
            val relatedVocabularyList = relatedVocabularyViewModel.list
            if(!relatedVocabularyList.isNullOrEmpty()){
                val onClickListener = View.OnClickListener { v ->
                    Log.v(TAG, "OnClick")
                    val position = recyclerView.getChildLayoutPosition(v!!)
                    val vocab = relatedVocabularyList[position]
                    runBlocking(Dispatchers.IO){
                        val definitionList = repository.getRelatedVocabularyDefinition(vocab,
                                sharedPreferenceHelper.definitionLanguageCode,
                                sharedPreferenceHelper.dictionary)
                        if(definitionList.isNotEmpty()){
                            activity!!.runOnUiThread {
                                vocabularyViewModel.list = listOf(vocab)
                                definitionViewModel.list = definitionList
                            }
                        }
                    }
                }
                Log.v(TAG, "Result size: [${relatedVocabularyList.size}].")
                val layoutManager = FlexboxLayoutManager(context)
                layoutManager.flexDirection = FlexDirection.ROW
                layoutManager.justifyContent = JustifyContent.SPACE_AROUND

                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = RelatedVocabularyAdapter(relatedVocabularyList, onClickListener)
            }
        }
    }
}