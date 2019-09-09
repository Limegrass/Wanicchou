package room.dbo.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import data.models.IVocabulary
import room.database.WanicchouDatabase
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.runBlocking

@Parcelize
@Entity(tableName = "Vocabulary",
        indices = [Index(
                value = arrayOf("Word",
                                "Pronunciation",
                                "LanguageID",
                                "Pitch"),
                unique = true)]
)

data class Vocabulary (
    @ColumnInfo(name = "Word")
    override var word: String,

    @ColumnInfo(name = "Pronunciation")
    override var pronunciation: String,

    @ColumnInfo(name = "Pitch")
    override var pitch: String,

    @ColumnInfo(name = "LanguageID")
    override var language: data.enums.Language,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyID")
    var vocabularyID: Long = 0) : Parcelable, IVocabulary {

    constructor (model : IVocabulary, vocabularyID: Long = 0)
            : this(model.word, model.pronunciation, model.pitch, model.language, vocabularyID)

    override fun toString(): String {
        return word
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is IVocabulary){
            return false
        }
        return this.word == other.word
                && this.pronunciation == other.pronunciation
                && this.language == other.language
                && this.pitch == other.pitch
    }

    override fun hashCode(): Int {
        return word.hashCode() xor
                pronunciation.hashCode() xor
                language.hashCode() xor
                pitch.hashCode()

    }
    companion object {
        /**
         * Gets the VocabularyID from the IVocabulary if it's an instance of the entity class,
         * else will perform a database request for it from the database given
         */
        fun getVocabularyID(vocabulary: IVocabulary,
                            database: WanicchouDatabase) : Long? {
            return if (vocabulary is Vocabulary){
                vocabulary.vocabularyID
            }
            else runBlocking {
                database.vocabularyDao().getVocabularyID(vocabulary.word,
                        vocabulary.pronunciation,
                        vocabulary.pitch,
                        vocabulary.language)
            }
        }

    }

}
