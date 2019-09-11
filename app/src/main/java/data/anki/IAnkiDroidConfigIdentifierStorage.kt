package data.anki

interface IAnkiDroidConfigIdentifierStorage {
    fun getDeckID(deckName : String) : Long?
    fun addDeckID(deckName : String, deckID : Long)

    fun getModelID(modelName : String, minimumFieldCount : Int = 1) : Long?
    fun addModelID(modelName : String, modelID : Long)
}