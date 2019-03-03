package com.waifusims.wanicchou.ui.adapter

import android.view.View
import android.widget.TextView
import com.waifusims.wanicchou.R
import data.room.entity.Vocabulary


class RelatedVocabularyAdapter(val list : List<Vocabulary>) :
        ListViewAdapter<Vocabulary, RelatedVocabularyAdapter.ViewHolder>(list, ::ViewHolder, R.layout.rv_item_related){
    class ViewHolder(itemView: View) : ListViewAdapter.ViewHolder<Vocabulary>(itemView) {
        private val tvRelated : TextView = itemView.findViewById(R.id.tv_related)
        override fun bind(value: Vocabulary) {
            //TODO: String resource template
            //TODO: Figure out if you can use interpolated strings with string resource
            val text = "${value.word}[${value.pronunciation}]"
            tvRelated.text = text
        }
    }
}
