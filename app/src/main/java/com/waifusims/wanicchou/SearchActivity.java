package com.waifusims.wanicchou;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.waifusims.wanicchou.databinding.ActivitySearchBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import data.room.context.ContextViewModel;
import data.room.notes.NoteViewModel;
import data.room.rel.RelatedWordEntity;
import data.room.rel.RelatedWordViewModel;
import data.room.voc.VocabularyEntity;
import data.room.voc.VocabularyViewModel;
import data.vocab.DictionaryType;
import data.vocab.JapaneseVocabulary;
import data.vocab.MatchType;
import data.vocab.search.RelatedWordEntry;
import data.vocab.search.SanseidoSearch;
import data.vocab.search.SanseidoSearchAsyncTaskLoader;
import data.vocab.search.SanseidoSearchWebView;
import util.anki.AnkiDroidHelper;
import util.anki.AnkiDroidConfig;
import android.support.v4.app.LoaderManager;

import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

// TODO: Automatically select EJ for English input
//TODO:  Horizontal UI
public class SearchActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<SanseidoSearchWebView> {
    public static final String LOG_TAG = "Wanicchou";
    private static final int ADD_PERM_REQUEST = 0;
    private static final int HOME_ACTIVITY_REQUEST_CODE = 42;

    private static final String SEARCH_WORD_KEY = "search";
    private static final int SANSEIDO_SEARCH_LOADER = 322;

    private ActivitySearchBinding mBinding;
    private AnkiDroidHelper mAnkiDroid;
    private Toast mToast;
    private VocabularyViewModel mVocabViewModel;
    private RelatedWordViewModel mRelatedWordsViewModel;
    private NoteViewModel mNoteViewModel;
    private ContextViewModel mContextViewModel;

    private SanseidoSearch mLastSearched;

    /* ==================================== +Lifecycle ================================== */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAnkiDroid = new AnkiDroidHelper(this);
        setContentView(R.layout.activity_search);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        setUpClickListeners();
        setUpOnFocusChangeListeners();
        setUpKeyListeners();


