package com.waifusims.wanicchou;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.waifusims.wanicchou.databinding.ActivitySearchBinding;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import data.room.WanicchouDatabase;
import data.room.context.ContextViewModel;
import data.room.notes.NoteViewModel;
import data.room.rel.RelatedWordEntity;
import data.room.rel.RelatedWordViewModel;
import data.room.voc.VocabularyEntity;
import data.room.voc.VocabularyViewModel;
import data.vocab.jp.search.sanseido.SanseidoSearchResult;
import data.vocab.models.DictionaryType;
import data.vocab.models.DictionaryTypes;
import data.vocab.models.DictionaryWebPage;
import data.vocab.jp.JapaneseVocabulary;
import data.vocab.OnJavaScriptCompleted;
import data.vocab.WordListEntry;
import data.vocab.models.SearchProvider;
import data.vocab.models.SearchResult;
import data.vocab.models.Vocabulary;
import util.anki.AnkiDroidHelper;

// TODO: Automatically select EJ for English input
//TODO:  Horizontal UI
// TODO: Toasts for DB searches
//TODO : Add click listener for Def label
public class SearchActivity extends AppCompatActivity
        implements OnJavaScriptCompleted {
    public static final String LOG_TAG = "Wanicchou";
    private static final int ADD_PERM_REQUEST = 0;
    private static final int SEARCH_ACTIVITY_REQUEST_CODE = 42;

    private ActivitySearchBinding mBinding;
    private AnkiDroidHelper mAnkiDroid;
    private Toast mToast;
    private VocabularyViewModel mVocabViewModel;
    private RelatedWordViewModel mRelatedWordViewModel;
    private NoteViewModel mNoteViewModel;
    private ContextViewModel mContextViewModel;
    private WanicchouSharedPreferencesHelper sharedPreferencesHelper;

    private SearchResult mLastSearched;
    private DictionaryWebPage mWebPage;

    /* ==================================== +Lifecycle ================================== */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        Context context = SearchActivity.this;
        sharedPreferencesHelper = new WanicchouSharedPreferencesHelper(context);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        setUpClickListeners();
        setUpOnFocusChangeListeners();
        setUpKeyListeners();
        mVocabViewModel = ViewModelProviders.of(this).get(VocabularyViewModel.class);
        mRelatedWordViewModel = ViewModelProviders.of(this).get(RelatedWordViewModel.class);
        mNoteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        mContextViewModel = ViewModelProviders.of(this).get(ContextViewModel.class);

        mBinding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String word = getSearchWord();
                if(!TextUtils.isEmpty(word)){
                    searchOnline(word);
                }
                else{
                    mBinding.swipeRefresh.setRefreshing(false);
                }
            }
        });
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
        showWordOnUI();
        showAnkiRelatedUIElements();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mLastSearched != null){
            // Save the word so it can be retrieved and researched when the activity is returned.
            sharedPreferencesHelper
                    .putString(R.string.search_word_key,
                            mLastSearched.getVocabulary().getWord() );
            sharedPreferencesHelper
                    .putString( R.string.dic_type_key,
                            mLastSearched.getVocabulary().getDictionaryType().toString() );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO: Move this away from onResume to reduce startup time
        if(mWebPage == null){
            String lastSearchedWord = sharedPreferencesHelper.getString(R.string.search_word_key);
            String dicType = sharedPreferencesHelper.getString(R.string.dic_type_key);
            DictionaryType dictionaryType = DictionaryTypes.getDictionaryType(dicType);
            if(!TextUtils.isEmpty(lastSearchedWord)){
                showWordFromDB(lastSearchedWord, dictionaryType);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(sharedPreferencesHelper.autoDeleteOption().equals("close")){
            new clearDBAsyncTask().execute(SearchActivity.this);
        }
        super.onDestroy();
    }

    protected static class clearDBAsyncTask extends AsyncTask<Context, Void, Void>{
        @Override
        protected Void doInBackground(Context... contexts) {
            WanicchouDatabase database = WanicchouDatabase.getDatabase(contexts[0]);
            database.clearAllTables();
            return null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    String key = getString(R.string.desired_word_index_key);
                    if (data.hasExtra(key)) {
                        //TODO: Show readings on Related Words so it makes sense to see multiple words with same def
                        int desiredRelatedWordIndex =
                                data.getExtras().getInt(key);
                        WordListEntry desiredWord =
                                mLastSearched.getRelatedWords().get(desiredRelatedWordIndex);

                        mBinding.searchBox.etSearchBox.setText(desiredWord.getRelatedWord());
                        if(mWebPage != null && !TextUtils.isEmpty(desiredWord.getRelatedWord())){
                            mWebPage.navigateRelatedWord( desiredWord,
                                    sharedPreferencesHelper.getMatchType());
                        }
                        else if(!TextUtils.isEmpty(desiredWord.getRelatedWord())){
                            String word = desiredWord.getRelatedWord();
                            if(searchDatabase(word)){
                                String message = getString(R.string.searched_from_db_toast, word);
                                int duration = Toast.LENGTH_SHORT;
                                showToast(message, duration);
                            }
                            else if(searchOnline(word)){
                            }
                        }
                    }
                    String message = getString(R.string.searching_related_toast);
                    int duration = Toast.LENGTH_SHORT;
                    showToast(message, duration);
                    break;
                default:
                    onResume();
            }
        }
    }

    /* ==================================== +Search ================================== */

    @Override
    public void onJavaScriptCompleted() {
        mLastSearched = mWebPage.getSearch();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handleSearchResult();
            }
        });

        String message;
        final int duration = Toast.LENGTH_SHORT;
        if (!TextUtils.isEmpty(mLastSearched.getVocabulary().getWord())) {
            message = getString(R.string.word_search_success, mLastSearched.getVocabulary().getWord());
        }
        else{
            message = getString(R.string.word_search_failure, mBinding.searchBox.etSearchBox.getText().toString());
        }
