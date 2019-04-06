package com.limegrass.wanicchou.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class ListViewAdapter<T, VH : ListViewAdapter.ViewHolder<T>>(private val list : MutableList<T>,
                                                             private val viewHolderConstructor: (View) -> VH,
                                                             private val layoutID : Int)
    : androidx.recyclerview.widget.RecyclerView.Adapter<VH>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val attachToRoot = false
        val view = inflater.inflate(layoutID, parent, attachToRoot)
        return viewHolderConstructor(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewHolder: VH, position: Int){
        viewHolder.bind(list[position])
    }

    abstract class ViewHolder<T>(itemView: View)
        : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        abstract fun bind(value: T)
    }
}
