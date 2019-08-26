package data.anki

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.SparseArray
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.ichi2.anki.FlashCardsContract
import com.ichi2.anki.api.AddContentApi
import com.ichi2.anki.api.NoteInfo
import com.limegrass.wanicchou.R
import com.limegrass.wanicchou.util.WanicchouToast

class AnkiDroidApi(private val context : Context)
    : IAnkiDroidApi {

    override val hasAvailableApi: Boolean
        get() = AddContentApi.getAnkiDroidPackageName(context) != null

    override val hasAnkiReadWritePermission: Boolean
        get() {
            val currentBuildVersion = Build.VERSION.SDK_INT
            val firstBuildRequiringPermissions = Build.VERSION_CODES.M
            val isBuildWithPermissionRequired = currentBuildVersion >= firstBuildRequiringPermissions
            return isBuildWithPermissionRequired &&
                    (ContextCompat.checkSelfPermission(context, FlashCardsContract.READ_WRITE_PERMISSION)
                            == PackageManager.PERMISSION_GRANTED)
        }

    override fun getAnkiSharedPreferencesDeckID(deckName : String) : Long? {
        val ankiDecksKey = context.getString(R.string.pref_anki_decks_key)
        val sharedPreferencesDecks = context.getSharedPreferences(ankiDecksKey, Context.MODE_PRIVATE)
        val deckID = sharedPreferencesDecks.getLong(deckName, -1)
        val sharedPreferenceDeckName =  api.getDeckName(deckID)
        return if (sharedPreferenceDeckName == null) null else deckID
    }

    override fun addAnkiSharedPreferencesDeckID(deckName : String, deckID : Long) {
        val ankiDecksKey = context.getString(R.string.pref_anki_decks_key)
        val decksDb = context.getSharedPreferences(ankiDecksKey, Context.MODE_PRIVATE)
        decksDb.edit().putLong(deckName, deckID).apply()
    }

    override fun getAnkiSharedPreferencesModelID(modelName : String, minimumFieldsCount : Int) : Long? {
        val ankiModelsKey = context.getString(R.string.pref_anki_models_key)
        val sharedPreferenceModels = context.getSharedPreferences(ankiModelsKey, Context.MODE_PRIVATE)
        val modelID = sharedPreferenceModels.getLong(modelName, -1)
        return if (modelID != -1L
                && api.getModelName(modelID) != null
                && api.getFieldList(modelID)!!.size >= minimumFieldsCount) { // could potentially have been renamed
            modelID
        }
        else {
            null
        }
    }

    override fun addAnkiSharedPreferencesModelID(modelName : String, modelID : Long) {
        val ankiModelsKey = context.getString(R.string.pref_anki_models_key)
        val modelsDb = context.getSharedPreferences(ankiModelsKey, Context.MODE_PRIVATE)
        modelsDb.edit().putLong(modelName, modelID).apply()
    }

    companion object{
        const val ANKI_READ_WRITE_PERMISSION_CALLBACK_CODE: Int = 420
    }

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
        if(!hasAnkiReadWritePermission){
//            requestAnkiReadWritePermission()
        }
        val noteID = api.addNote(modelID, deckID, fields, tags)
        val noteKey = fields.first()
        val toastText = context.getString(R.string.anki_added_toast, noteKey)
        showToast(toastText)
        return noteID
    }

    override fun getModelList(minFieldCount: Int): Map<Long, String> {
        return api.getModelList(minFieldCount)
    }

    private fun showToast(toastText : String){
        WanicchouToast.toast?.cancel()
        WanicchouToast.toast = Toast.makeText(context,
                toastText,
                Toast.LENGTH_LONG)
        WanicchouToast.toast!!.show()
    }

}
