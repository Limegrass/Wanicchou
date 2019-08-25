package data.anki

interface IAnkiDroidConfig<T> {
    val deckName : String
    val modelName : String
    val fields : Array<String>
    val cardFormats : List<CardFormat>
    val frontSideKey : String
    val backSideKey : String
    val css : String
    val sortField : Int?
    fun mapToNoteFields(noteEntry : T) : Array<String>
    fun mapFromNoteFields(fields : Array<String>) : T
}