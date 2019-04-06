package com.waifusims.wanicchou.ui.adapter


import android.view.View
import android.widget.TextView
import com.google.android.flexbox.AlignSelf
import com.google.android.flexbox.FlexboxLayoutManager
import com.waifusims.wanicchou.R
import data.room.entity.Tag


class TagAdapter(list : List<Tag>) :
        ListViewAdapter<Tag, TagAdapter.ViewHolder>(list.toMutableList(), ::ViewHolder, R.layout.rv_item_text_span){
    class ViewHolder(itemView: View) : ListViewAdapter.ViewHolder<Tag>(itemView) {
        private val tvTag : TextView = itemView.findViewById(R.id.tv_item_span)
        override fun bind(value: Tag) {
            //TODO: String resource template
            //TODO: Figure out if you can use interpolated strings with string resource
            tvTag.text = value.tagText
            if (itemView.layoutParams is FlexboxLayoutManager.LayoutParams){
                val flexboxLayoutParams = itemView.layoutParams as FlexboxLayoutManager.LayoutParams
                flexboxLayoutParams.flexGrow = 1.0f
                flexboxLayoutParams.alignSelf = AlignSelf.AUTO
            }
        }
    }
}
