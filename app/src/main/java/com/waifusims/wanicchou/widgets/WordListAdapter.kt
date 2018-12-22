//package com.waifusims.wanicchou.widgets
//
//import android.support.v7.widget.RecyclerView
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//
//import com.waifusims.wanicchou.R
//
//import data.vocab.shared.WordListEntry
//
///**
// * RecyclerView adapter for related words
// * Created by Limegrass on 4/4/2018.
// */
//class WordListAdapter<VH : RecyclerView.ViewHolder>(private val mWordList: List<WordListEntry>, private val mListItemClickListener: WordViewHolder.ListItemClickListener) : SelectableAdapter<WordListAdapter.WordViewHolder>() {
////
////    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
////        val context = parent.context
////        val wordLayoutId = R.layout.word_list_item
////        val inflater = LayoutInflater.from(context)
////        val view = inflater.inflate(wordLayoutId, parent, false)
////        return WordViewHolder(view, mListItemClickListener)
////    }
////
////
////    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
////        //        Log.d(TAG, "#" + position);
////        val word = mWordList[position]
////        holder.showSelectedOverlay(isSelected(position))
////        holder.setDictionaryType(mWordList[position].dictionaryType)
////        holder.setRelatedWord(mWordList[position].relatedWord)
////    }
////
////    fun getWordAtPosition(position: Int): String? {
////        return mWordList[position].relatedWord
////    }
////
////    override fun getItemCount(): Int {
////        return mWordList.size
////    }
////
////    class WordViewHolder(view: View, private val mLongClickListener: ListItemClickListener) : RecyclerView.ViewHolder(view), View.OnLongClickListener, View.OnClickListener {
////        private val mTVDictionary: TextView
////        private val mTVWord: TextView
////        private val mViewSelectedOverlay: View
////
////        init {
////            mTVDictionary = view.findViewById(R.id.tv_word_dic_type)
////            mTVWord = view.findViewById(R.id.tv_word_item)
////            mViewSelectedOverlay = view.findViewById(R.id.view_selected_overlay)
////            view.setOnLongClickListener(this)
////        }
////
////        fun setDictionaryType(dictionaryType: DictionaryType?) {
////            mTVDictionary.text = dictionaryType!!.toDisplayText()
////        }
////
////        fun setRelatedWord(word: String?) {
////
////            mTVWord.text = word
////        }
////
////        fun showSelectedOverlay(selected: Boolean) {
////            mViewSelectedOverlay.visibility = if (selected) View.VISIBLE else View.INVISIBLE
////        }
////
////        override fun onLongClick(view: View): Boolean {
////            val position = adapterPosition
////            Log.d(TAG, "Word index: $position")
////            mLongClickListener.onItemLongClick(position)
////            return true
////        }
////
////        override fun onClick(view: View) {
////            val position = adapterPosition
////            mLongClickListener.onItemClick(position)
////        }
////
////        interface ListItemClickListener {
////            fun onItemLongClick(position: Int)
////            fun onItemClick(position: Int)
////        }
////    }
////
////    companion object {
////        private val TAG = WordListAdapter<*>::class.java!!.getSimpleName()
////    }
//}
