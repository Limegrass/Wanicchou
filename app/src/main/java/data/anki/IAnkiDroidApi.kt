package data.anki

import android.util.SparseArray
import com.ichi2.anki.api.NoteInfo

interface IAnkiDroidApi {
    val modelList : Map<Long, String>
    val deckList : Map<Long, String>
    val currentModelID : Long
    val apiHostSpecVersion : Int
    val selectedDeckName : String

    fun addNewDeck(deckName : String) : Long
    fun getDeckName(deckID : Long) : String?
    fun getModelName(modelID : Long) : String?
    fun getFieldList(modelID : Long) : Array<String>?
    fun updateNoteFields(noteID : Long, fields : Array<String>) : Boolean
    fun updateNoteTags(noteID : Long, tags : Set<String>) : Boolean
    fun findDuplicateNotes(modelID : Long, firstFieldValue : String) : List<NoteInfo>
    fun findDuplicateNotes(modelID : Long, firstFieldValues : List<String>) : SparseArray<List<NoteInfo>>
    fun <T> addNewCustomModel(configuration : IAnkiDroidConfig<T>, deckID : Long) : Long
    fun addNote(modelID : Long, deckID: Long, fields : Array<String>, tags : Set<String>) : Long
    fun getModelList(minFieldCount : Int) : Map<Long, String>

    // Maybe these could live in a separate interface
    val hasAvailableApi : Boolean
    val hasAnkiReadWritePermission : Boolean
    fun addAnkiSharedPreferencesDeckID(deckName : String, deckID : Long)
    fun getAnkiSharedPreferencesModelID(modelName : String, minimumFieldsCount : Int) : Long?
    fun addAnkiSharedPreferencesModelID(modelName : String, modelID : Long)
    fun getAnkiSharedPreferencesDeckID(deckName : String) : Long?
}