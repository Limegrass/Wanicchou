package com.waifusims.wanicchou.ui.adapter

import android.view.View
import android.widget.TextView
import com.waifusims.wanicchou.R
import data.room.entity.Definition

class DefinitionAdapter(list : List<Definition>) :
        ListViewAdapter<Definition, DefinitionAdapter.ViewHolder>(list.toMutableList(), ::ViewHolder, R.layout.rv_item_text_block){
    class ViewHolder(itemView: View) : ListViewAdapter.ViewHolder<Definition>(itemView) {
        private val tvDefinition : TextView = itemView.findViewById(R.id.tv_item_block)
        override fun bind(value: Definition) {
            tvDefinition.text = value.definitionText
        }
    }
}