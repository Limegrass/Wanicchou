package data.anki

import android.content.Context
import android.util.SparseArray
import com.ichi2.anki.api.AddContentApi
import com.ichi2.anki.api.NoteInfo

class AnkiDroidApi(context : Context) : IAnkiDroidApi {

    private val api = AddContentApi(context)

    override val modelList : Map<Long, String>
        get() =  api.modelList
    override val deckList : Map<Long, String>
        get() =  api.deckList
    override val currentModelID: Long
        get() = api.currentModelId
    override val apiHostSpecVersion: Int
        get() = api.apiHostSpecVersion
    override val selectedDeckName: String
        get() = api.selectedDeckName

    override fun addNewDeck(deckName: String): Long {
        return api.addNewDeck(deckName)
    }

    /**
     * null if deckID is not found.
     */
    override fun getDeckName(deckID: Long): String? {
        return api.getDeckName(deckID)
    }

    /**
     * null if modelID is not found.
     */
    override fun getModelName(modelID: Long): String? {
        return api.getModelName(modelID)
    }

    /**
     * null if modelID is not found.
     */
    override fun getFieldList(modelID: Long): Array<String>? {
        return api.getFieldList(modelID)
    }

    /**
     * true is note is found and updated
     */
    override fun updateNoteFields(noteID: Long, fields: Array<String>): Boolean {
        return api.updateNoteFields(noteID, fields)
    }

    /**
     * true is note is found and updated
     */
    override fun updateNoteTags(noteID: Long, tags: Set<String>): Boolean {
        return api.updateNoteTags(noteID, tags)
    }

    override fun findDuplicateNotes(modelID: Long, firstFieldValue: String): List<NoteInfo> {
        return api.findDuplicateNotes(modelID, firstFieldValue)
    }

    /**
     * Each index in the SparseArray corresponds to the index in the given list of keys
     */
    override fun findDuplicateNotes(modelID: Long, firstFieldValues : List<String>)
            : SparseArray<List<NoteInfo>> {
        return api.findDuplicateNotes(modelID, firstFieldValues) ?: SparseArray()
    }

    override fun <T> addNewCustomModel(configuration: IAnkiDroidConfig<T>,
                                   deckID : Long): Long {
        return api.addNewCustomModel(configuration.deckName,
                configuration.fields,
                configuration.cardFormats.map { it.formatName }.toTypedArray(),
                configuration.cardFormats.map { it.questionFormat }.toTypedArray(),
                configuration.cardFormats.map { it.answerFormat }.toTypedArray(),
                configuration.css,
                deckID,
                configuration.sortField)
    }

    override fun addNote(modelID: Long, deckID: Long, fields: Array<String>, tags: Set<String>): Long {
        return api.addNote(modelID, deckID, fields, tags)
    }

    override fun getModelList(minFieldCount: Int): Map<Long, String> {
        return api.getModelList(minFieldCount)
    }

}
