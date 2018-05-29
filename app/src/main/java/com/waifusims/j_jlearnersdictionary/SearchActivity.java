package com.waifusims.j_jlearnersdictionary;

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

import com.waifusims.j_jlearnersdictionary.databinding.ActivitySearchBinding;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.room.rel.RelatedWordEntity;
import data.room.rel.RelatedWordViewModel;
import data.room.voc.VocabularyEntity;
import data.room.voc.VocabularyViewModel;
import data.vocab.DictionaryType;
import data.vocab.JapaneseVocabulary;
import data.vocab.search.SanseidoSearch;
import data.vocab.search.SanseidoSearchAsyncTaskLoader;
import util.anki.AnkiDroidHelper;
import util.anki.AnkiDroidConfig;
import android.support.v4.app.LoaderManager;

import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.widget.Toast;
import android.widget.ViewSwitcher;

//TODO:  Horizontal UI
public class SearchActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<SanseidoSearch> {
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

    private SanseidoSearch mLastSearched;

    /* ==================================== +Lifecycle ================================== */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAnkiDroid = new AnkiDroidHelper(this);
        setContentView(R.layout.activity_search);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        setUpClickListeners();

        mVocabViewModel = ViewModelProviders.of(this).get(VocabularyViewModel.class);
        mRelatedWordsViewModel = ViewModelProviders.of(this).get(RelatedWordViewModel.class);
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
        if(!TextUtils.isEmpty(searchWord)){
            showWordFromDB(searchWord);
        }
    }

    @Override
    protected void onDestroy() {
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

    /* ==================================== +Loader and +Search ================================== */

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
                    addWordToDb(invalidWord);
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


    /* ==================================== +Databases ================================== */

    private Map<String, Set<String> > getExistingRelatedWordsFromDb(String word){
        VocabularyEntity entity = mVocabViewModel.getWord(word, getCurrentDictionaryPreference());
        if (entity == null){
            return null;
        }

        int fkBaseWordID = entity.getId();

        Map<String, Set<String> > existingRelatedWordsMap = new HashMap<>();
        for (DictionaryType type : DictionaryType.values()){
            List<RelatedWordEntity> relatedWordEntities =
                    mRelatedWordsViewModel.getRelatedWordList(fkBaseWordID, type);
            Set<String> relatedWords = new HashSet<>();
            for(RelatedWordEntity relatedWordEntity : relatedWordEntities){
                relatedWords.add(relatedWordEntity.getRelatedWord());
            }
            existingRelatedWordsMap.put(type.toJapaneseDictionaryType(), relatedWords);
        }
        return existingRelatedWordsMap;
    }

    private void addWordsToRelatedWordsDb(String searchWord, Map<String, Set<String> > newRelatedWords){
        VocabularyEntity entity = getWordFromDb(searchWord);
        for (String dictionaryType : newRelatedWords.keySet()){
            for(String relatedWord : newRelatedWords.get(dictionaryType)){
                    RelatedWordEntity relatedWordToAdd =
                            new RelatedWordEntity(entity, relatedWord, dictionaryType);
                    mRelatedWordsViewModel.insert(relatedWordToAdd);
            }
        }
    }

    private VocabularyEntity getWordFromDb(String word) {
        return mVocabViewModel.getWord(word, getCurrentDictionaryPreference());
    }

    private void addWordToDb(JapaneseVocabulary vocab){
        String notes = mBinding.ankiAdditionalFields.etNotes.getText().toString();
        String wordContext = mBinding.ankiAdditionalFields.etContext.getText().toString();

        VocabularyEntity vocabularyEntity = new VocabularyEntity(vocab, notes, wordContext);
        mVocabViewModel.insert(vocabularyEntity);
    }

    private void updateWordInDb(JapaneseVocabulary vocab){
        //I need the ID, so I have to DB query. Could work around if I save ID
        VocabularyEntity wordInDb = getWordFromDb(vocab.getWord());
        if (wordInDb == null){
            return;
        }
        wordInDb.setWord(vocab.getWord());
        wordInDb.setDefinition(vocab.getDefintion());
        wordInDb.setReading(vocab.getReading());
        wordInDb.setPitch(vocab.getPitch());
        wordInDb.setDictionaryType(vocab.getDictionaryType().toString());
        wordInDb.setNotes(getWordNotes());
        wordInDb.setWordContext(getWordContext());

        mVocabViewModel.update(wordInDb);
    }


    private boolean showWordFromDB(String searchWord){
        VocabularyEntity entity = getWordFromDb(searchWord);
        //TODO: Give option of web search
        if(entity != null){
            JapaneseVocabulary vocabulary = new JapaneseVocabulary(entity);
            mBinding.ankiAdditionalFields.etNotes.setText(entity.getNotes());
            mBinding.ankiAdditionalFields.etContext.setText(entity.getWordContext());

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

    //TODO: Make UI be TVs until clicked on, then become ET
    private String getWordNotes(){
        return mBinding.ankiAdditionalFields.etNotes.getText().toString();
    }

    private String getWordContext(){
        return mBinding.ankiAdditionalFields.etContext.getText().toString();
    }

    private void clearAnkiFields(){
        mBinding.ankiAdditionalFields.etContext.setText("");
        mBinding.ankiAdditionalFields.etNotes.setText("");
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
        String message = getString(R.string.toast_anki_added);
        int duration = Toast.LENGTH_SHORT;
        mToast = Toast.makeText(context, message, duration);
        mToast.show();
    }

    private void setUpClickListeners(){
        // Definition TV/ET
        mBinding.wordDefinition.tvDefinition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.wordDefinition.vsDefinition.showNext();
                mBinding.wordDefinition.etDefinition.requestFocus();
            }
        });

        mBinding.wordDefinition.etDefinition.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    String changedText = mBinding.wordDefinition.etDefinition.getText().toString();
                    mBinding.wordDefinition.tvDefinition.setText(changedText);
                    mBinding.wordDefinition.vsDefinition.showNext();
                }
            }
        });

        // Context Label, TV, and ET
        mBinding.ankiAdditionalFields.tvContextLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.ankiAdditionalFields.vsContext.showNext();
                mBinding.ankiAdditionalFields.etContext.requestFocus();
            }
        });

        mBinding.ankiAdditionalFields.tvContext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.ankiAdditionalFields.vsContext.showNext();
                mBinding.ankiAdditionalFields.etContext.requestFocus();
            }
        });

        mBinding.ankiAdditionalFields.etContext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    String changedText = mBinding.ankiAdditionalFields.tvContext.getText().toString();
                    mBinding.ankiAdditionalFields.tvContext.setText(changedText);
                    mBinding.ankiAdditionalFields.vsContext.showNext();
                }
            }
        });


        // Notes label, TV, ET
        mBinding.ankiAdditionalFields.tvNotesLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.ankiAdditionalFields.vsNotes.showNext();
                mBinding.ankiAdditionalFields.etNotes.requestFocus();
            }
        });
        mBinding.ankiAdditionalFields.tvNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.ankiAdditionalFields.vsNotes.showNext();
                mBinding.ankiAdditionalFields.etNotes.requestFocus();
            }
        });

        mBinding.ankiAdditionalFields.etNotes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    String changedText = mBinding.ankiAdditionalFields.etNotes.getText().toString();
                    mBinding.ankiAdditionalFields.tvNotes.setText(changedText);
                    mBinding.ankiAdditionalFields.vsNotes.showNext();
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
                            if(mLastSearched != null){
                                updateWordInDb(mLastSearched.getVocabulary());
                            }
                            String searchWord = mBinding.wordSearch.etSearchBox.getText().toString();
                            clearAnkiFields();
                            // If the word is not in the DB, we need to make a search
                            if(!showWordFromDB(searchWord)) {
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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (mAnkiDroid.shouldRequestPermission()) {
                    mAnkiDroid.requestPermission(SearchActivity.this, ADD_PERM_REQUEST);
                }
                addWordToAnki();
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
        Context context = this;
        Class childActivity = SettingsActivity.class;
        if (itemId == R.id.action_settings) {
            Intent startSettingsActivityIntent = new Intent(context, childActivity);
            startActivity(startSettingsActivityIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO: Fix this breaking first launch
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
}
