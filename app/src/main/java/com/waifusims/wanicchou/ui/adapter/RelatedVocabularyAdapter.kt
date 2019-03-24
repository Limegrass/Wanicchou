package com.waifusims.wanicchou.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignSelf
import com.google.android.flexbox.FlexboxLayoutManager
import com.waifusims.wanicchou.R
import data.room.entity.Vocabulary


class RelatedVocabularyAdapter(list : List<Vocabulary>, private val onClickListener : View.OnClickListener) :
        ListViewAdapter<Vocabulary, RelatedVocabularyAdapter.ViewHolder>(list, ::ViewHolder, R.layout.rv_item_text_span){
    companion object {
        private val TAG = RelatedVocabularyAdapter::class.java.simpleName
    }


    private lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = super.onCreateViewHolder(parent, viewType)
        viewHolder.itemView.setOnClickListener(onClickListener)
        return viewHolder
    }


    class ViewHolder(itemView: View) : ListViewAdapter.ViewHolder<Vocabulary>(itemView) {
        private val tvRelated : TextView = itemView.findViewById(R.id.tv_item_span)
        override fun bind(value: Vocabulary) {
            //TODO: String resource template
            //TODO: Figure out if you can use interpolated strings with string resource
            val text = "${value.word} [${value.pronunciation}]"
            tvRelated.text = text
            if (itemView.layoutParams is FlexboxLayoutManager.LayoutParams){
                val flexboxLayoutParams = itemView.layoutParams as FlexboxLayoutManager.LayoutParams
                flexboxLayoutParams.flexGrow = 1.0f
                flexboxLayoutParams.alignSelf = AlignSelf.AUTO
            }
        }

    }
}
