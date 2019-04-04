package com.waifusims.wanicchou.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.flexbox.AlignSelf
import com.google.android.flexbox.FlexboxLayoutManager
import com.waifusims.wanicchou.R

//TODO: Change this implementation
//TODO: DiffUtil for better animations
// https://medium.com/@iammert/using-diffutil-in-android-recyclerview-bdca8e4fbb00
class TextSpanRecyclerViewAdapter(list: List<String>,
                                  private val onClickListener : View.OnClickListener? = null)
    : ListViewAdapter<String, TextSpanRecyclerViewAdapter.ViewHolder>(list.toMutableList(),
                ::ViewHolder,
                R.layout.rv_item_text_span){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = super.onCreateViewHolder(parent, viewType)
        if(onClickListener != null){
            viewHolder.itemView.setOnClickListener(onClickListener)
        }
        return viewHolder
    }
    class ViewHolder(itemView: View) : ListViewAdapter.ViewHolder<String>(itemView) {
        private val textView : TextView = itemView.findViewById(R.id.tv_item_span)
        override fun bind(value: String) {
            //TODO: String resource template
            //TODO: Figure out if you can use interpolated strings with string resource
            textView.text = value
            if (itemView.layoutParams is FlexboxLayoutManager.LayoutParams){
                val flexboxLayoutParams = itemView.layoutParams as FlexboxLayoutManager.LayoutParams
                flexboxLayoutParams.flexGrow = 1.0f
                flexboxLayoutParams.alignSelf = AlignSelf.AUTO
            }
        }
    }
}
