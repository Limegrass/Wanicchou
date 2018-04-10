package com.waifusims.j_jlearnersdictionary;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.waifusims.j_jlearnersdictionary.databinding.ActivityHomeBinding;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.JapaneseVocabulary;
import data.SanseidoSearch;
import util.anki.AnkiDroidHelper;
import util.anki.AnkiDroidConfig;

import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.widget.Toast;

//TODO: Move ListItemClickListener implementation to a different activity
public class HomeActivity extends AppCompatActivity {
    public static final String LOG_TAG = "JJLD";
    private static final int ADD_PERM_REQUEST = 0;

    private ActivityHomeBinding mBinding;
    private AnkiDroidHelper mAnkiDroid;
    private SanseidoSearch mLastSearched;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAnkiDroid = new AnkiDroidHelper(this);

        setContentView(R.layout.activity_home);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        mBinding.wordSearch.etSearchBox.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View searchBox, int keyCode, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    switch (keyCode){
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            String searchWord = mBinding.wordSearch.etSearchBox.getText().toString();
                            new SanseidoQueryTask().execute(searchWord);
                            return true;
                        default:
                            //TODO: Would returning true do anything undesired? Find out
                            return false;
                    }
                }
                return false;
            }
        });

        mBinding.btnRelatedWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentStartRelatedWordsActivity =
                        new Intent(getApplicationContext(), RelatedWordsActivity.class);
                intentStartRelatedWordsActivity
                        .putExtra(getString(R.string.key_related_words),
                        mLastSearched);
                startActivity(intentStartRelatedWordsActivity);

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


    }



    public class SanseidoQueryTask extends AsyncTask<String, Void, SanseidoSearch>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            final Context context = getApplicationContext();
            final String searchingText = getString(R.string.word_searching);
            final int searchingToastDuration = Toast.LENGTH_SHORT;

            mToast = Toast.makeText(context, searchingText, searchingToastDuration);
            mToast.show();
        }

        @Override
        protected SanseidoSearch doInBackground(String... searchWords) {
            String word= searchWords[0];
            SanseidoSearch search = null;
            try{
                search = new SanseidoSearch(word);
            }
            catch (IOException e){
                e.printStackTrace();
            }

            return search;
        }

        @Override
        protected void onPostExecute(SanseidoSearch search) {
            super.onPostExecute(search);
            mToast.cancel();
            String message;
            final Context context = getApplicationContext();
            final int searchCompleteToastDuration = Toast.LENGTH_SHORT;

            if(search!= null && !search.getVocabulary().getWord().equals("")){
                mLastSearched = search;
                message = getString(R.string.word_search_success);

                JapaneseVocabulary vocabulary = search.getVocabulary();
                String definition = vocabulary.getDefintion();

                mBinding.wordDefinition.tvWord.setText(search.getWordSource());
                mBinding.wordDefinition.tvDefinition.setText(definition);

                // TODO: LONG PRESS TO SEARCH FOR A RELATED
                for (String key : search.getRelatedWords().keySet()){
                    Set<String> relatedWords = search.getRelatedWords().get(key);
                    for (String relatedWord : relatedWords){
                        mBinding.garbage.append(key + " " + relatedWord + "\n");
                    }
                }

                mBinding.fab.setVisibility(View.VISIBLE);
                mBinding.btnRelatedWords.setVisibility(View.VISIBLE);

            }
            else{
                message = getString(R.string.word_search_failure);
            }
            mToast = Toast.makeText(context, message, searchCompleteToastDuration);
            mToast.show();
        }

    }


    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions,
                                            @NonNull int[] grantResults) {
        if (requestCode==ADD_PERM_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            addCardsToAnkiDroid(AnkiDroidConfig.getExampleData());
        } else {
            mToast.cancel();
            Context context = HomeActivity.this;
            String message = getString(R.string.toast_permissions_denied);
            int duration = Toast.LENGTH_SHORT;
            mToast = Toast.makeText(context, message, duration);
            mToast.show();
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

    //TODO: REMOVE IF NOT NECESSARY
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

    //TODO: Change click to expand a menu and add associated UI elements
    //TODO: Maybe implement a clozed type when sentence search is included
    /**
     * Use the instant-add API to add flashcards directly to AnkiDroid.
     * @param view the view the floating action button exists in
     */
    private void addWordToAnki(View view){
        long deckId = getDeckId();
        long modelId = getModelId();
        String[] fieldNames = mAnkiDroid.getApi().getFieldList(modelId);
        String[] fields = new String[fieldNames.length];
        fields[AnkiDroidConfig.FIELDS_INDEX_KANJI] = mBinding.wordDefinition.tvWord.getText().toString();
        fields[AnkiDroidConfig.FIELDS_INDEX_READING] = mBinding.wordDefinition.tvWord.getText().toString();

        // Anki uses HTML, so the newlines are not displayed without a double newline or a break
        String definition = mBinding.wordDefinition.tvDefinition.getText().toString();
        definition = definition.replaceAll("\n", "<br>");
        fields[AnkiDroidConfig.FIELDS_INDEX_DEFINITION] = definition;

        fields[AnkiDroidConfig.FIELDS_INDEX_FURIGANA] = mBinding.wordDefinition.tvWord.getText().toString();
        String notes = mBinding.ankiAdditionalFields.etNotes.getText().toString();
        fields[AnkiDroidConfig.FIELDS_INDEX_NOTES] = notes;
        String wordContext = mBinding.ankiAdditionalFields.etContext.getText().toString();
        fields[AnkiDroidConfig.FIELDS_INDEX_CONTEXT] = wordContext;
        Set<String> tags = AnkiDroidConfig.TAGS;
        mAnkiDroid.getApi().addNote(modelId, deckId, fields, tags);

        mToast.cancel();
        Context context = HomeActivity.this;
        String message = getString(R.string.toast_anki_added);
        int duration = Toast.LENGTH_SHORT;
        mToast = Toast.makeText(context, message, duration);
        mToast.show();

    }

}
