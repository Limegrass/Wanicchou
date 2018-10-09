package com.waifusims.wanicchou.widgets;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.waifusims.wanicchou.R;

import java.util.List;

import data.vocab.WordListEntry;
import data.vocab.models.DictionaryType;

/**
 * RecyclerView adapter for related words
 * Created by Limegrass on 4/4/2018.
 */
public class WordListAdapter<VH extends RecyclerView.ViewHolder>
        extends SelectableAdapter<WordListAdapter.WordViewHolder> {
    private static final String TAG = WordListAdapter.class.getSimpleName();

    private WordViewHolder.ListItemClickListener mListItemClickListener;
    private List<WordListEntry> mWordList;

    public WordListAdapter(List<WordListEntry> words, WordViewHolder.ListItemClickListener listener){
        mWordList = words;
        mListItemClickListener = listener;
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
        int wordLayoutId = R.layout.word_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(wordLayoutId, parent, false);
        return new WordViewHolder(view, mListItemClickListener);
    }


    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
//        Log.d(TAG, "#" + position);
        final WordListEntry word = mWordList.get(position);
        holder.showSelectedOverlay(isSelected(position));
        holder.setDictionaryType(mWordList.get(position).getDictionaryType());
        holder.setRelatedWord(mWordList.get(position).getRelatedWord());
    }

    public String getWordAtPosition(int position){
        return mWordList.get(position).getRelatedWord();
    }

    @Override
    public int getItemCount() {
        return mWordList.size();
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener, View.OnClickListener {
        private TextView mTVDictionary;
        private TextView mTVWord;
        private View mViewSelectedOverlay;
        private ListItemClickListener mLongClickListener;

        public WordViewHolder(View view, ListItemClickListener listener){
            super(view);
            mTVDictionary = view.findViewById(R.id.tv_word_dic_type);
            mTVWord = view.findViewById(R.id.tv_word_item);
            mViewSelectedOverlay = view.findViewById(R.id.view_selected_overlay);
            mLongClickListener = listener;
            view.setOnLongClickListener(this);
        }

        public void setDictionaryType(DictionaryType dictionaryType){
            mTVDictionary.setText(dictionaryType.toDisplayText());
        }
        public void setRelatedWord(String word){

            mTVWord.setText(word);
        }

        public void showSelectedOverlay(boolean selected){
            mViewSelectedOverlay.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            Log.d(TAG, "Word index: " + position);
            mLongClickListener.onItemLongClick(position);
            return true;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mLongClickListener.onItemClick(position);
        }

        public interface ListItemClickListener {
            void onItemLongClick(int position);
            void onItemClick(int position);
        }
    }
}
