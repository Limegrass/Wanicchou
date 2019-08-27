package data.anki

/**
 * Helper to encompass the logic of an anki card add/update
 */
class AnkiDroidHelper(private val ankiDroidApi : IAnkiDroidApi,
                      private val configuration : IAnkiDroidConfig<WanicchouAnkiEntry>,
                      private val ankiIdStorage: IAnkiDroidConfigIdentifierStorage) {
    //<editor-fold desc="Public methods">
    fun addUpdateNote(ankiEntry : WanicchouAnkiEntry,
                      tags: Set<String>): Long {
        val existingNoteID = getExistingNoteID(ankiEntry)
        val fields = configuration.mapToNoteFields(ankiEntry)
        return if (existingNoteID == null){
            ankiDroidApi.addNote(wanicchouModelID, wanicchouDeckID, fields, tags)
        }
        else{
            ankiDroidApi.updateNoteFields(existingNoteID, fields)
            ankiDroidApi.updateNoteTags(existingNoteID, tags)
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
            return storedDeckID ?: run {
                val deckID = ankiNameMatchedDeckID ?: ankiDroidApi.addNewDeck(configuration.deckName)
                ankiIdStorage.addDeckID(configuration.deckName, deckID)
                return deckID
            }
        }
    // Helper property
    private val storedDeckID : Long?
        get () {
            val deckID = ankiIdStorage.getDeckID(configuration.deckName) ?: return null
            ankiDroidApi.getDeckName(deckID) ?: return null
            return deckID
        }

    /**
     * Returns an active model ID for the current configuration. Adds one if it does not exist.
     */
    private val wanicchouModelID: Long
        get() {
            return storedModelID ?: run {
                val modelID = ankiNameMatchedModelID
                        ?: ankiDroidApi.addNewCustomModel(configuration.modelName,
                                configuration.fields,
                                configuration.cardFormats.map { it.formatName }.toTypedArray(),
                                configuration.cardFormats.map { it.questionFormat }.toTypedArray(),
                                configuration.cardFormats.map { it.answerFormat }.toTypedArray(),
                                configuration.css,
                                wanicchouDeckID,
                                configuration.sortField)
                ankiIdStorage.addModelID(configuration.modelName, modelID)
                return modelID
            }
        }
    private val storedModelID : Long?
        get(){
            val minimumFieldCount = configuration.fields.size
            val modelID = ankiIdStorage.getModelID(configuration.modelName,
                                                   minimumFieldCount) ?: return null
            ankiDroidApi.getModelName(modelID) ?: return null
            if(ankiDroidApi.getFieldList(modelID)!!.size >= minimumFieldCount) {
                return modelID
            }
            return null
        }
    //</editor-fold>

    //<editor-fold desc="Helpers">
    //TODO: Get working duplicate checking
    private fun getExistingNoteID(ankiEntry: WanicchouAnkiEntry) : Long? {
        val existingNotes = ankiDroidApi.findDuplicateNotes(wanicchouModelID, ankiEntry.vocabulary.word)
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

    private val ankiNameMatchedModelID : Long?
        get() {
            val modelList = ankiDroidApi.getModelList(configuration.fields.size)
            val ignoreCase = true
            for ((modelID, ankiModelName) in modelList) {
                if (ankiModelName.equals(configuration.modelName, ignoreCase))  {
                    return modelID
                }
            }
            return null
        }

    private val ankiNameMatchedDeckID : Long?
        get() {
            val deckList = ankiDroidApi.deckList
            val ignoreCase = true
            for ((deckID, ankiDeckName) in deckList) {
                if (ankiDeckName.contains(configuration.deckName, ignoreCase)) {
                    return deckID
                }
            }
            return null
        }
    //</editor-fold>
}
