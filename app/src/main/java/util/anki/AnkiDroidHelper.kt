package util.anki

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.SparseArray
import com.ichi2.anki.api.AddContentApi
import com.ichi2.anki.api.AddContentApi.READ_WRITE_PERMISSION
import com.ichi2.anki.api.NoteInfo
import data.vocab.model.DictionaryEntry
import java.util.*

/**
 * Originally from 'ankidroid/apisample'
 */

//TODO: SingletonHolder
class AnkiDroidHelper(context: Context) {
    private val mContext: Context = context.applicationContext
    private val api: AddContentApi = AddContentApi(mContext)
    /**
     * Checks for the DeckID in sharedPref, then in AnkiDroid
     * Adds the deck if it doesn't exist in either
     * @return DeckID of the Wanicchou Deck
     */
    private val wanicchouDeckID: Long
        get() {
            var deckID = getWanicchouDeckID(AnkiDroidConfig.DECK_NAME)
            if (deckID == null) {
                deckID = api.addNewDeck(AnkiDroidConfig.DECK_NAME)
                storeDeckReference(AnkiDroidConfig.DECK_NAME, deckID!!)
            }
            return deckID
        }

    /**
     * helper method to retrieve the model ID for JJLD
     * @return the model ID for JJLD in Anki
     */
    private val wanicchouModelID: Long
        get() {
            var modelID = findModelIDByName(AnkiDroidConfig.MODEL_NAME, AnkiDroidConfig.FIELDS.size)
            val sortField = null
            if (modelID == null) {
                modelID = api.addNewCustomModel(AnkiDroidConfig.MODEL_NAME,
                        AnkiDroidConfig.FIELDS,
                        AnkiDroidConfig.CARD_NAMES,
                        AnkiDroidConfig.QFMT,
                        AnkiDroidConfig.AFMT,
                        AnkiDroidConfig.CSS,
                        wanicchouDeckID,
                        sortField)
                storeModelReference(AnkiDroidConfig.MODEL_NAME, modelID!!)
            }
            return modelID
        }

    companion object {
        private const val SHARED_PREF_DECK_DB = "com.ichi2.anki.api.decks"
        private const val SHARED_PREF_MODEL_DB = "com.ichi2.anki.api.models"
    }
    /**
     * Whether or not the API is available to use.
     * The API could be unavailable if AnkiDroid is not installed or the user explicitly disabled the API
     * @return true if the API is available to use
     */
    fun isApiAvailable(): Boolean {
        return AddContentApi.getAnkiDroidPackageName(mContext) != null
    }