        mVocabViewModel = ViewModelProviders.of(this).get(VocabularyViewModel.class);
        mRelatedWordsViewModel = ViewModelProviders.of(this).get(RelatedWordViewModel.class);
        mNoteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        mContextViewModel = ViewModelProviders.of(this).get(ContextViewModel.class);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.last_searched_key), mLastSearched);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLastSearched = savedInstanceState.getParcelable(getString(R.string.last_searched_key));
        showVocabOnUI();
        showAnkiRelatedUIElements();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mLastSearched != null){
            // Save the word so it can be retrieved and researched when the activity is returned.
            Context context = this;
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.search_word_key),
                    mLastSearched.getVocabulary().getWord());
            editor.putString(getString(R.string.dic_type_key),
                    mLastSearched.getVocabulary().getDictionaryType().toString());
            editor.apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Loader<SanseidoSearchWebView> loader =
                getSupportLoaderManager().getLoader(SANSEIDO_SEARCH_LOADER);
        if(loader != null){
            switch (loader.getId()){
                case SANSEIDO_SEARCH_LOADER:
                    SanseidoSearchAsyncTaskLoader ssLoader = (SanseidoSearchAsyncTaskLoader) loader;
                    //If the first load hasn't completed, we won't change the dictionary type
                    //so we don't make the loader dictionary type inconsistent with the search type
                    if(ssLoader.isFirstLoadFinished()){
                        ssLoader.changeDictionaryType(getCurrentDictionaryPreference());
                    }
                    break;
                default:
            }
        }
        Context context = SearchActivity.this;
        String stringIfMissing = "";
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String searchWord = sharedPreferences.getString(getString(R.string.search_word_key),
                stringIfMissing);
        String dicTypeKey = getString(R.string.dic_type_key);
        //Returns null if nothing saved
        String dicType = sharedPreferences.getString(dicTypeKey, stringIfMissing);
        DictionaryType dictionaryType = DictionaryType.fromString(dicType);
        if(!TextUtils.isEmpty(searchWord)){
            showWordFromDB(searchWord, dictionaryType);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == HOME_ACTIVITY_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    String key = getString(R.string.desired_related_word_key);
                    if (data.hasExtra(key)) {
                        String desiredRelatedWord =
                                data.getExtras().getString(key);
                        if(!TextUtils.isEmpty(desiredRelatedWord)){
                            mBinding.wordSearch.etSearchBox.setText(desiredRelatedWord);
                        }
                    }
                    if(mToast != null){
                        mToast.cancel();
                    }
                    // TODO: It's not actually doing the search right now
                    Context context = this;
                    String msg = getString(R.string.searching_related_toast);
                    int duration = Toast.LENGTH_SHORT;
                    mToast = Toast.makeText(context, msg, duration);
                    mToast.show();
                    break;
                default:
                    onResume();
            }
        }
    }

    /* ==================================== +Loader and +Search ================================== */

    @Override
    public Loader<SanseidoSearchWebView> onCreateLoader(int id, final Bundle args) {
        DictionaryType dictionaryType = getCurrentDictionaryPreference();
        MatchType matchType = getCurrentMatchType();
        return new SanseidoSearchAsyncTaskLoader(this,
                args.getString(SEARCH_WORD_KEY),
                dictionaryType,
                matchType
        );
    }

    //TODO: Make this stop executing coming back from SettingsActivity/(OnResume?)
    @Override
    public void onLoadFinished(Loader<SanseidoSearchWebView> loader, SanseidoSearchWebView search) {
        switch(loader.getId()){
            case SANSEIDO_SEARCH_LOADER:
                if(mToast != null){
                    mToast.cancel();
                }
                String message;
                final Context context = getApplicationContext();
                final int searchCompleteToastDuration = Toast.LENGTH_SHORT;

                SanseidoSearchAsyncTaskLoader ssLoader = (SanseidoSearchAsyncTaskLoader)loader;
                if(search != null){
                    if(ssLoader.isFirstLoadFinished()){
                        if (!TextUtils.isEmpty(search.getVocabulary().getWord())) {
                            mLastSearched = search;
                            message = getString(R.string.word_search_success);

                            showVocabOnUI();

                            VocabularyEntity entity =
                                    getWordFromDb(search.getVocabulary().getWord(),
                                            search.getVocabulary().getDictionaryType());

                            if (entity == null) {
                                if(mVocabViewModel.insert(search.getVocabulary())) {
                                    addWordsToRelatedWordsDb(search.getVocabulary(),
                                            search.getRelatedWords());

                                    mNoteViewModel.insertNewNote(search.getVocabulary().getWord());
                                    mContextViewModel.insertNewContext(search.getVocabulary().getWord());
                                }
                            }
                            else{
                                String note = mNoteViewModel.getNoteOf(entity.getWord());
                                mBinding.ankiAdditionalFields.etNotes.setText(note);
                                mBinding.ankiAdditionalFields.tvNotes.setText(note);

                                String wordContext = mContextViewModel.getContextOf(entity.getWord());
                                mBinding.ankiAdditionalFields.etContext.setText(wordContext);
                                mBinding.ankiAdditionalFields.tvContext.setText(wordContext);

                            }

                            showAnkiRelatedUIElements();
                        }
                        else{
                            // TODO: Add empty entry to db for failed searches that aren't network errors.
                            // TODO: Probably a null somewhere since I do this
                            DictionaryType dictionaryType = ssLoader.getDictionaryType();
                            JapaneseVocabulary invalidWord =
                                    new JapaneseVocabulary(mBinding.wordSearch.etSearchBox
                                            .getText().toString(), dictionaryType);

                            VocabularyEntity entity =
                                    getWordFromDb(search.getVocabulary().getWord(), dictionaryType);
                            if (entity == null) {
                                mVocabViewModel.insert(invalidWord);
                            }
                            // TODO: Maybe a different error message
                            message = getString(R.string.word_search_failure);
                        }
                        ssLoader.setFirstLoadFinished(false);
                        mToast = Toast.makeText(context, message, searchCompleteToastDuration);
                        mToast.show();
                    }
                }
                else{
                    if(ssLoader.isFirstLoadFinished()) {
                        // TODO: Check for network and http request time outs
                        message = getString(R.string.word_search_failure);
                        ssLoader.setFirstLoadFinished(false);
                        mToast = Toast.makeText(context, message, searchCompleteToastDuration);
                        mToast.show();
                    }
                }
                break;
            default:
                return;

        }

    }

    // Required override but unused
    @Override
    public void onLoaderReset(Loader<SanseidoSearchWebView> loader) {
    }


    /* ==================================== +Databases ================================== */

    private List<RelatedWordEntry> getExistingRelatedWordsFromDb(String word){
        VocabularyEntity entity = mVocabViewModel.getWord(word, getCurrentDictionaryPreference());
        if (entity == null){
            return null;
        }


        List<RelatedWordEntry> relatedWords = new ArrayList<>();
        for (DictionaryType dictionaryType : DictionaryType.values()){
            List<RelatedWordEntity> relatedWordEntities =
                    mRelatedWordsViewModel.getRelatedWordList(entity, dictionaryType);
            for(RelatedWordEntity relatedWordEntity : relatedWordEntities){
                relatedWords.add(new RelatedWordEntry(relatedWordEntity.getRelatedWord(), relatedWordEntity.getDictionaryType()));
            }
        }
        return relatedWords;
    }

    private void addWordsToRelatedWordsDb(JapaneseVocabulary vocabulary, List<RelatedWordEntry> newRelatedWords){
        VocabularyEntity entity = getWordFromDb(vocabulary.getWord(), vocabulary.getDictionaryType());
        if (entity == null){
            return;
        }
        for (RelatedWordEntry entry : newRelatedWords){
            RelatedWordEntity relatedWordToAdd =
                    new RelatedWordEntity(entity, entry.getRelatedWord(),
                            entry.getDictionaryType().toString());
            mRelatedWordsViewModel.insert(relatedWordToAdd);
        }
    }

    private VocabularyEntity getWordFromDb(String word, DictionaryType dictionaryType) {
        return mVocabViewModel.getWord(word, dictionaryType);
    }


    // TODO: Redo this method because it doesn't do anything, call it in onFocusChanged for tvDefinition
    private void updateWordInDb(JapaneseVocabulary vocab){
        //I need the ID, so I have to DB query. Could work around if I save ID
        VocabularyEntity wordInDb = getWordFromDb(vocab.getWord(), vocab.getDictionaryType());
        if (wordInDb == null){
            return;
        }
        wordInDb.setWord(vocab.getWord());
        wordInDb.setDefinition(vocab.getDefintion());
        wordInDb.setReading(vocab.getReading());
        wordInDb.setPitch(vocab.getPitch());
        wordInDb.setDictionaryType(vocab.getDictionaryType().toString());
        mVocabViewModel.update(wordInDb);
    }

    private boolean showWordFromDB(String searchWord, DictionaryType dictionaryType){
        VocabularyEntity vocabEntity = getWordFromDb(searchWord, dictionaryType);
        //TODO: Give option of web search
        if(vocabEntity != null){
            if (DictionaryType.fromString(vocabEntity.getDictionaryType()) != getCurrentDictionaryPreference()){
                return false;
            }

            JapaneseVocabulary vocabulary = new JapaneseVocabulary(vocabEntity);


            String note = mNoteViewModel.getNoteOf(vocabEntity.getWord());
            String wordContext = mContextViewModel.getContextOf(vocabEntity.getWord());

            mBinding.ankiAdditionalFields.tvNotes.setText(note);
            mBinding.ankiAdditionalFields.etNotes.setText(note);
            mBinding.ankiAdditionalFields.tvContext.setText(wordContext);
            mBinding.ankiAdditionalFields.etContext.setText(wordContext);

            mLastSearched = new SanseidoSearch(vocabulary,
                    getExistingRelatedWordsFromDb(vocabulary.getWord()));
            showVocabOnUI();
            showAnkiRelatedUIElements();
            return true;
        }
        return false;
    }

    private void updateNote(){
        mNoteViewModel.updateNote(mLastSearched.getVocabulary().getWord(),
                mBinding.ankiAdditionalFields.tvNotes.getText().toString());
    }

    private void updateContext(){
        mContextViewModel.updateContext(mLastSearched.getVocabulary().getWord(),
                mBinding.ankiAdditionalFields.tvContext.getText().toString());
    }

    private DictionaryType getCurrentDictionaryPreference(){
        Context context = this;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String dictionaryTypeString = sharedPreferences.getString(
                getString(R.string.pref_dictionary_type_key),
                getString(R.string.pref_dictionary_type_default)
        );
        return DictionaryType.fromString(dictionaryTypeString);
    }

    private MatchType getCurrentMatchType(){
        Context context = this;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String matchTypeString = sharedPreferences.getString(
                getString(R.string.pref_match_type_key),
                getString(R.string.pref_match_type_default)
        );
        return MatchType.fromString(matchTypeString);
    }

    /* ==================================== +UI and +Helpers ================================== */
    /**
     * Helper class to hide and show Anki import related UI elements after a search is performed.
     */
    private void showAnkiRelatedUIElements(){
        mBinding.btnRelatedWords.setVisibility(View.VISIBLE);
        mBinding.fab.setVisibility(View.VISIBLE);
        mBinding.ankiAdditionalFields.viewAnkiFields.setVisibility(View.VISIBLE);
    }

    private void showVocabOnUI(){
        JapaneseVocabulary vocabulary = mLastSearched.getVocabulary();
        String definition = vocabulary.getDefintion();
        mBinding.wordDefinition.tvWord.setText(vocabulary.getWord());
        mBinding.wordDefinition.tvDefinition.setText(definition);
        mBinding.wordDefinition.etDefinition.setText(definition);
    }

    private void clearAnkiFields(){

        mBinding.ankiAdditionalFields.tvContext.setText("");
        mBinding.ankiAdditionalFields.etContext.setText("");
        mBinding.ankiAdditionalFields.tvNotes.setText("");
        mBinding.ankiAdditionalFields.etNotes.setText("");
    }

    private void startSearchLoader(String searchWord){
        if(!showWordFromDB(searchWord, getCurrentDictionaryPreference())) {
            Bundle searchBundle = new Bundle();
            searchBundle.putString(SEARCH_WORD_KEY, searchWord);
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<SanseidoSearchWebView> searchLoader =
                    loaderManager.getLoader(SANSEIDO_SEARCH_LOADER);
            if (searchLoader == null) {
                loaderManager.initLoader(SANSEIDO_SEARCH_LOADER,
                        searchBundle, SearchActivity.this);
            } else {
                ((SanseidoSearchAsyncTaskLoader) searchLoader).changeDictionaryType(getCurrentDictionaryPreference());
                loaderManager.restartLoader(SANSEIDO_SEARCH_LOADER,
                        searchBundle, SearchActivity.this);
            }

            Context context = SearchActivity.this;
            final String searchingText = getString(R.string.word_searching);
            final int searchingToastDuration = Toast.LENGTH_LONG;

            mToast = Toast.makeText(context, searchingText, searchingToastDuration);
            mToast.show();
        }
    }
    //TODO: Change click to expand a menu and add associated UI elements
    //TODO: Maybe implement a clozed type when sentence search is included
    //TODO: Duplicate checking
    /**
     * Use the instant-add API to add flashcards directly to AnkiDroid.
     */
    private void addWordToAnki(){
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
        String message = getString(R.string.anki_added_toast);
        int duration = Toast.LENGTH_SHORT;
        mToast = Toast.makeText(context, message, duration);
        mToast.show();
    }

    private void setUpOnFocusChangeListeners(){
        mBinding.wordDefinition.etDefinition.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    String changedText = mBinding.wordDefinition.etDefinition.getText().toString();
                    mBinding.wordDefinition.tvDefinition.setText(changedText);
                    mBinding.wordDefinition.vsDefinition.showNext();
                    InputMethodManager inputMethodManager =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(
                            mBinding.ankiAdditionalFields.etNotes.getWindowToken(),
                            0
                    );
                }
            }
        });

        mBinding.ankiAdditionalFields.etContext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    String changedText =
                            mBinding.ankiAdditionalFields.etContext.getText().toString();
                    mBinding.ankiAdditionalFields.tvContext.setText(changedText);
                    mBinding.ankiAdditionalFields.vsContext.showNext();
                    updateContext();
                    InputMethodManager inputMethodManager =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(
                            mBinding.ankiAdditionalFields.etNotes.getWindowToken(),
                            0
                    );
                }
            }
        });

        mBinding.ankiAdditionalFields.etNotes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    String changedText = mBinding.ankiAdditionalFields.etNotes.getText().toString();
                    mBinding.ankiAdditionalFields.tvNotes.setText(changedText);
                    mBinding.ankiAdditionalFields.vsNotes.showNext();
                    updateNote();
                    InputMethodManager inputMethodManager =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(
                            mBinding.ankiAdditionalFields.etNotes.getWindowToken(),
                            0
                    );

                }
            }
        });
    }

    private void setUpClickListeners(){
        // Definition TV/ET
        mBinding.wordDefinition.tvDefinition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.wordDefinition.vsDefinition.showNext();
                mBinding.wordDefinition.etDefinition.requestFocus();
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(mBinding.wordDefinition.etDefinition,
                        InputMethodManager.SHOW_IMPLICIT);
            }
        });


        // Context Label, TV, and ET
        mBinding.ankiAdditionalFields.tvContextLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.ankiAdditionalFields.vsContext.showNext();
                mBinding.ankiAdditionalFields.etContext.requestFocus();
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(mBinding.ankiAdditionalFields.etContext,
                        InputMethodManager.SHOW_IMPLICIT);
            }
        });

        mBinding.ankiAdditionalFields.tvContext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.ankiAdditionalFields.vsContext.showNext();
                mBinding.ankiAdditionalFields.etContext.requestFocus();
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(mBinding.ankiAdditionalFields.etContext,
                        InputMethodManager.SHOW_IMPLICIT);
            }
        });



        // Notes label, TV, ET
        mBinding.ankiAdditionalFields.tvNotesLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.ankiAdditionalFields.vsNotes.showNext();
                mBinding.ankiAdditionalFields.etNotes.requestFocus();
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(mBinding.ankiAdditionalFields.etNotes,
                        InputMethodManager.SHOW_IMPLICIT);
            }
        });
        mBinding.ankiAdditionalFields.tvNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.ankiAdditionalFields.vsNotes.showNext();
                mBinding.ankiAdditionalFields.etNotes.requestFocus();
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(mBinding.ankiAdditionalFields.etNotes,
                        InputMethodManager.SHOW_IMPLICIT);
            }
        });



        mBinding.btnRelatedWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentStartRelatedWordsActivity =
                        new Intent(getApplicationContext(), WordListActivity.class);
                intentStartRelatedWordsActivity
                        .putExtra(getString(R.string.related_word_key),
                                mLastSearched);
                startActivityForResult(intentStartRelatedWordsActivity, HOME_ACTIVITY_REQUEST_CODE);

                // Unless a new word was selected from the child activity

            }
        });

        //TODO: Fix it so it requests on add card, not on start up. Avoid crashes before addition.

        FloatingActionButton fab = findViewById(R.id.fab);
        // TODO: Hide if Anki is not installed.
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (mAnkiDroid.shouldRequestPermission()) {
                    mAnkiDroid.requestPermission(SearchActivity.this, ADD_PERM_REQUEST);
                    return;
                }
                addWordToAnki();
            }
        });
    }

    private void setUpKeyListeners(){
        mBinding.wordSearch.etSearchBox.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View searchBox, int keyCode, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    switch (keyCode){
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            String searchWord = mBinding.wordSearch.etSearchBox.getText().toString();
                            clearAnkiFields();
                            // If the word is not in the DB, we need to make a search

                            startSearchLoader(searchWord);

                            return true;
                        default:
                            //TODO: Would returning true do anything undesired? Find out
                            return false;
                    }
                }
                return false;
            }
        });
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

        switch (itemId){
            case R.id.action_settings:
                Context context = this;
                Class childActivity = SettingsActivity.class;
                Intent startSettingsActivityIntent = new Intent(context, childActivity);
                startActivity(startSettingsActivityIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            String message = getString(R.string.permissions_denied_toast);
            int duration = Toast.LENGTH_SHORT;
            mToast = Toast.makeText(context, message, duration);
            mToast.show();
        }
    }
}
