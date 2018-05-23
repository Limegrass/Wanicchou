package com.waifusims.j_jlearnersdictionary;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.waifusims.j_jlearnersdictionary.databinding.ActivityHomeBinding;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import data.room.VocabularyEntity;
import data.room.VocabularyRepository;
import data.room.VocabularyViewModel;
import data.vocab.DictionaryType;
import data.vocab.JapaneseVocabulary;
import data.db.RelatedWordsContract;
import data.db.RelatedWordsDbHelper;
import data.vocab.search.SanseidoSearch;
import data.vocab.search.SanseidoSearchAsyncTaskLoader;
import data.db.VocabularyContract;
import data.db.VocabularyDbHelper;
import util.anki.AnkiDroidHelper;
import util.anki.AnkiDroidConfig;
import android.support.v4.app.LoaderManager;

import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.widget.Toast;

//TODO:  Horizontal UI
public class SearchActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<SanseidoSearch> {
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
    private VocabularyViewModel mVocabViewModel;

    private SanseidoSearch mLastSearched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAnkiDroid = new AnkiDroidHelper(this);
        setContentView(R.layout.activity_home);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        setUpClickListeners();

        VocabularyDbHelper vocabularyDbHelper = new VocabularyDbHelper(this);
        mVocabDb = vocabularyDbHelper.getWritableDatabase();
        RelatedWordsDbHelper relatedWordsDbHelper = new RelatedWordsDbHelper(this);
        mRelatedWordsDb = relatedWordsDbHelper.getWritableDatabase();

//        mRepo = new VocabularyRepository(getApplication());
        mVocabViewModel = ViewModelProviders.of(this).get(VocabularyViewModel.class);
//        mVocabViewModel.getAllWords().observe(this, new Observer<List<VocabularyEntity>>() {
//            @Override
//            public void onChanged(@Nullable List<VocabularyEntity> vocabularyEntities) {
//
//            }
//        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.key_last_searched), mLastSearched);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLastSearched = savedInstanceState.getParcelable(getString(R.string.key_last_searched));
        showVocabOnUI();
        showAnkiRelatedUIElements();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mLastSearched != null){
            // Save the current state of the word entry to the DB
            updateWordInDb(mLastSearched.getVocabulary());

            // Save the word so it can be retrieved and researched when the activity is returned.
            Context context = this;
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.key_search_word),
                    mLastSearched.getVocabulary().getWord());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Loader<SanseidoSearch> loader =
                getSupportLoaderManager().getLoader(SANSEIDO_SEARCH_LOADER);
        if(loader != null){
            SanseidoSearchAsyncTaskLoader loaderCast = (SanseidoSearchAsyncTaskLoader) loader;
            loaderCast.changeDictionaryType(getCurrentDictionaryPreference());
        }
        Context context = SearchActivity.this;
        String stringIfMissing = "";
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String searchWord = sharedPreferences.getString(getString(R.string.key_search_word),
                stringIfMissing);
        DictionaryType dictionaryType = getCurrentDictionaryPreference();
        if(!TextUtils.isEmpty(searchWord)){
            showWordFromDB(searchWord, dictionaryType);
        }
    }

    @Override
    protected void onDestroy() {
        mVocabDb.close();
        mRelatedWordsDb.close();
        super.onDestroy();
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
                    if(mToast != null){
                        mToast.cancel();
                    }
                    // TODO: It's not actually doing the search right now
                    Context context = this;
                    String msg = getString(R.string.toast_searching_related);
                    int duration = Toast.LENGTH_SHORT;
                    mToast = Toast.makeText(context, msg, duration);
                    mToast.show();
                    break;
                default:
                    onResume();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Context context = this;
        Class childActivity = SettingsActivity.class;
        if (itemId == R.id.action_settings) {
            Intent startSettingsActivityIntent = new Intent(context, childActivity);
            startActivity(startSettingsActivityIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<SanseidoSearch> onCreateLoader(int id, final Bundle args) {
        Context context = SearchActivity.this;
        DictionaryType dictionaryType = getCurrentDictionaryPreference();
        return new SanseidoSearchAsyncTaskLoader(context,
                args.getString(SEARCH_WORD_KEY),
                dictionaryType
        );
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
            if (!TextUtils.isEmpty(search.getVocabulary().getWord())) {
                mLastSearched = search;
                message = getString(R.string.word_search_success);

                showVocabOnUI();

                //TODO : Get Dic Type at Search time
                VocabularyEntity entity = getWordFromDb(search.getVocabulary().getWord());

                if (entity == null ||
                        search.getVocabulary().getDictionaryType() != getCurrentDictionaryPreference()) {
                    addWordToDb(search.getVocabulary());
                    addWordsToRelatedWordsDb(search.getVocabulary().getWord(),
                            search.getRelatedWords());
                }

                showAnkiRelatedUIElements();
            }
            else{

                // TODO: Add empty entry to db for failed searches that aren't network errors.
                // TODO: Probably a null somewhere since I do this
                // TODO: Get dictionaryType at time of search request
                DictionaryType dictionaryType = getCurrentDictionaryPreference();
                JapaneseVocabulary invalidWord =
                        new JapaneseVocabulary(mBinding.wordSearch.etSearchBox
                                .getText().toString(), dictionaryType);

                VocabularyEntity entity = getWordFromDb(search.getVocabulary().getWord());

                if (entity == null) {
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
            Context context = SearchActivity.this;
            String message = getString(R.string.toast_permissions_denied);
            int duration = Toast.LENGTH_SHORT;
            mToast = Toast.makeText(context, message, duration);
            mToast.show();
        }
    }

    private Map<String, Set<String> > getExistingRelatedWordsFromDb(String word){
        VocabularyEntity entity = mVocabViewModel.getWord(word, getCurrentDictionaryPreference());
        if (entity == null){
            return null;
        }

        int fkBaseWordID = entity.id;

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

        return new HashMap<>();
    }

    //TODO Update DB related methods
    //Check List: Get Word From DB, getExistingRelated, addWordstoRelated,UpdatedInDb, showFromDB
    private VocabularyEntity getWordFromDb(String word) {
        VocabularyEntity entity = null;
        entity = mVocabViewModel.getWord(word, getCurrentDictionaryPreference());
        return entity;
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

        VocabularyEntity entity = getWordFromDb(searchWord);
        if(entity == null){
            return -1;
        }

        int fkSearchWordId = entity.id;

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

    private void addWordToDb(JapaneseVocabulary vocab){
        String notes = mBinding.ankiAdditionalFields.etNotes.getText().toString();
        String wordContext = mBinding.ankiAdditionalFields.etContext.getText().toString();

        VocabularyEntity vocabularyEntity = new VocabularyEntity(vocab, notes, wordContext);
        mVocabViewModel.insert(vocabularyEntity);
//        mRepo.insert(vocabularyEntity);
    }

    private int updateWordInDb(JapaneseVocabulary vocab){

        ContentValues contentValues = buildVocabularyContentValues(vocab);

        String whereClause = VocabularyContract.VocabularyEntry.COLUMN_WORD + "=?";
        String[] whereArgs = {vocab.getWord()};

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

    private void setUpClickListeners(){
        mBinding.wordSearch.etSearchBox.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View searchBox, int keyCode, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    switch (keyCode){
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            String searchWord = mBinding.wordSearch.etSearchBox.getText().toString();
                            DictionaryType requestedDictionaryType = getCurrentDictionaryPreference();

                            // If the word is not in the DB, we need to make a search
                            if(!showWordFromDB(searchWord, requestedDictionaryType)) {
                                Bundle searchBundle = new Bundle();
                                searchBundle.putString(SEARCH_WORD_KEY, searchWord);
                                LoaderManager loaderManager = getSupportLoaderManager();
                                Loader<String> searchLoader =
                                        loaderManager.getLoader(SANSEIDO_SEARCH_LOADER);
                                if (searchLoader == null){
                                    loaderManager.initLoader(SANSEIDO_SEARCH_LOADER,
                                            searchBundle, SearchActivity.this);
                                }
                                else {
                                    loaderManager.restartLoader(SANSEIDO_SEARCH_LOADER,
                                            searchBundle, SearchActivity.this);
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
                        new Intent(getApplicationContext(), WordListActivity.class);
                intentStartRelatedWordsActivity
                        .putExtra(getString(R.string.key_related_words),
                                mLastSearched);
                startActivityForResult(intentStartRelatedWordsActivity, HOME_ACTIVITY_REQUEST_CODE);

                // Unless a new word was selected from the child activity

            }
        });

        //TODO: Fix it so it requests on add card, not on start up. Avoid crashes before addition.
        if (mAnkiDroid.shouldRequestPermission()) {
            mAnkiDroid.requestPermission(SearchActivity.this, ADD_PERM_REQUEST);
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                addWordToAnki();
            }
        });
    }

    private boolean showWordFromDB(String searchWord, DictionaryType dictionaryType){
        VocabularyEntity entity = getWordFromDb(searchWord);
        //TODO: Give option of web search
        if(entity != null){
            JapaneseVocabulary vocabulary = new JapaneseVocabulary(entity);

            mLastSearched = new SanseidoSearch(vocabulary,
                    getExistingRelatedWordsFromDb(vocabulary.getWord()));
            showVocabOnUI();
            showAnkiRelatedUIElements();
            return true;
        }
        return false;
    }

    private DictionaryType getCurrentDictionaryPreference(){
        Context context = this;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String dictionaryTypeString = sharedPreferences.getString(
                getString(R.string.key_pref_dictionary_type),
                getString(R.string.default_pref_dictionary_type)
        );
        DictionaryType dictionaryType = DictionaryType.fromSanseidoKey(dictionaryTypeString);
        return dictionaryType;
    }

    private void showVocabOnUI(){
        JapaneseVocabulary vocabulary = mLastSearched.getVocabulary();
        String definition = vocabulary.getDefintion();

        mBinding.wordDefinition.tvWord.setText(vocabulary.getWord());
        mBinding.wordDefinition.tvDefinition.setText(definition);
    }

    //TODO: Change click to expand a menu and add associated UI elements
    //TODO: Maybe implement a clozed type when sentence search is included
    //TODO: Duplicate checking
    /**
     * Use the instant-add API to add flashcards directly to AnkiDroid.
     */
    public void addWordToAnki(){
        long deckId = mAnkiDroid.getDeckId();
        long modelId = mAnkiDroid.getModelId();
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
        Context context = SearchActivity.this;
        String message = getString(R.string.toast_anki_added);
        int duration = Toast.LENGTH_SHORT;
        mToast = Toast.makeText(context, message, duration);
        mToast.show();
    }
}
