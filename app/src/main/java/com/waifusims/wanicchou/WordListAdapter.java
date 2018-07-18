package com.waifusims.wanicchou;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import data.vocab.WordListEntry;

/**
 * RecyclerView adapter for related words
 * Created by Limegrass on 4/4/2018.
 */
public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder>{
    private static final String TAG = WordListAdapter.class.getSimpleName();
    private final ListItemClickListener mOnClickListener;

    private List<WordListEntry> wordList;

    public WordListAdapter(List<WordListEntry> words, ListItemClickListener listener){
        wordList = words;
        mOnClickListener = listener;
    }

    @Override
    public WordListAdapter.WordViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType){
        Context context = parent.getContext();
        int wordLayoutId = R.layout.word_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(wordLayoutId, parent, false);
        return new WordViewHolder(view);
    }

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    public String getWordAtPosition(int position){
        return wordList.get(position).getRelatedWord();
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        private TextView tvDictionary;
        private TextView tvWord;

        public WordViewHolder(View view){
            super(view);

            tvDictionary = view.findViewById(R.id.tv_word_dic_type);
            tvWord = view.findViewById(R.id.tv_word_item);
            view.setOnLongClickListener(this);
        }

        void bind(int listIndex){
            tvDictionary.setText(wordList.get(listIndex).getDictionaryType().toDisplayText());
            tvWord.setText(wordList.get(listIndex).getRelatedWord());
        }



        @Override
        public boolean onLongClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
            return false;
        }
    }
}
