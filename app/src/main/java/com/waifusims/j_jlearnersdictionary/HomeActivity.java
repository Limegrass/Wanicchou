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
import java.util.Set;

import data.JapaneseVocabulary;
import data.SanseidoSearch;
import util.anki.AnkiDroidHelper;
import util.anki.AnkiDroidConfig;

import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.widget.Toast;

//TODO:  Horizontal UI
//TODO: OnPause, OnResume
public class HomeActivity extends AppCompatActivity {
    public static final String LOG_TAG = "JJLD";
    private static final int ADD_PERM_REQUEST = 0;
    private static final int HOME_ACTIVITY_REQUEST_CODE = 42;

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
                startActivityForResult(intentStartRelatedWordsActivity, HOME_ACTIVITY_REQUEST_CODE);

                //TODO: Keep searched word information when coming back from child activity
                // Unless a new word was selected from the child activity

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
                addWordToAnki();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == HOME_ACTIVITY_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    String key = getString(R.string.key_desired_related_word);
                    if (data.hasExtra(key)) {
                        String desiredRelatedWord =
                                data.getExtras().getString(key);
                        mBinding.wordSearch.etSearchBox.setText(desiredRelatedWord);
                        new SanseidoQueryTask().execute(desiredRelatedWord);
                    }
                    mToast.cancel();
                    Context context = this;
                    String msg = getString(R.string.toast_searching_related);
                    int duration = Toast.LENGTH_SHORT;
                    mToast = Toast.makeText(context, msg, duration);
                    mToast.show();
                    break;
                default:
            }
        }
    }

    /**
     * Helper class to perform internet tasks on a background thread using AsyncTask.
     */
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

                showAnkiRelatedUIElements();

            }
            else{
                message = getString(R.string.word_search_failure);
            }
            mToast = Toast.makeText(context, message, searchCompleteToastDuration);
            mToast.show();
        }

    }

    /**
     * Helper class to hide and show Anki import related UI elements after a search is performed.
     */
    private void showAnkiRelatedUIElements(){
        mBinding.fab.setVisibility(View.VISIBLE);
        mBinding.btnRelatedWords.setVisibility(View.VISIBLE);
        mBinding.ankiAdditionalFields.tvContextLabel.setVisibility(View.VISIBLE);
        mBinding.ankiAdditionalFields.tvNotesLabel.setVisibility(View.VISIBLE);
        mBinding.ankiAdditionalFields.etContext.setVisibility(View.VISIBLE);
        mBinding.ankiAdditionalFields.etNotes.setVisibility(View.VISIBLE);
    }


    /**
     * Helper method to add cards to Anki if permission is granted
     * @param requestCode a signifier request code
     * @param permissions the permissions desired
     * @param grantResults if the permissions were granted
     */
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions,
                                            @NonNull int[] grantResults) {
        if (requestCode==ADD_PERM_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            addWordToAnki();
        } else {
            mToast.cancel();
            Context context = HomeActivity.this;
            String message = getString(R.string.toast_permissions_denied);
            int duration = Toast.LENGTH_SHORT;
            mToast = Toast.makeText(context, message, duration);
            mToast.show();
        }
    }

    /**
     * helper method to retrieve the deck ID for JJLD
     * @return the deck ID for JJLD in Anki
     */
    private long getDeckId() {
        Long did = mAnkiDroid.findDeckIdByName(AnkiDroidConfig.DECK_NAME);
        if (did == null) {
            did = mAnkiDroid.getApi().addNewDeck(AnkiDroidConfig.DECK_NAME);
            mAnkiDroid.storeDeckReference(AnkiDroidConfig.DECK_NAME, did);
        }
        return did;
    }

    /**
     * helper method to retrieve the model ID for JJLD
     * @return the model ID for JJLD in Anki
     */
    private long getModelId() {
        Long mid = mAnkiDroid.findModelIdByName(AnkiDroidConfig.MODEL_NAME, AnkiDroidConfig.FIELDS.length);
        if (mid == null) {
            mid = mAnkiDroid.getApi().addNewCustomModel(AnkiDroidConfig.MODEL_NAME, AnkiDroidConfig.FIELDS,
                    AnkiDroidConfig.CARD_NAMES, AnkiDroidConfig.QFMT, AnkiDroidConfig.AFMT, AnkiDroidConfig.CSS, getDeckId(), null);
            mAnkiDroid.storeModelReference(AnkiDroidConfig.MODEL_NAME, mid);
        }
        return mid;
    }


    //TODO: Change click to expand a menu and add associated UI elements
    //TODO: Maybe implement a clozed type when sentence search is included
    //TODO: Duplicate checking
    /**
     * Use the instant-add API to add flashcards directly to AnkiDroid.
     */
    private void addWordToAnki(){
        long deckId = getDeckId();
        long modelId = getModelId();
        String[] fieldNames = mAnkiDroid.getApi().getFieldList(modelId);
        String[] fields = new String[fieldNames.length];
        fields[AnkiDroidConfig.FIELDS_INDEX_WORD] = mLastSearched.getVocabulary().getWord();
        fields[AnkiDroidConfig.FIELDS_INDEX_READING] = mLastSearched.getVocabulary().getReading();

        // Anki uses HTML, so the newlines are not displayed without a double newline or a break
        String definition = mBinding.wordDefinition.tvDefinition.getText().toString();
        definition = definition.replaceAll("\n", "<br>");
        fields[AnkiDroidConfig.FIELDS_INDEX_DEFINITION] = definition;

        fields[AnkiDroidConfig.FIELDS_INDEX_FURIGANA] = mLastSearched.getVocabulary().getFurigana();
        fields[AnkiDroidConfig.FIELDS_INDEX_PITCH] = mLastSearched.getVocabulary().getPitch();

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
