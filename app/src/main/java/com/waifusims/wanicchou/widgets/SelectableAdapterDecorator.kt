package com.waifusims.wanicchou.widgets

import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.view.ViewGroup

import java.util.ArrayList

abstract class SelectableAdapterDecorator<VH : RecyclerView.ViewHolder>(val adapter: RecyclerView.Adapter<VH>)
    : RecyclerView.Adapter<VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): VH {
        return adapter.onCreateViewHolder(parent, position)
    }

    override fun onBindViewHolder(parent: VH, position: Int) {
        adapter.onBindViewHolder(parent, position)
    }

    override fun getItemCount(): Int {
        return adapter.itemCount
    }


    private val mSelectedItems: SparseBooleanArray = SparseBooleanArray()
    val count: Int
        get() = mSelectedItems.size()

    val selectedItems: List<Int>
        get() {
            val selections = ArrayList<Int>(mSelectedItems.size())
            for (i in 0 until mSelectedItems.size()) {
                selections.add(mSelectedItems.keyAt(i))
            }
            return selections
        }

    fun isSelected(position: Int): Boolean {
        return mSelectedItems.get(position)
    }

    fun toggleSelection(position: Int) {
        if (mSelectedItems.get(position, false)) {
            mSelectedItems.delete(position)
        } else {
            mSelectedItems.put(position, true)
        }
        notifyItemChanged(position)
    }

    fun clearSelection() {
        val selections = selectedItems
        mSelectedItems.clear()
        for (position in selections) {
            notifyItemChanged(position)
        }
    }
}
