package com.waifusims.j_jlearnersdictionary;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.waifusims.j_jlearnersdictionary.databinding.ActivityHomeBinding;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import data.DictionaryType;
import data.JapaneseVocabulary;
import data.RelatedWordsContract;
import data.RelatedWordsDbHelper;
import data.SanseidoSearch;
import data.SanseidoSearchAsyncTaskLoader;
import data.VocabularyContract;
import data.VocabularyDbHelper;
import util.anki.AnkiDroidHelper;
import util.anki.AnkiDroidConfig;
import android.support.v4.app.LoaderManager;

import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.widget.Toast;

//TODO:  Horizontal UI
//TODO: OnPause, OnResume
public class HomeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<SanseidoSearch>{
    public static final String LOG_TAG = "Wanicchou";
    private static final int ADD_PERM_REQUEST = 0;
    private static final int HOME_ACTIVITY_REQUEST_CODE = 42;

    private static final String SEARCH_WORD_KEY = "search";
    private static final int SANSEIDO_SEARCH_LOADER = 322;

    private SQLiteDatabase mVocabDb;
    private SQLiteDatabase mRelatedWordsDb;
    private ActivityHomeBinding mBinding;
    private AnkiDroidHelper mAnkiDroid;
    private Toast mToast;

    private SanseidoSearch mLastSearched;
    // TODO: Update the DB onPause/SavedInstanceState/new search

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
                            Cursor cursor = getWordFromDb(searchWord);

                            if(cursor.moveToFirst()){

                                //TODO: Give option of web search
                                JapaneseVocabulary vocabulary = new JapaneseVocabulary(cursor);

                                mBinding.wordDefinition.tvWord.setText(vocabulary.getWord());
                                mBinding.wordDefinition.tvDefinition.setText(vocabulary.getDefintion());

                                mLastSearched = new SanseidoSearch(vocabulary,
                                        getExistingRelatedWordsFromDb(vocabulary.getWord()));



                                showAnkiRelatedUIElements();
                            }
                            else{
                                Bundle searchBundle = new Bundle();
                                searchBundle.putString(SEARCH_WORD_KEY, searchWord);
                                LoaderManager loaderManager = getSupportLoaderManager();
                                Loader<String> searchLoader =
                                        loaderManager.getLoader(SANSEIDO_SEARCH_LOADER);
                                if (searchLoader == null){
                                    loaderManager.initLoader(SANSEIDO_SEARCH_LOADER,
                                            searchBundle, HomeActivity.this);
                                }
                                else {
                                    loaderManager.restartLoader(SANSEIDO_SEARCH_LOADER,
                                            searchBundle, HomeActivity.this);
                                }
                            }
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