    /**
     * Whether or not we should request full access to the AnkiDroid API
     */
    fun shouldRequestPermission(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            false
        } else {
            (ContextCompat.checkSelfPermission(mContext, READ_WRITE_PERMISSION)
                    != PackageManager.PERMISSION_GRANTED)
        }
    }

    /**
     * Request permission from the user to access the AnkiDroid API (for SDK 23+)
     * @param callbackActivity An Activity which implements onRequestPermissionsResult()
     * @param callbackCode The callback code to be used in onRequestPermissionsResult()
     */
    fun requestPermission(callbackActivity: Activity, callbackCode: Int) {
        ActivityCompat.requestPermissions(callbackActivity,
                                          arrayOf(READ_WRITE_PERMISSION),
                                          callbackCode)
    }

    // Attempts to find existing note and update it if it exists, else add it
    fun addUpdateNote(dictionaryEntry: DictionaryEntry,
                      notes: List<String>,
                      tags: MutableSet<String>): Long {
        val existingNotes = findDuplicateNotes(wanicchouModelID, dictionaryEntry.word)
        for (note in existingNotes){
            // If Word, word language, def language, pronunciation, and dictionary is the same,
            // then we will treat it as the same card.
            val noteWordLanguage = note.fields[AnkiDroidConfig.FIELDS_INDEX_WORD_LANGUAGE]
            val noteDefinitionLanguage = note.fields[AnkiDroidConfig.FIELDS_INDEX_DEFINITION_LANGUAGE]
            val noteDictionary = note.fields[AnkiDroidConfig.FIELDS_INDEX_DICTIONARY]
            val notePronunciation = note.fields[AnkiDroidConfig.FIELDS_INDEX_PRONUNCIATION]
            if (noteWordLanguage == dictionaryEntry.wordLanguageCode
                    && noteDefinitionLanguage == dictionaryEntry.definitionLanguageCode
                    && noteDictionary == dictionaryEntry.dictionary
                    && notePronunciation == dictionaryEntry.pronunciation){
                updateNoteFields(note.id, dictionaryEntry, notes)
                updateNoteTags(note.id, tags)
                return note.id
            }
        }
        return addNote(dictionaryEntry, notes, tags)
    }

    /**
     * Add a search result to Anki
     * @param dictionaryEntry the entry to add
     * @param notes user saved notes
     * @param tags to include
     */
    private fun addNote(dictionaryEntry: DictionaryEntry, notes: List<String>, tags : MutableSet<String>): Long {
        val fields = getFieldsArray(dictionaryEntry, notes)
        tags.addAll(AnkiDroidConfig.TAGS)
        return api.addNote(wanicchouModelID, wanicchouDeckID, fields, tags)
    }


    /**
     * Save a mapping from deckName to getDeckId in the SharedPreferences
     */
    private fun storeDeckReference(deckName: String, deckId: Long) {
        val decksDb = mContext.getSharedPreferences(SHARED_PREF_DECK_DB, Context.MODE_PRIVATE)
        decksDb.edit().putLong(deckName, deckId).apply()
    }

    /**
     * Save a mapping from modelName to modelId in the SharedPreferences
     */
    private fun storeModelReference(modelName: String, modelId: Long) {
        val modelsDb = mContext.getSharedPreferences(SHARED_PREF_MODEL_DB, Context.MODE_PRIVATE)
        modelsDb.edit().putLong(modelName, modelId).apply()
    }



    /**
     * Try to find the given model by name, accounting for renaming of the model:
     * If there's a model with this modelName that is known to have previously been created (by this app)
     * and the corresponding model ID exists and has the required number of fields
     * then return that ID (even though it may have since been renamed)
     * If there's a model from #getModelList with modelName and required number of fields then return its ID
     * Otherwise return null
     * @param modelName the name of the model to find
     * @param numFields the minimum number of fields the model is required to have
     * @return the model ID
     */
    private fun findModelIDByName(modelName: String, numFields: Int): Long? {
        val modelsDb = mContext.getSharedPreferences(SHARED_PREF_MODEL_DB, Context.MODE_PRIVATE)
        val prefsModelID = modelsDb.getLong(modelName, -1)
        // if we have a reference saved to modelName and it exists and has at least numFields then return it
        if (prefsModelID != -1L && api.getModelName(prefsModelID) != null
                && api.getFieldList(prefsModelID).size >= numFields) { // could potentially have been renamed
            return prefsModelID
        }
        val modelList = api.getModelList(numFields)
        for ((key, value) in modelList) {
            if (value == modelName) {
                return key // first model wins
            }
        }
        // model no longer exists (by name nor old id) or the number of fields was reduced
        return null
    }


    /**
     * Returns: DeckID in SharedPreferences, if it exists
     * else DeckID of Deck with the same name in AnkiDroid
     * else null
     * @param deckName the name of the deck to find
     * @return the did of the deck in Anki
     */
    private fun getWanicchouDeckID(deckName: String): Long? {
        // Search SharedPrefs first
        val sharedPrefDecks = mContext
                .getSharedPreferences(SHARED_PREF_DECK_DB, Context.MODE_PRIVATE)
        val ankiDroidDeckID : Long? = sharedPrefDecks.getLong(deckName, -1)
        return if (ankiDroidDeckID != (-1).toLong()
                && api.getDeckName(ankiDroidDeckID) != null) {
            ankiDroidDeckID
        } else {
            // DeckID in AnkiDroid if it exists, else null
            getDeckIDFromAnki(deckName)
        }
    }

    /**
     * Get the ID of the deck which matches the name
     * @param deckName Exact name of deck (note: deck names are unique in Anki)
     * @return the ID of the deck that has given name, or null if no deck was found
     */
    private fun getDeckIDFromAnki(deckName: String): Long? {
        val deckList = api.deckList
        val ignoreCase = true
        for ((key, value) in deckList) {
            if (value.equals(deckName, ignoreCase)) {
                return key
            }
        }
        return null
    }




    private fun separateNotes(notes: List<String>): String{
        return notes.reduce { a, b -> a + "\n" +b }
    }

    private fun getFieldsArray(dictionaryEntry: DictionaryEntry,
                               notes: List<String>) : Array<String?>{
        val fieldNames = api.getFieldList(wanicchouModelID)
        val fields = arrayOfNulls<String>(fieldNames.size)
        fields[AnkiDroidConfig.FIELDS_INDEX_WORD] = dictionaryEntry.word
        fields[AnkiDroidConfig.FIELDS_INDEX_WORD_LANGUAGE] = dictionaryEntry.word
        fields[AnkiDroidConfig.FIELDS_INDEX_PRONUNCIATION] = dictionaryEntry.pronunciation
        fields[AnkiDroidConfig.FIELDS_INDEX_DEFINITION] = dictionaryEntry.definition
        fields[AnkiDroidConfig.FIELDS_INDEX_DEFINITION_LANGUAGE] = dictionaryEntry.definition
        fields[AnkiDroidConfig.FIELDS_INDEX_FURIGANA] = getFurigana(dictionaryEntry)
        fields[AnkiDroidConfig.FIELDS_INDEX_PITCH] = dictionaryEntry.pitch
        fields[AnkiDroidConfig.FIELDS_INDEX_NOTES] = separateNotes(notes)
        fields[AnkiDroidConfig.FIELDS_INDEX_DICTIONARY] = dictionaryEntry.dictionary
        return fields
    }

    //TODO: Change click to expand a menu and add associated UI elements
    //TODO: Maybe implement a clozed type when sentence search is included
    //TODO: Duplicate checking

    private fun updateNoteFields(noteID: Long, dictionaryEntry: DictionaryEntry, notes: List<String>){
        val fields = getFieldsArray(dictionaryEntry, notes)
        api.updateNoteFields(noteID, fields)
    }

    private fun updateNoteTags(noteID: Long, tags : MutableSet<String>): Boolean {
        tags.addAll(AnkiDroidConfig.TAGS)
        return api.updateNoteTags(noteID, tags)
    }

    private fun findDuplicateNotes(modelID: Long, key: String): List<NoteInfo>{
        return api.findDuplicateNotes(modelID, key)
    }

    private fun findDuplicateNotes(modelID: Long, keys: List<String>): SparseArray<List<NoteInfo>> {
        return api.findDuplicateNotes(modelID, keys)
    }

    private fun getNoteCount(modelID: Long): Int {
        return api.getNoteCount(modelID)
    }


    private fun getNote(noteID: Long): NoteInfo? {
        return api.getNote(noteID)
    }

    private fun previewNewNote(modelID: Long, fields: Array<String>): Map<String, Map<String,String>>{
        return api.previewNewNote(modelID, fields)
    }

    private fun getCurrentModelID() : Long {
        return api.currentModelId
    }

    private fun getFieldList(modelID : Long): Array<String>{
        return api.getFieldList(modelID)
    }

    private fun getModelList() : Map<Long, String>{
        return api.modelList
    }

    private fun getModelList(minFieldCount: Int) : Map<Long, String>{
        return api.getModelList(minFieldCount)
    }

    private fun getModelName(modelID: Long): String {
        return api.getModelName(modelID)
    }

    private fun addNewDeck(deckName: String): Long {
        return api.addNewDeck(deckName)
    }

    private fun getSelectedDeckName(): String {
        return api.selectedDeckName
    }

    private fun getDeckList(): Map<Long, String>{
        return api.deckList
    }

    private fun getDeckName(deckID: Long): String {
        return api.getDeckName(deckID)
    }

    private fun getAnkiDroidPackageName(): String {
        return AddContentApi.getAnkiDroidPackageName(mContext)
    }

    private fun getApiHostSpecVersion(): Int {
        return api.apiHostSpecVersion
    }

    /**
     * Generates an Anki format furigana string if word is not its pronunciation
     * @return a string for Anki's furigana display.
     */
    private fun getFurigana(vocabulary: DictionaryEntry): String {
        return if (vocabulary.word == vocabulary.pronunciation) {
            vocabulary.pronunciation
        } else "$vocabulary.word[${vocabulary.pronunciation}]"
    }


    /**
     * Remove the duplicates from a list of note fields and tags
     * @param fields List of fields to remove duplicates from
     * @param tags List of tags to remove duplicates from
     * @param modelId ID of model to search for duplicates on
     */
    private fun removeDuplicates(fields: LinkedList<Array<String>>, tags: LinkedList<Set<String>>, modelId: Long) {
        // Build a list of the duplicate keys (first fields) and find all notes that have a match with each key
        val keys = ArrayList<String>(fields.size)
        for (f in fields) {
            keys.add(f[0])
        }
        val duplicateNotes = api.findDuplicateNotes(modelId, keys)
        // Do some sanity checks
        if (tags.size != fields.size) {
            throw IllegalStateException("List of tags must be the same length as the list of fields")
        }
        if (duplicateNotes.size() == 0 || fields.size == 0 || tags.size == 0) {
            return
        }
        if (duplicateNotes.keyAt(duplicateNotes.size() - 1) >= fields.size) {
            throw IllegalStateException("The array of duplicates goes outside the bounds of the original lists")
        }
        // Iterate through the fields and tags LinkedLists, removing those that had a duplicate
        val fieldIterator = fields.listIterator()
        val tagIterator = tags.listIterator()
        var listIndex = -1
        for (i in 0 until duplicateNotes.size()) {
            val duplicateIndex = duplicateNotes.keyAt(i)
            while (listIndex < duplicateIndex) {
                fieldIterator.next()
                tagIterator.next()
                listIndex++
            }
            fieldIterator.remove()
            tagIterator.remove()
        }
    }



//    fun addUpdateNote(noteID: Long, dictionaryEntry)
    //Exists in AnkiDroidAPI but not here
//    fun addNewCustomModel
//    fun addNotes
//    fun addNote
}
