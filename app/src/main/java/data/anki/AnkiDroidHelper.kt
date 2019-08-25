package data.anki
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ichi2.anki.api.AddContentApi
import com.ichi2.anki.api.AddContentApi.READ_WRITE_PERMISSION
import java.util.*

/**
 * Interface between Wanicchou and the AnkiDroid api.
 */
class AnkiDroidHelper(context : Context,
                      private val callbackActivity : Activity,
                      private val api : IAnkiDroidApi,
                      private val configuration : IAnkiDroidConfig<WanicchouAnkiEntry>) {
    //<editor-fold desc="Fields/Properties">
    companion object {
        const val ANKI_PERMISSION_REQUEST_CALLBACK_CODE : Int = 420
        private const val SHARED_PREF_DECK_DB = "com.ichi2.anki.api.decks"
        private const val SHARED_PREF_MODEL_DB = "com.ichi2.anki.api.models"
    }

    private val applicationContext = context.applicationContext

    /**
     * Checks for the DeckID in sharedPref, then in AnkiDroid
     * Adds the deck if it doesn't exist in either
     * @return DeckID of the Wanicchou Deck
     */
    private val wanicchouDeckID: Long
        get() {
            //Check shared preferences, then AnkiDroid for a valid deckID
            val deckIDFromSharedPreferences = sharedPreferencesDeckID
            val deckNameFromSharedPreferenceID = api.getDeckName(deckIDFromSharedPreferences)
            if(!deckNameFromSharedPreferenceID.isNullOrBlank()){
                return deckIDFromSharedPreferences
            }
            val addedDeckID = api.addNewDeck(configuration.deckName)
            storeDeckReference(configuration.deckName, addedDeckID)
            return addedDeckID
        }

    private val sharedPreferencesDeckID : Long
    get() {
        val sharedPreferencesDecks = applicationContext.getSharedPreferences(SHARED_PREF_DECK_DB,
                Context.MODE_PRIVATE)
        return sharedPreferencesDecks.getLong(configuration.deckName, -1)
    }

    /**
     * helper method to retrieve the model ID for JJLD
     * @return the model ID for JJLD in Anki
     */
    private val wanicchouModelID: Long
        get() {
            var modelID = findModelIDByName(configuration.modelName, configuration.fields.size)
            if (modelID == null) {
                modelID = api.addNewCustomModel(configuration, wanicchouDeckID)
                storeModelReference(configuration.modelName, modelID)
            }
            return modelID
        }
    //</editor-fold>

    //<editor-fold desc="Public methods">
    /**
     * Whether or not the API is available to use.
     * The API could be unavailable if AnkiDroid is not installed or the user explicitly disabled the API
     * @return true if the API is available to use
     */
    fun isApiAvailable(): Boolean {
        return AddContentApi.getAnkiDroidPackageName(applicationContext) != null
    }

    private fun shouldRequestPermission(): Boolean {
        val currentBuildVersion = Build.VERSION.SDK_INT
        val firstBuildRequiringPermissions = Build.VERSION_CODES.M
        val isBuildWithPermissionRequired = currentBuildVersion >= firstBuildRequiringPermissions
        return isBuildWithPermissionRequired &&
                (ContextCompat.checkSelfPermission(applicationContext, READ_WRITE_PERMISSION)
                        != PackageManager.PERMISSION_GRANTED)
    }

    /**
     * The callbackActivity must implement onRequestPermissionResult and include the callback code.
     */
    private fun requestPermission() {
        ActivityCompat.requestPermissions(callbackActivity,
                arrayOf(READ_WRITE_PERMISSION),
                ANKI_PERMISSION_REQUEST_CALLBACK_CODE)
    }

    private fun getExistingNoteID(ankiEntry: WanicchouAnkiEntry) : Long? {
        val existingNotes = api.findDuplicateNotes(wanicchouModelID, ankiEntry.vocabulary.word)
        for (note in existingNotes) {
            val entryFromNote = configuration.mapFromNoteFields(note.fields)
            //All existing notes already have same word
            if (entryFromNote.vocabulary.language == ankiEntry.vocabulary.language
                    && entryFromNote.definition.language == ankiEntry.definition.language
                    && entryFromNote.definition.dictionary == ankiEntry.definition.dictionary
                    && entryFromNote.vocabulary.pronunciation == ankiEntry.vocabulary.pronunciation
                    && entryFromNote.definition.definitionText == ankiEntry.definition.definitionText) {
                return note.id
            }
        }
        return null
    }

    // Attempts to find existing noteText and update it if it exists, else add it
    fun addUpdateNote(ankiEntry : WanicchouAnkiEntry,
                      tags: Set<String>): Long {
        if(shouldRequestPermission()){
            requestPermission()
        }
        val existingNoteID = getExistingNoteID(ankiEntry)
        val fields = configuration.mapToNoteFields(ankiEntry)
        return if (existingNoteID == null){
            api.addNote(wanicchouModelID, wanicchouDeckID, fields, tags)
        }
        else{
            api.updateNoteFields(existingNoteID, fields)
            api.updateNoteTags(existingNoteID, tags)
            existingNoteID
        }
    }
    //</editor-fold>

    /**
     * Save a mapping from deckName to getDeckId in the SharedPreferences
     */
    private fun storeDeckReference(deckName: String, deckId: Long) {
        val decksDb = applicationContext.getSharedPreferences(SHARED_PREF_DECK_DB, Context.MODE_PRIVATE)
        decksDb.edit().putLong(deckName, deckId).apply()
    }

    /**
     * Save a mapping from modelName to modelId in the SharedPreferences
     */
    private fun storeModelReference(modelName: String, modelId: Long) {
        val modelsDb = applicationContext.getSharedPreferences(SHARED_PREF_MODEL_DB, Context.MODE_PRIVATE)
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
        val modelsDb = applicationContext.getSharedPreferences(SHARED_PREF_MODEL_DB, Context.MODE_PRIVATE)
        val prefsModelID = modelsDb.getLong(modelName, -1L)
        // if we have a reference saved to modelName and it exists and has at least numFields then return it
        val fieldsList = api.getFieldList(prefsModelID)!!
        if (prefsModelID != -1L && api.getModelName(prefsModelID) != null
                && fieldsList.size >= numFields) { // could potentially have been renamed
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
     * Get the ID of the deck which matches the deck name for wanicchou
     * @return the ID of the deck that has given name, or null if no deck was found
     */
    private val ankiDeckID : Long?
    get() {
        val deckList = api.deckList
        val ignoreCase = true
        for ((key, value) in deckList) {
            if (value.contains(configuration.deckName, ignoreCase)) {
                return key
            }
        }
        return null
    }


    //TODO: Change click to expand a menu and add associated UI elements
    //TODO: Maybe implement a clozed type when sentence search is included
    //TODO: Duplicate checking

    //<editor-fold desc="Helpers">
    /**
     * Remove the duplicates from a list of noteText fields and tags
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
    //</editor-fold>
}