        VocabularyDbHelper vocabularyDbHelper = new VocabularyDbHelper(this);
        mVocabDb = vocabularyDbHelper.getWritableDatabase();
        RelatedWordsDbHelper relatedWordsDbHelper = new RelatedWordsDbHelper(this);
        mRelatedWordsDb = relatedWordsDbHelper.getWritableDatabase();

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
    protected void onDestroy() {
        super.onDestroy();
        mVocabDb.close();
        mRelatedWordsDb.close();
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

    @Override
    public Loader<SanseidoSearch> onCreateLoader(int id, final Bundle args) {
        return new SanseidoSearchAsyncTaskLoader(HomeActivity.this,
                args.getString(SEARCH_WORD_KEY));
    }

    @Override
    public void onLoadFinished(Loader<SanseidoSearch> loader, SanseidoSearch search) {

        if(mToast != null){
            mToast.cancel();
        }
        String message;
        final Context context = getApplicationContext();
        final int searchCompleteToastDuration = Toast.LENGTH_SHORT;

        if(search != null){
            if (!search.getVocabulary().getWord().equals("")) {
                mLastSearched = search;
                message = getString(R.string.word_search_success);

                JapaneseVocabulary vocabulary = search.getVocabulary();
                String definition = vocabulary.getDefintion();

                mBinding.wordDefinition.tvWord.setText(vocabulary.getWord());
                mBinding.wordDefinition.tvDefinition.setText(definition);

                Cursor cursor = getWordFromDb(vocabulary.getWord());

                if (!cursor.moveToFirst()) {
                    addWordToDb(search.getVocabulary());
                    addWordsToRelatedWordsDb(search.getVocabulary().getWord(),
                            search.getRelatedWords());
                }

                showAnkiRelatedUIElements();
            }
            else{

                // TODO: Add empty entry to db for failed searches that aren't network errors.
                // TODO: Probably a null somewhere since I do this
                JapaneseVocabulary invalidWord =
                        new JapaneseVocabulary(mBinding.wordSearch.etSearchBox
                                .getText().toString(), DictionaryType.JJ);

                Cursor cursor = getWordFromDb(invalidWord.getWord());

                if (!cursor.moveToFirst()) {
                    addWordToDb(search.getVocabulary());
                }

                // TODO: Maybe a different error message
                message = getString(R.string.word_search_failure);
            }


        }
        else{
            // TODO: Check for network and http request time outs
            message = getString(R.string.word_search_failure);
        }
        mToast = Toast.makeText(context, message, searchCompleteToastDuration);
        mToast.show();
    }

    // Required override but unused
    @Override
    public void onLoaderReset(Loader<SanseidoSearch> loader) {

    }


    /**
     * Helper class to hide and show Anki import related UI elements after a search is performed.
     */
    private void showAnkiRelatedUIElements(){
        mBinding.btnRelatedWords.setVisibility(View.VISIBLE);
        mBinding.fab.setVisibility(View.VISIBLE);
        mBinding.ankiAdditionalFields.tvContextLabel.setVisibility(View.VISIBLE);
        mBinding.ankiAdditionalFields.tvNotesLabel.setVisibility(View.VISIBLE);
        mBinding.ankiAdditionalFields.etContext.setVisibility(View.VISIBLE);
        mBinding.ankiAdditionalFields.etNotes.setVisibility(View.VISIBLE);
    }

    // TODO: Refactor everything so I either have related words on SQLite DB searches
    // Or return something different on web searches.


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
            if (mToast != null){
                mToast.cancel();
            }
            Context context = HomeActivity.this;
            String message = getString(R.string.toast_permissions_denied);
            int duration = Toast.LENGTH_SHORT;
            mToast = Toast.makeText(context, message, duration);
            mToast.show();
        }
    }

    // TODO: Move these methods to the Helper
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
        fields[AnkiDroidConfig.FIELDS_INDEX_DICTIONARY_TYPE] =
                mLastSearched.getVocabulary().getDictionaryType().toString();
        Set<String> tags = AnkiDroidConfig.TAGS;
        mAnkiDroid.getApi().addNote(modelId, deckId, fields, tags);

        mToast.cancel();
        Context context = HomeActivity.this;
        String message = getString(R.string.toast_anki_added);
        int duration = Toast.LENGTH_SHORT;
        mToast = Toast.makeText(context, message, duration);
        mToast.show();

    }

    private Map<String, Set<String> > getExistingRelatedWordsFromDb(String word){
        Cursor wordInVocabDb = getWordFromDb(word);
        if (!wordInVocabDb.moveToFirst()){
            return null;
        }
        int idIndex = wordInVocabDb
                .getColumnIndex(VocabularyContract.VocabularyEntry._ID);

        int fkBaseWordID = wordInVocabDb.getInt(idIndex);

        final String[] columns = {RelatedWordsContract.RelatedWordEntry.COLUMN_RELATED_WORD,
                RelatedWordsContract.RelatedWordEntry.COLUMN_DICTIONARY_TYPE};
        final String selection = RelatedWordsContract.RelatedWordEntry.FK_BASE_WORD+ "=?";
        final String[] selectionArgs = {String.valueOf(fkBaseWordID)};
        final String groupBy = null;
        final String having = null;

        Cursor existingRelatedWordsCursor = mRelatedWordsDb.query(
                RelatedWordsContract.RelatedWordEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                VocabularyContract.VocabularyEntry._ID
        );

        if(existingRelatedWordsCursor.moveToFirst()){
            Map<String, Set<String> > existingRelatedWordsMap = new HashMap<>();
            int relatedWordIndex =
                    existingRelatedWordsCursor.getColumnIndex(
                            RelatedWordsContract.RelatedWordEntry.COLUMN_RELATED_WORD);

            do {
                String relatedWord = existingRelatedWordsCursor.getString(relatedWordIndex);
                String dictionaryType = existingRelatedWordsCursor
                        .getString(existingRelatedWordsCursor
                                .getColumnIndex(RelatedWordsContract.RelatedWordEntry.COLUMN_DICTIONARY_TYPE));
                if(!existingRelatedWordsMap.containsKey(dictionaryType)){
                    Set<String> wordSet = new HashSet<>();
                    existingRelatedWordsMap.put(dictionaryType, wordSet);
                }
                existingRelatedWordsMap.get(dictionaryType).add(relatedWord);
            } while (existingRelatedWordsCursor.moveToNext());

            return existingRelatedWordsMap;
        }

        return new HashMap<String, Set<String> >();
    }

    private Cursor getWordFromDb(String word){
        final String[] columns = null;
        final String selection = VocabularyContract.VocabularyEntry.COLUMN_WORD + "=?";
        final String[] selectionArgs = {word};
        final String groupBy = null;
        final String having = null;

        return mVocabDb.query(
                VocabularyContract.VocabularyEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                VocabularyContract.VocabularyEntry._ID
        );
    }
    private long addWordsToRelatedWordsDb(String searchWord, Map<String, Set<String> > relatedWords){
        long numEntries = 0;

        Map<String, Set<String>> existingRelatedWordsMap = getExistingRelatedWordsFromDb(searchWord);
        Set<String> existingRelatedWords = new HashSet<>();
        if(existingRelatedWordsMap != null){
            for(String key : existingRelatedWordsMap.keySet()) {
                existingRelatedWords.addAll(existingRelatedWordsMap.get(key));
            }
        }

        //Word must exist in Vocab DB to add related words
        Cursor searchWordCursor = getWordFromDb(searchWord);
        searchWordCursor.moveToFirst();
        int fkSearchWordId = searchWordCursor.getInt(
                searchWordCursor.getColumnIndex(VocabularyContract.VocabularyEntry._ID));

        for (String dictionaryType : relatedWords.keySet()){
            for(String relatedWord : relatedWords.get(dictionaryType)){
                //Check if the entry exists, then insert it if it doesn't.
                if(!existingRelatedWords.contains(relatedWord)){
                    ContentValues relatedWordContentValues = new ContentValues();
                    relatedWordContentValues.put(RelatedWordsContract.RelatedWordEntry.FK_BASE_WORD, fkSearchWordId);
                    relatedWordContentValues.put(RelatedWordsContract.RelatedWordEntry.COLUMN_RELATED_WORD, relatedWord);
                    relatedWordContentValues.put(RelatedWordsContract.RelatedWordEntry.COLUMN_DICTIONARY_TYPE, dictionaryType);
                    numEntries = mRelatedWordsDb.insert(
                            RelatedWordsContract.RelatedWordEntry.TABLE_NAME,
                            null,
                            relatedWordContentValues);
                }
            }
        }
        return numEntries;
    }


    private long addWordToDb(JapaneseVocabulary vocab){
        ContentValues contentValues = buildVocabularyContentValues(vocab);

        return mVocabDb.insert(
                VocabularyContract.VocabularyEntry.TABLE_NAME,
                null,
                contentValues);
    }

    private int updateWordInDb(JapaneseVocabulary vocab){

        ContentValues contentValues = buildVocabularyContentValues(vocab);

        String whereClause = VocabularyContract.VocabularyEntry.COLUMN_WORD + "=" + vocab.getWord();
        String[] whereArgs = null;

        return mVocabDb.update(
                VocabularyContract.VocabularyEntry.TABLE_NAME,
                contentValues,
                whereClause,
                whereArgs
        );
    }

    private ContentValues buildVocabularyContentValues(JapaneseVocabulary vocab){
        ContentValues contentValues = new ContentValues();
        contentValues.put(VocabularyContract.VocabularyEntry.COLUMN_WORD, vocab.getWord());
        contentValues.put(VocabularyContract.VocabularyEntry.COLUMN_READING, vocab.getReading());
        contentValues.put(VocabularyContract.VocabularyEntry.COLUMN_DEFINITION, vocab.getDefintion());
        contentValues.put(VocabularyContract.VocabularyEntry.COLUMN_PITCH, vocab.getPitch());
        contentValues.put(VocabularyContract.VocabularyEntry.COLUMN_DICTIONARY_TYPE,
                vocab.getDictionaryType().toString());

        contentValues.put(VocabularyContract.VocabularyEntry.COLUMN_NOTES,
                mBinding.ankiAdditionalFields.etNotes.getText().toString());
        contentValues.put(VocabularyContract.VocabularyEntry.COLUMN_CONTEXT,
                mBinding.ankiAdditionalFields.etContext.getText().toString());
        return contentValues;
    }
}
