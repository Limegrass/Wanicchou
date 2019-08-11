package com.limegrass.wanicchou

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.limegrass.wanicchou.ui.adapter.TextSpanRecyclerViewAdapter
import com.limegrass.wanicchou.viewmodel.DatabaseViewModel
import data.room.dbo.entity.Vocabulary


/**
 * Separate activity to display the related words of a SanseidoSearch.
 * If a word is long pressed, it will be searched and brought back to the home activity.
 * TODO: Clean up this entire activity
 */
class DatabaseActivity : AppCompatActivity(){
    companion object {
        private val TAG = DatabaseActivity::class.java.simpleName
        const val REQUEST_CODE = 3154
    }

    private val databaseViewModel : DatabaseViewModel by lazy {
        ViewModelProviders.of(this)
                .get(DatabaseViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database)
    }

    override fun onPostResume() {
        val recyclerView = findViewById<RecyclerView>(R.id.rv_database_list)
        val observer = Observer<List<Vocabulary>>{
            val layoutManager = FlexboxLayoutManager(this)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.SPACE_AROUND
            recyclerView.layoutManager = layoutManager
            val vocabularyList = databaseViewModel.vocabularyList.value!!
            val onClickListener = View.OnClickListener { v ->
                Log.v(TAG, "OnClick")
                val position = recyclerView.getChildLayoutPosition(v!!)
                val vocab = vocabularyList[position]
                val result = Intent()
                result.putExtra("Vocabulary", vocab)
                setResult(REQUEST_CODE, result)
                finish()
            }
            val vocabularyWords = vocabularyList.map{ "${it.word} [${it.pronunciation}]" }
            recyclerView.adapter = TextSpanRecyclerViewAdapter(vocabularyWords, onClickListener)
        }
        val obs = Observer<List<Vocabulary>>{
            recyclerView.adapter?.notifyDataSetChanged()
            recyclerView.invalidate()
        }
        databaseViewModel.vocabularyList.observe(this, observer)
        databaseViewModel.vocabularyList.observe(this, obs)
        super.onPostResume()
    }
}
