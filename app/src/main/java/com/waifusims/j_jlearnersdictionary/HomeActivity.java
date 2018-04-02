package com.waifusims.j_jlearnersdictionary;

import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.waifusims.j_jlearnersdictionary.databinding.ActivityHomeBinding;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.SanseidoSearch;
import util.anki.AnkiDroidHelper;
import util.anki.AnkiDroidConfig;

import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {
    public static final String LOG_TAG = "JJLD";
    private static final int ADD_PERM_REQUEST = 0;

    private ActivityHomeBinding mBinding;
    private AnkiDroidHelper mAnkiDroid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAnkiDroid = new AnkiDroidHelper(this);

        setContentView(R.layout.activity_home);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        mBinding.wordSearch.etSearchBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(mBinding.wordSearch.etSearchBox.getText().toString()
                        .equals(getResources().getString(R.string.search_field_default))) {
                    mBinding.wordSearch.etSearchBox.getText().clear();
                }
            }
        });
        mBinding.wordSearch.etContext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(mBinding.wordSearch.etContext.getText().toString()
                        .equals(getResources().getString(R.string.context_default))) {
                    mBinding.wordSearch.etContext.getText().clear();
                }
            }
        });
        mBinding.wordSearch.etNotes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(mBinding.wordSearch.etNotes.getText().toString()
                        .equals(getResources().getString(R.string.notes_default))) {
                    mBinding.wordSearch.etNotes.getText().clear();
                }
            }
        });

        mBinding.wordSearch.etSearchBox.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View searchBox, int keyCode, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    switch (keyCode){
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            String searchWord = mBinding.wordSearch.etSearchBox.getText().toString();
                            URL url = null;
                            try{
                                url = SanseidoSearch.buildQueryURL(searchWord, true);
                            }
                            catch (MalformedURLException e){
                                e.printStackTrace();
                            }
                            new SanseidoQueryTask().execute(url);
                            return true;
                        default:
                            //TODO: Would returning true do anything undesired? Find out
                            return false;
                    }
                }
                return false;
            }
        });

        //TODO: Fix it so it requests on add card, not on start up. Avoid crashes before addition.
        if (mAnkiDroid.shouldRequestPermission()) {
            mAnkiDroid.requestPermission(HomeActivity.this, ADD_PERM_REQUEST);
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                addWordToAnki(view);
            }
        });
    // Add all data using AnkiDroid provider
