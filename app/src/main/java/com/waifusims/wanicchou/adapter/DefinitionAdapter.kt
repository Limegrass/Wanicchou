package com.waifusims.wanicchou.adapter

import android.view.View
import android.widget.TextView
import com.waifusims.wanicchou.R
import data.room.entity.Definition

class DefinitionAdapter(val list : List<Definition>) :
        ListViewAdapter<Definition, DefinitionAdapter.ViewHolder>(list, ::ViewHolder, R.layout.rv_item_definition){
    class ViewHolder(itemView: View) : ListViewAdapter.ViewHolder<Definition>(itemView) {
        private val tvDefinition : TextView = itemView.findViewById(R.id.tv_definition)
        override fun bind(value: Definition) {
            tvDefinition.text = value.definitionText
        }
    }
}