//        // TODO: Check for network and http request time outs
//        message = getString(R.string.word_search_failure);
        showToast(message, duration);
    }



    /* ==================================== +Databases ================================== */

    private List<WordListEntry> getExistingRelatedWordsFromDb(String word){
        VocabularyEntity entity = mVocabViewModel.getWord(word,
                sharedPreferencesHelper.getDictionaryPreference());
        if (entity == null){
            return null;
        }

        List<WordListEntry> relatedWords = new ArrayList<>();
        SearchProvider provider = sharedPreferencesHelper.getSearchProvider();
        if(provider == null){
            return null;
        }
        for (DictionaryType dictionaryType :
                DictionaryTypes.getAllDictionaryTypeForLanguage(provider.LANGUAGE)){
            List<RelatedWordEntity> relatedWordEntities =
                    mRelatedWordViewModel.getRelatedWordList(entity, dictionaryType);
            for(RelatedWordEntity relatedWordEntity : relatedWordEntities){
                relatedWords.add(new WordListEntry(relatedWordEntity.getRelatedWord(), relatedWordEntity.getDictionaryType()));
            }
        }
        return relatedWords;
    }

    private void addWordsToRelatedWordsDb(Vocabulary vocabulary, List<WordListEntry> newRelatedWords){
        VocabularyEntity entity = getWordFromDb(vocabulary.getWord(), vocabulary.getDictionaryType());
        if (entity == null){
            return;
        }
        for (WordListEntry entry : newRelatedWords){
            RelatedWordEntity relatedWordToAdd =
                    new RelatedWordEntity(entity, entry.getRelatedWord(),
                            entry.getDictionaryType().toString());
            mRelatedWordViewModel.insert(relatedWordToAdd);
        }
    }

    private VocabularyEntity getWordFromDb(String word, DictionaryType dictionaryType) {
        return mVocabViewModel.getWord(word, dictionaryType);
    }


    // TODO: Redo this method because it doesn't do anything, call it in onFocusChanged for tvDefinition
    private void updateDefinition(Vocabulary vocab, String definition){
        //I need the ID, so I have to DB query. Could work around if I save ID
        VocabularyEntity wordInDb = getWordFromDb(vocab.getWord(), vocab.getDictionaryType());
        if (wordInDb == null){
            return;
        }
        wordInDb.setDefinition(definition);
        mVocabViewModel.update(wordInDb);
    }

    private boolean showWordFromDB(String searchWord, DictionaryType dictionaryType){
        VocabularyEntity vocabEntity = getWordFromDb(searchWord, dictionaryType);
        //TODO: Give option of web search
        if(vocabEntity != null){
            if (DictionaryTypes.getDictionaryType(vocabEntity.getDictionaryType())
                    != sharedPreferencesHelper.getDictionaryPreference()){
                return false;
            }

            JapaneseVocabulary vocabulary = new JapaneseVocabulary(vocabEntity);


            String note = mNoteViewModel.getNoteOf(vocabEntity.getWord());
            String wordContext = mContextViewModel.getContextOf(vocabEntity.getWord());

            mBinding.ankiAdditionalFields.tvNotes.setText(note);
            mBinding.ankiAdditionalFields.etNotes.setText(note);
            mBinding.ankiAdditionalFields.tvContext.setText(wordContext);
            mBinding.ankiAdditionalFields.etContext.setText(wordContext);

            mLastSearched = new SanseidoSearchResult(vocabulary,
                    getExistingRelatedWordsFromDb(vocabulary.getWord()));
            showWordOnUI();
            showAnkiRelatedUIElements();
            return true;
        }
        return false;
    }

    private void updateNote(){
        String note =  mNoteViewModel.getNoteOf(mLastSearched.getVocabulary().getWord());
        if(note != null){
            mNoteViewModel.updateNote(mLastSearched.getVocabulary().getWord(),
                    mBinding.ankiAdditionalFields.tvNotes.getText().toString());
        }
        else{
            String event = "info";
            if(shouldSaveSearch(event)){
                saveSearch();
            }
        }
    }

    private void saveSearch(){
        if(mVocabViewModel.insert(mLastSearched.getVocabulary())) {
            addWordsToRelatedWordsDb(mLastSearched.getVocabulary(),
                    mLastSearched.getRelatedWords());
            mNoteViewModel.insertNewNote(mLastSearched.getVocabulary().getWord());
            mContextViewModel.insertNewContext(mLastSearched.getVocabulary().getWord());
        }
    }

    private boolean shouldSaveSearch(String event){
        Vocabulary vocab = mLastSearched.getVocabulary();
        VocabularyEntity wordInDb = getWordFromDb(vocab.getWord(), vocab.getDictionaryType());
        if (wordInDb != null){
            return false;
        }

        switch (sharedPreferencesHelper.autoSaveOption()){
            //Catches failure event only
            case "all":
                return true;
            case "success":
                if(event.equals("success")){
                    return true;
                }
            case "info":
                if(event.equals("info")){
                    return true;
                }
            default:
                return false;
        }
    }

    private void updateContext(){
        String wordContext =  mContextViewModel.getContextOf(mLastSearched.getVocabulary().getWord());
        if(wordContext != null){
            mContextViewModel.updateContext(mLastSearched.getVocabulary().getWord(),
                    mBinding.ankiAdditionalFields.tvContext.getText().toString());
        }
        else{
            String event = "info";
            if(shouldSaveSearch(event)){
                saveSearch();
            }
        }
    }


    /* ==================================== +UI and +Helpers ================================== */
    /**
     * Helper class to hide and show Anki import related UI elements after a search is performed.
     */
    private void showAnkiRelatedUIElements(){
        mBinding.btnRelatedWords.setVisibility(View.VISIBLE);
        mBinding.ankiAdditionalFields.viewAnkiFields.setVisibility(View.VISIBLE);
        if (AnkiDroidHelper.isApiAvailable(this)){
            mBinding.fab.setVisibility(View.VISIBLE);
        }
        else {
            mBinding.fab.setVisibility(View.INVISIBLE);
        }
    }

    private void clearAnkiFields(){
        mBinding.ankiAdditionalFields.tvContext.setText("");
        mBinding.ankiAdditionalFields.etContext.setText("");
        mBinding.ankiAdditionalFields.tvNotes.setText("");
        mBinding.ankiAdditionalFields.etNotes.setText("");
    }

    private void setUpOnFocusChangeListeners(){
        mBinding.vocabularyInformation.etDefinition.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && mLastSearched != null){
                    String changedText = mBinding.vocabularyInformation.etDefinition.getText().toString();
                    mBinding.vocabularyInformation.tvDefinition.setText(changedText);
                    mLastSearched.getVocabulary().setDefinition(changedText);
                    updateDefinition(mLastSearched.getVocabulary(), changedText);
                    mBinding.vocabularyInformation.vsDefinition.showNext();
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
        mBinding.vocabularyInformation.tvDefinition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.vocabularyInformation.vsDefinition.showNext();
                mBinding.vocabularyInformation.etDefinition.requestFocus();
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(mBinding.vocabularyInformation.etDefinition,
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
                        .putParcelableArrayListExtra(getString(R.string.related_word_key),
                                (ArrayList<? extends Parcelable>) mLastSearched.getRelatedWords());

                startActivityForResult(intentStartRelatedWordsActivity, SEARCH_ACTIVITY_REQUEST_CODE);

            }
        });

        //TODO: Fix it so it requests on add card, not on start up. Avoid crashes before addition.

        FloatingActionButton fab = findViewById(R.id.fab);
        // TODO: Hide if Anki is not installed.
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(mAnkiDroid == null){
                    mAnkiDroid = new AnkiDroidHelper(SearchActivity.this);
                }
                if (mAnkiDroid.shouldRequestPermission()) {
                    mAnkiDroid.requestPermission(SearchActivity.this, ADD_PERM_REQUEST);
                    return;
                }

                String notes = mBinding.ankiAdditionalFields.etNotes.getText().toString();
                String wordContext = mBinding.ankiAdditionalFields.etContext.getText().toString();
                mAnkiDroid.addWordToAnki(mLastSearched, notes, wordContext);
                String message = getString(R.string.anki_added_toast);
                int duration = Toast.LENGTH_SHORT;
                showToast(message, duration);


                String autoDeleteOption = sharedPreferencesHelper.autoDeleteOption();
                if(autoDeleteOption.equals("import")){
                    mNoteViewModel.delete(mLastSearched.getVocabulary().getWord());
                    mContextViewModel.delete(mLastSearched.getVocabulary().getWord());
                    mRelatedWordViewModel.deleteWordsRelatedTo(mLastSearched.getVocabulary().getWord());
                    mVocabViewModel.delete(mLastSearched.getVocabulary().getWord(),
                            mLastSearched.getVocabulary().getDictionaryType());
                }

            }
        });
    }

    private boolean searchDatabase(String word){
        clearAnkiFields();
        word = word.trim();
        SearchProvider provider = sharedPreferencesHelper.getSearchProvider();

        DictionaryType dicPref = null;
        try {
            Method autoAssigner = provider.DICTIONARY_TYPE_CLASS.getMethod("assignTypeByInput", String.class);
            dicPref = (DictionaryType) autoAssigner.invoke(null, word);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if(dicPref == null){
            dicPref = sharedPreferencesHelper.getDictionaryPreference();
        }

        return showWordFromDB(word, dicPref);
    }

    private boolean searchOnline(String word){
        try{
            SearchProvider provider = sharedPreferencesHelper.getSearchProvider();
            DictionaryType dicPref = null;

            Method autoAssigner = provider.DICTIONARY_TYPE_CLASS.getMethod("assignTypeByInput", String.class);
            dicPref = (DictionaryType) autoAssigner.invoke(null, word);
            if(dicPref == null){
                dicPref = sharedPreferencesHelper.getDictionaryPreference();
            }

            Context context = SearchActivity.this;
            OnJavaScriptCompleted listener = SearchActivity.this;

            //TODO: Reuse existing webview if possible

            Constructor<?> webViewContructor = provider.WEB_VIEW_CLASS.getConstructor(
                    Context.class,
                    String.class,
                    DictionaryType.class,
                    provider.MATCH_TYPE_CLASS,
                    OnJavaScriptCompleted.class
            );
            mWebPage = (DictionaryWebPage) webViewContructor.newInstance(
                    context,
                    word,
                    dicPref,
                    provider.MATCH_TYPE_CLASS.cast(sharedPreferencesHelper.getMatchType()),
                    listener
            );
            return true;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setUpKeyListeners(){
        mBinding.searchBox.etSearchBox.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View searchBox, int keyCode, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    switch (keyCode){
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:

                            String message = getString(R.string.word_searching);
                            int duration = Toast.LENGTH_SHORT;
                            showToast(message, duration);

                            String word = getSearchWord();
                            if(searchDatabase(word)) {
                                message = getString(R.string.searched_from_db_toast, word);
                                showToast(message, duration);
                            }
                            else if(searchOnline(word)){
                                //Handled by handleSearchResult for now
                            }
                        default:
                            return false;
                    }
                }
                return false;
            }
        });
    }

    private String getSearchWord(){
        return mBinding.searchBox.etSearchBox.getText().toString();
    }

    private void handleSearchResult(){
        String event;
        if(mLastSearched != null){
            if (!TextUtils.isEmpty(mLastSearched.getVocabulary().getWord())) {
                event = "success";
                if (shouldSaveSearch(event)) {
                    saveSearch();
                }
                showWordOnUI();

                showAnkiRelatedUIElements();
            }
            else{
                event = "failure";
                if(shouldSaveSearch(event)){
                    addInvalidWord(mLastSearched);
                }
            }
        }
        mBinding.swipeRefresh.setRefreshing(false);
    }

    private void showWordOnUI(){
        assert mLastSearched != null;
        Vocabulary vocabulary = mLastSearched.getVocabulary();
        String definition = vocabulary.getDefinition();
        mBinding.vocabularyInformation.tvWord.setText(vocabulary.getWord());
        mBinding.vocabularyInformation.tvDefinition.setText(definition);
        mBinding.vocabularyInformation.etDefinition.setText(definition);
        mBinding.vocabularyInformation.tvReading.setText(vocabulary.getReading());
        if(!TextUtils.isEmpty(vocabulary.getPitch())){
            mBinding.vocabularyInformation.tvPitch.setText(vocabulary.getPitch());
            mBinding.vocabularyInformation.tvPitch.setVisibility(View.VISIBLE);
        }
        else{
            mBinding.vocabularyInformation.tvPitch.setVisibility(View.INVISIBLE);
        }

        mBinding.vocabularyInformation.tvDictionary.setText(vocabulary.getDictionaryType().toDisplayText());

        String note = mNoteViewModel.getNoteOf(mLastSearched.getVocabulary().getWord());
        mBinding.ankiAdditionalFields.etNotes.setText(note);
        mBinding.ankiAdditionalFields.tvNotes.setText(note);

        String wordContext = mContextViewModel.getContextOf(mLastSearched.getVocabulary().getWord());
        mBinding.ankiAdditionalFields.etContext.setText(wordContext);
        mBinding.ankiAdditionalFields.tvContext.setText(wordContext);


    }

    private void addInvalidWord(SearchResult searchResult){
        // TODO: Add empty entry to db for failed searches that aren't network errors.
        // TODO: Probably a null somewhere since I do this
        DictionaryType dictionaryType = searchResult.getVocabulary().getDictionaryType();
        JapaneseVocabulary invalidWord =
                new JapaneseVocabulary(mBinding.searchBox.etSearchBox
                        .getText().toString(), dictionaryType);

        VocabularyEntity entity =
                getWordFromDb(searchResult.getVocabulary().getWord(), dictionaryType);
        if (entity == null) {
            mVocabViewModel.insert(invalidWord);
        }
        // TODO: Maybe a different error message
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
            case R.id.action_db:
                Intent startDbActviity = getDatabaseActivityIntent();
                startActivityForResult(startDbActviity, SEARCH_ACTIVITY_REQUEST_CODE);
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
        String message;
        int duration = Toast.LENGTH_SHORT;
        if (requestCode==ADD_PERM_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            String notes = mBinding.ankiAdditionalFields.etNotes.getText().toString();
            String wordContext = mBinding.ankiAdditionalFields.etContext.getText().toString();
            mAnkiDroid.addWordToAnki(mLastSearched, notes, wordContext);
            message = getString(R.string.anki_added_toast);
            showToast(message, duration);
        } else {
            message = getString(R.string.permissions_denied_toast);
        }
        showToast(message, duration);
    }

    private void showToast(String message, int duration){
        if (mToast != null){
            mToast.cancel();
        }
        Context context = SearchActivity.this;
        mToast = Toast.makeText(context, message, duration);
        mToast.show();
    }

    //TODO: REfactor this to use a proper view model
    private Intent getDatabaseActivityIntent(){
        Intent databaseActivityIntent =
                new Intent(getApplicationContext(), DatabaseActivity.class);

        List<WordListEntry> dbWords = mVocabViewModel.getAllSavedWords();

        databaseActivityIntent
                .putParcelableArrayListExtra(getString(R.string.related_word_key),
                        (ArrayList<? extends Parcelable>) dbWords);

        return databaseActivityIntent;
    }

    //TODO: Change the context/notes/def to be live data objects


}