//    addCardsToAnkiDroid(AnkiDroidConfig.getExampleData());

    }


    public class SanseidoQueryTask extends AsyncTask<URL, Void, Document>{
        Toast toast;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            final Context context = getApplicationContext();
            final CharSequence searchingText = getResources().getString(R.string.word_searching);
            final int searchingToastDuration = Toast.LENGTH_SHORT;

            toast = Toast.makeText(context, searchingText, searchingToastDuration);
            toast.show();
        }

        @Override
        protected Document doInBackground(URL... urls) {
            URL searchURL = urls[0];
            Document htmlTree = null;
            try{
                htmlTree = SanseidoSearch.getSanseidoSource(searchURL);
            }
            catch (IOException e){
                e.printStackTrace();
            }

            return htmlTree;
        }

        @Override
        protected void onPostExecute(Document htmlTree) {
            super.onPostExecute(htmlTree);
            toast.cancel();

            if(htmlTree != null && !htmlTree.equals("")){
                String definition = SanseidoSearch.getDefinition(htmlTree);

                final Context context = getApplicationContext();
                final int searchCompleteToastDuration = Toast.LENGTH_SHORT;
                CharSequence text;

                if (definition.equals("")){
                    text = getResources().getString(R.string.word_search_failure);
                }
                else{
                    String successfulWordSearched = SanseidoSearch.getWord(htmlTree);
                    mBinding.wordSearch.tvWord.setText(successfulWordSearched);
                    mBinding.wordSearch.tvDefinition.setText(definition);
                    text = getResources().getString(R.string.word_search_success);
                }
                toast = Toast.makeText(context, text, searchCompleteToastDuration);
                toast.show();
            }
            else{
            }
        }

    }

    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions,
                                            @NonNull int[] grantResults) {
        if (requestCode==ADD_PERM_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            addCardsToAnkiDroid(AnkiDroidConfig.getExampleData());
        } else {
            Toast.makeText(HomeActivity.this, "Permissions denied", Toast.LENGTH_LONG).show();
        }
    }

    private long getDeckId() {
        Long did = mAnkiDroid.findDeckIdByName(AnkiDroidConfig.DECK_NAME);
        if (did == null) {
            did = mAnkiDroid.getApi().addNewDeck(AnkiDroidConfig.DECK_NAME);
            mAnkiDroid.storeDeckReference(AnkiDroidConfig.DECK_NAME, did);
        }
        return did;
    }

    private long getModelId() {
        Long mid = mAnkiDroid.findModelIdByName(AnkiDroidConfig.MODEL_NAME, AnkiDroidConfig.FIELDS.length);
        if (mid == null) {
            mid = mAnkiDroid.getApi().addNewCustomModel(AnkiDroidConfig.MODEL_NAME, AnkiDroidConfig.FIELDS,
                    AnkiDroidConfig.CARD_NAMES, AnkiDroidConfig.QFMT, AnkiDroidConfig.AFMT, AnkiDroidConfig.CSS, getDeckId(), null);
            mAnkiDroid.storeModelReference(AnkiDroidConfig.MODEL_NAME, mid);
        }
        return mid;
    }

    /**
     * Use the instant-add API to add flashcards directly to AnkiDroid.
     * @param data List of cards to be added. Each card has a HashMap of field name / field value pairs.
     */
    private void addCardsToAnkiDroid(final List<Map<String, String>> data) {
        //TODO: Pass in appropriate data
        //TODO: Test if it adds card, just on enter for now
        long deckId = getDeckId();
        long modelId = getModelId();
        String[] fieldNames = mAnkiDroid.getApi().getFieldList(modelId);
        // Build list of fields and tags
        LinkedList<String []> fields = new LinkedList<>();
        LinkedList<Set<String>> tags = new LinkedList<>();

        //TODO: Is it using a for loop instead of key value pairs to account for changes?
        for (Map<String, String> fieldMap: data) {
            // Build a field map accounting for the fact that the user could have changed the fields in the model
            String[] flds = new String[fieldNames.length];
            for (int i = 0; i < flds.length; i++) {
                // Fill up the fields one-by-one until either all fields are filled or we run out of fields to send
                if (i < AnkiDroidConfig.FIELDS.length) {
                    flds[i] = fieldMap.get(AnkiDroidConfig.FIELDS[i]);
                }
            }
            tags.add(AnkiDroidConfig.TAGS);
            fields.add(flds);
        }
        // Remove any duplicates from the LinkedLists and then add over the API
        mAnkiDroid.removeDuplicates(fields, tags, modelId);
        Context context = HomeActivity.this;
        //TODO: Add a string formatter for added, if there's ever cases for me to add multiple cards
        int added = mAnkiDroid.getApi().addNotes(modelId, deckId, fields, tags);
        //TODO: Change text to a string resource
        Toast.makeText(context, "Card added!", Toast.LENGTH_LONG).show();
    }

    private void addWordToAnki(View view){
        long deckId = getDeckId();
        long modelId = getModelId();
        String[] fieldNames = mAnkiDroid.getApi().getFieldList(modelId);
        String[] fields = new String[fieldNames.length];
        fields[AnkiDroidConfig.FIELDS_INDEX_KANJI] = mBinding.wordSearch.tvWord.getText().toString();
        fields[AnkiDroidConfig.FIELDS_INDEX_READING] = mBinding.wordSearch.tvWord.getText().toString();
        fields[AnkiDroidConfig.FIELDS_INDEX_MEANING] = mBinding.wordSearch.tvDefinition.getText().toString();
        fields[AnkiDroidConfig.FIELDS_INDEX_FURIGANA] = mBinding.wordSearch.tvWord.getText().toString();
        String notes = mBinding.wordSearch.etNotes.getText().toString();
        if(notes.equals(getResources().getString(R.string.notes_default))){
            notes = "";
        }
        fields[AnkiDroidConfig.FIELDS_INDEX_NOTES] = notes;
        String wordContext = mBinding.wordSearch.etContext.getText().toString();
        if(notes.equals(getResources().getString(R.string.context_default))){
            wordContext = "";
        }
        fields[AnkiDroidConfig.FIELDS_INDEX_CONTEXT] = wordContext;
        Set<String> tags = AnkiDroidConfig.TAGS;
        mAnkiDroid.getApi().addNote(modelId, deckId, fields, tags);
    }

}
