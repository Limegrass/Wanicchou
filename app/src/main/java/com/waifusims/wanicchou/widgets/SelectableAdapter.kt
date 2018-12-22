package com.waifusims.wanicchou.widgets

import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray

import java.util.ArrayList

abstract class SelectableAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
//    private val mSelectedItems: SparseBooleanArray
//
//    val selectedItemsCount: Int
//        get() = mSelectedItems.size()
//
//    val selectedItems: List<Int>
//        get() {
//            val selections = ArrayList<Int>(mSelectedItems.size())
//            for (i in 0 until mSelectedItems.size()) {
//                selections.add(mSelectedItems.keyAt(i))
//            }
//            return selections
//        }
//
//    init {
//        mSelectedItems = SparseBooleanArray()
//    }
//
//    fun isSelected(position: Int): Boolean {
//        return selectedItems.contains(position)
//    }
//
//    fun toggleSelection(position: Int) {
//        if (mSelectedItems.get(position, false)) {
//            mSelectedItems.delete(position)
//        } else {
//            mSelectedItems.put(position, true)
//        }
//        notifyItemChanged(position)
//    }
//
//    fun clearSelection() {
//        val selections = selectedItems
//        mSelectedItems.clear()
//        for (position in selections) {
//            notifyItemChanged(position)
//        }
//    }
}
