package com.limegrass.wanicchou.ui.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.limegrass.wanicchou.R
import com.limegrass.wanicchou.ui.adapter.TextSpanRecyclerViewAdapter
import com.limegrass.wanicchou.util.WanicchouSearchManager
import com.limegrass.wanicchou.util.WanicchouSharedPreferences
import com.limegrass.wanicchou.viewmodel.DictionaryEntryViewModel
import data.room.database.WanicchouDatabase
import data.room.repository.DictionaryEntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class RelatedFragment : Fragment() {
    companion object {
        private val TAG : String = RelatedFragment::class.java.simpleName
    }
    private val dictionaryEntryViewModel : DictionaryEntryViewModel by lazy {
        ViewModelProviders.of(parentFragmentActivity)
            .get(DictionaryEntryViewModel::class.java)
    }

    private val searchManager by lazy {
        val database = WanicchouDatabase(parentFragmentActivity)
        val repository = DictionaryEntryRepository(database)
        val connectivityManager = parentFragmentActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val sharedPreferences = WanicchouSharedPreferences(parentFragmentActivity)
        WanicchouSearchManager(repository, connectivityManager, sharedPreferences, parentFragmentActivity)
    }

    private lateinit var parentFragmentActivity : FragmentActivity
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val attachToRoot = false
        parentFragmentActivity = activity!!
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
        val lifecycleOwner : LifecycleOwner = this
        dictionaryEntryViewModel.setObserver(lifecycleOwner){
            val recyclerView = view.findViewById<RecyclerView>(R.id.rv_related)
            Log.v(TAG, "LiveData emitted.")
            val dictionaryEntries = dictionaryEntryViewModel.availableDictionaryEntries
            if(!dictionaryEntries.isNullOrEmpty()){
                val onClickListener = View.OnClickListener { v ->
                    Log.v(TAG, "OnClick")
                    val position = recyclerView.getChildLayoutPosition(v!!)
                    val dictionaryEntry = dictionaryEntries[position]
                    if(dictionaryEntry.definitions.isNotEmpty()){
                        dictionaryEntryViewModel.value = dictionaryEntry
                    } else {
                        runBlocking(Dispatchers.IO){
                            val searchResults = searchManager.search(dictionaryEntry.vocabulary.word)
                            Log.v(TAG, "Result size: [${searchResults.size}].")
                        }
                    }
                }
                val layoutManager = FlexboxLayoutManager(context)
                layoutManager.flexDirection = FlexDirection.ROW
                layoutManager.justifyContent = JustifyContent.SPACE_AROUND
                val activeEntry = dictionaryEntryViewModel.value
                val relatedVocabularies = dictionaryEntries.filter {
                    it != activeEntry
                }.map {
                    "${it.vocabulary.word} [${it.vocabulary.pronunciation}]"
                }
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = TextSpanRecyclerViewAdapter(relatedVocabularies, onClickListener)
            }

        }
    }
}