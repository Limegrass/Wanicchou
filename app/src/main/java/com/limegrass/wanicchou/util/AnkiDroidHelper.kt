package com.limegrass.wanicchou.util
import data.anki.IAnkiDroidApi
import data.anki.IAnkiDroidConfig
import data.anki.WanicchouAnkiEntry
import java.util.*

/**
 * Interface between Wanicchou and the AnkiDroid api.
 */
class AnkiDroidHelper(private val api : IAnkiDroidApi,
                      private val configuration : IAnkiDroidConfig<WanicchouAnkiEntry>) {
    //<editor-fold desc="Public methods">
    // Attempts to find existing noteText and update it if it exists, else add it
    fun addUpdateNote(ankiEntry : WanicchouAnkiEntry,
                      tags: Set<String>): Long {
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

    //<editor-fold desc="Fields/Properties">
    /**
     * Returns an active model ID for the current configuration. Adds one if it does not exist.
     */
    private val wanicchouDeckID: Long
        get() {
            return api.getAnkiSharedPreferencesDeckID(configuration.deckName)
                    ?: ankiDeckID
                    ?: insertAndStoreNewAnkiConfigDeckID()
        }

    private fun insertAndStoreNewAnkiConfigDeckID() : Long {
        val addedDeckID = api.addNewDeck(configuration.deckName)
        api.addAnkiSharedPreferencesDeckID(configuration.deckName, addedDeckID)
        return addedDeckID
    }

    /**
     * Returns an active model ID for the current configuration. Adds one if it does not exist.
     */
    private val wanicchouModelID: Long
        get() {
            return api.getAnkiSharedPreferencesModelID(configuration.modelName, configuration.fields.size)
                    ?: ankiModelIDFromConfigurationName
                    ?: insertAndStoreNewAnkiConfigModelID()
        }

    private fun insertAndStoreNewAnkiConfigModelID() : Long {
        val addedModelID = api.addNewCustomModel(configuration, wanicchouDeckID)
        api.addAnkiSharedPreferencesModelID(configuration.modelName, addedModelID)
        return addedModelID
    }
    //</editor-fold>

    //<editor-fold desc="Helpers">
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

    /**
     * Helper get for WanicchouModelID
     */
    private val ankiModelIDFromConfigurationName : Long?
        get() {
            val modelList = api.getModelList(configuration.fields.size)
            for ((modelID, ankiModelName) in modelList) {
                if (ankiModelName == configuration.modelName) {
                    return modelID // first model wins
                }
            }
            return null
        }

    private val ankiDeckID : Long?
        get() {
            val deckList = api.deckList
            val ignoreCase = true
            for ((deckID, ankiDeckName) in deckList) {
                if (ankiDeckName.contains(configuration.deckName, ignoreCase)) {
                    return deckID
                }
            }
            return null
        }

    //TODO: Change click to expand a menu and add associated UI elements
    //TODO: Duplicate checking
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
