package com.waifusims.wanicchou;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import data.room.context.ContextViewModel;
import data.room.notes.NoteViewModel;
import data.room.rel.RelatedWordEntity;
import data.room.rel.RelatedWordViewModel;
import data.room.voc.VocabularyEntity;
import data.room.voc.VocabularyViewModel;
import data.vocab.jp.JapaneseDictionaryType;
import data.vocab.jp.search.sanseido.SanseidoMatchType;
import data.vocab.models.DictionaryType;
import data.vocab.models.DictionaryWebPage;
import data.vocab.jp.JapaneseVocabulary;
import data.vocab.OnJavaScriptCompleted;
import data.vocab.RelatedWordEntry;
import data.vocab.models.Search;
import data.vocab.models.Vocabulary;
import data.vocab.jp.search.sanseido.SanseidoSearch;
import data.vocab.jp.search.sanseido.SanseidoSearchWebView;
import util.anki.AnkiDroidConfig;
import util.anki.AnkiDroidHelper;

// TODO: Automatically select EJ for English input
//TODO:  Horizontal UI
public class SearchActivity extends AppCompatActivity
        implements OnJavaScriptCompleted {
    public static final String LOG_TAG = "Wanicchou";
    private static final int ADD_PERM_REQUEST = 0;
    private static final int SEARCH_ACTIVITY_REQUEST_CODE = 42;

    private static final String SEARCH_WORD_KEY = "search";

    private ActivitySearchBinding mBinding;
    private AnkiDroidHelper mAnkiDroid;
    private Toast mToast;
    private VocabularyViewModel mVocabViewModel;
    private RelatedWordViewModel mRelatedWordsViewModel;
    private NoteViewModel mNoteViewModel;
    private ContextViewModel mContextViewModel;

    private Search mLastSearched;
    private DictionaryWebPage mWebPage;

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
        showWordOnUI();
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
        if(mWebPage == null){
            Context context = SearchActivity.this;
            String stringIfMissing = "";
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);
            String searchWord = sharedPreferences.getString(getString(R.string.search_word_key),
                    stringIfMissing);
            String dicTypeKey = getString(R.string.dic_type_key);
            //Returns null if nothing saved
            String dicType = sharedPreferences.getString(dicTypeKey, stringIfMissing);
            DictionaryType dictionaryType = JapaneseDictionaryType.fromKey(dicType);
            if(!TextUtils.isEmpty(searchWord)){
                showWordFromDB(searchWord, dictionaryType);
            }
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
                        RelatedWordEntry desiredWord =
                                mLastSearched.getRelatedWords().get(desiredRelatedWordIndex);

                        mWebPage.navigateRelatedWord(desiredWord);
//                        if(!TextUtils.isEmpty(desiredRelatedWord)){
//                            mBinding.wordSearch.etSearchBox.setText(desiredRelatedWord);
//                        }
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
        final Context context = getApplicationContext();
        final int searchCompleteToastDuration = Toast.LENGTH_SHORT;
        if (!TextUtils.isEmpty(mLastSearched.getVocabulary().getWord())) {
            message = getString(R.string.word_search_success);
        }
        else{
            message = getString(R.string.word_search_failure);
        }
//        // TODO: Check for network and http request time outs
//        message = getString(R.string.word_search_failure);
        mToast = Toast.makeText(context, message, searchCompleteToastDuration);
        mToast.show();
    }



    /* ==================================== +Databases ================================== */

    private List<RelatedWordEntry> getExistingRelatedWordsFromDb(String word){
        VocabularyEntity entity = mVocabViewModel.getWord(word, getCurrentDictionaryPreference());
        if (entity == null){
            return null;
        }


        List<RelatedWordEntry> relatedWords = new ArrayList<>();
        for (JapaneseDictionaryType dictionaryType : JapaneseDictionaryType.values()){
            List<RelatedWordEntity> relatedWordEntities =
                    mRelatedWordsViewModel.getRelatedWordList(entity, dictionaryType);
            for(RelatedWordEntity relatedWordEntity : relatedWordEntities){
                relatedWords.add(new RelatedWordEntry(relatedWordEntity.getRelatedWord(), relatedWordEntity.getDictionaryType()));
            }
        }
        return relatedWords;
    }

    private void addWordsToRelatedWordsDb(Vocabulary vocabulary, List<RelatedWordEntry> newRelatedWords){
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
            if (JapaneseDictionaryType.fromKey(vocabEntity.getDictionaryType()) != getCurrentDictionaryPreference()){
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
            showWordOnUI();
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
        return JapaneseDictionaryType.fromKey(dictionaryTypeString);
    }

    private SanseidoMatchType getCurrentMatchType(){
        Context context = this;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String matchTypeString = sharedPreferences.getString(
                getString(R.string.pref_match_type_key),
                getString(R.string.pref_match_type_default)
        );
        return SanseidoMatchType.fromKey(matchTypeString);
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


    private void clearAnkiFields(){
        mBinding.ankiAdditionalFields.tvContext.setText("");
        mBinding.ankiAdditionalFields.etContext.setText("");
        mBinding.ankiAdditionalFields.tvNotes.setText("");
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

        if(mLastSearched.getVocabulary() instanceof JapaneseVocabulary){
            //TODO: Bandaided after interface implementation, something cleaner
            fields[AnkiDroidConfig.FIELDS_INDEX_FURIGANA] =
                    ((JapaneseVocabulary)mLastSearched.getVocabulary()).getFurigana();
        }
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
                    updateDefinition(mLastSearched.getVocabulary(), changedText);
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
                //TODO: Find out if there are cases where mWebPage will be destroyed on going to child
//                intentStartRelatedWordsActivity.putExtra(getString(R.string.url_key),
//                        mWebPage.getUrl());
//                intentStartRelatedWordsActivity.putExtra(getString(R.string.html_key),
//                        mWebPage.getHtmlDocument().toString());

                startActivityForResult(intentStartRelatedWordsActivity, SEARCH_ACTIVITY_REQUEST_CODE);

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
                            // If the word isn't saved in our DB, start a new search for it.
                            if(!showWordFromDB(searchWord, getCurrentDictionaryPreference())){
                                try {
                                    Context context = SearchActivity.this;
                                    OnJavaScriptCompleted listener = SearchActivity.this;
                                    mWebPage = new SanseidoSearchWebView(
                                            context,
                                            searchWord,
                                            getCurrentDictionaryPreference(),
                                            getCurrentMatchType(),
                                            listener
                                    );
                                } catch (IOException e) {
                                    e.printStackTrace();
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
    }

    private void handleSearchResult(){
        if(mLastSearched != null){
            if (!TextUtils.isEmpty(mLastSearched.getVocabulary().getWord())) {
                VocabularyEntity entity =
                        getWordFromDb(mLastSearched.getVocabulary().getWord(),
                                mLastSearched.getVocabulary().getDictionaryType());

                if (entity == null) {
                    if(mVocabViewModel.insert(mLastSearched.getVocabulary())) {
                        addWordsToRelatedWordsDb(mLastSearched.getVocabulary(),
                                mLastSearched.getRelatedWords());
                        mNoteViewModel.insertNewNote(mLastSearched.getVocabulary().getWord());
                        mContextViewModel.insertNewContext(mLastSearched.getVocabulary().getWord());
                    }
                }
                showWordOnUI();

                showAnkiRelatedUIElements();
            }
            else{
                addInvalidWord(mLastSearched);
            }
        }
    }

    private void showWordOnUI(){
        Vocabulary vocabulary = mLastSearched.getVocabulary();
        String definition = vocabulary.getDefinition();
        mBinding.wordDefinition.tvWord.setText(vocabulary.getWord());
        mBinding.wordDefinition.tvDefinition.setText(definition);
        mBinding.wordDefinition.etDefinition.setText(definition);

        String note = mNoteViewModel.getNoteOf(mLastSearched.getVocabulary().getWord());
        mBinding.ankiAdditionalFields.etNotes.setText(note);
        mBinding.ankiAdditionalFields.tvNotes.setText(note);

        String wordContext = mContextViewModel.getContextOf(mLastSearched.getVocabulary().getWord());
        mBinding.ankiAdditionalFields.etContext.setText(wordContext);
        mBinding.ankiAdditionalFields.tvContext.setText(wordContext);
    }

    private void addInvalidWord(Search search){
        // TODO: Add empty entry to db for failed searches that aren't network errors.
        // TODO: Probably a null somewhere since I do this
        DictionaryType dictionaryType = search.getVocabulary().getDictionaryType();
        JapaneseVocabulary invalidWord =
                new JapaneseVocabulary(mBinding.wordSearch.etSearchBox
                        .getText().toString(), dictionaryType);

        VocabularyEntity entity =
                getWordFromDb(search.getVocabulary().getWord(), dictionaryType);
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
