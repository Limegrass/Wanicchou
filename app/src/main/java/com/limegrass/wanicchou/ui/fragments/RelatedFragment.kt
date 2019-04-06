package com.limegrass.wanicchou.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.limegrass.wanicchou.R
import com.limegrass.wanicchou.ui.adapter.RelatedVocabularyAdapter
import com.limegrass.wanicchou.util.WanicchouSharedPreferenceHelper
import com.limegrass.wanicchou.viewmodel.DefinitionViewModel
import com.limegrass.wanicchou.viewmodel.RelatedVocabularyViewModel
import com.limegrass.wanicchou.viewmodel.VocabularyViewModel
import data.room.VocabularyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
        return inflater.inflate(R.layout.fragment_related,
                container,
                attachToRoot)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setRelatedObserver(view)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setRelatedObserver(view : View){
        val context = context!!
        val activity = activity!!
        val lifecycleOwner : LifecycleOwner = this
        relatedVocabularyViewModel.setObserver(lifecycleOwner){
            val recyclerView = view.findViewById<RecyclerView>(R.id.rv_related)
            Log.v(TAG, "LiveData emitted.")
            val relatedVocabularyList = relatedVocabularyViewModel.value
            if(!relatedVocabularyList.isNullOrEmpty()){
                val onClickListener = View.OnClickListener { v ->
                    Log.v(TAG, "OnClick")
                    val position = recyclerView.getChildLayoutPosition(v!!)
                    val vocab = relatedVocabularyList[position]
                    Toast.makeText(context, "Searching for ${vocab.word}.", Toast.LENGTH_LONG).show()
                    runBlocking(Dispatchers.IO){
                        val definition = repository.getRelatedVocabularyDefinition(vocab,
                                sharedPreferenceHelper.definitionLanguageID,
                                sharedPreferenceHelper.dictionary)
                        activity.runOnUiThread {
                            vocabularyViewModel.value = listOf(vocab)
                            definitionViewModel.value = listOf(definition)
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

        vocabularyViewModel.setObserver(lifecycleOwner){
            GlobalScope.launch(Dispatchers.IO) {
                val vocabularyID = vocabularyViewModel.vocabulary.vocabularyID
                val relatedWordList = repository.getRelatedWords(vocabularyID)
                activity.runOnUiThread {
                    relatedVocabularyViewModel.value = relatedWordList
                }
            }
        }
    }
}