package com.waifusims.wanicchou.widgets;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    private SparseBooleanArray mSelectedItems;

    public SelectableAdapter(){
        mSelectedItems = new SparseBooleanArray();
    }

    public boolean isSelected(int position){
        return getSelectedItems().contains(position);
    }

    public void toggleSelection(int position) {
        if (mSelectedItems.get(position, false)){
            mSelectedItems.delete(position);
        } else{
            mSelectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    public void clearSelection() {
        List<Integer> selections = getSelectedItems();
        mSelectedItems.clear();
        for (int position : selections){
            notifyItemChanged(position);
        }
    }

    public int getSelectedItemsCount(){
        return mSelectedItems.size();
    }

    public List<Integer> getSelectedItems(){
        List<Integer> selections = new ArrayList<>(mSelectedItems.size());
        for (int i = 0; i < mSelectedItems.size(); i++) {
            selections.add(mSelectedItems.keyAt(i));
        }
        return selections;
    }
}
