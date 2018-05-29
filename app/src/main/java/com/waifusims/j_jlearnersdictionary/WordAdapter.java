package com.waifusims.j_jlearnersdictionary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * RecyclerView adapter for related words
 * Created by Limegrass on 4/4/2018.
 */
public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder>{
    private static final String TAG = WordAdapter.class.getSimpleName();
    private final ListItemClickListener mOnClickListener;

    // TODO: Generalize for multiple dictionary selection, or restrict user to select only 1 dictionary
    // This may require changing from Map<String, Set<String> > in others to being just a vector
    private List<String> relatedWords;
    // TODO: Bandaid solution, fix later in constructor. (See above todo)
    private List<String> dictionary;

    public WordAdapter(Map<String, Set<String>> words, ListItemClickListener listener){
        relatedWords = new ArrayList<>();
        dictionary = new ArrayList<>();

        for (String dictionaryKey : words.keySet()){
            for(String word : words.get(dictionaryKey)){
                relatedWords.add(word);
                dictionary.add(dictionaryKey);
            }
        }
        mOnClickListener = listener;
    }

    @Override
    public WordAdapter.WordViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType){
        Context context = parent.getContext();
        int wordLayoutId = R.layout.word_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachToParent = false;
        View view = inflater.inflate(wordLayoutId, parent, attachToParent);
        WordViewHolder wordViewHolder = new WordViewHolder(view);
        return wordViewHolder;
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
        return relatedWords.get(position);
    }

    @Override
    public int getItemCount() {
        return relatedWords.size();
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
            tvDictionary.setText(dictionary.get(listIndex));
            tvWord.setText(relatedWords.get(listIndex));
        }



        @Override
        public boolean onLongClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
            return false;
        }
    }
}
