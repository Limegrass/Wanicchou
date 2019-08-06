package data.arch.models

interface IDictionaryEntry {
    val vocabulary : IVocabulary
    val definitions : List<IDefinition>
}
// Can change to List<>? if performance becomes of concern.
// Benefits of list: I don't have to allow null definitions, it makes sense to return empty lists
//                   No repeated vocabulary words, don't need to order by anything weird
//                   makes more sense in general when UI controls change.
// Benefits of flat: I can target an individual definition easier, which makes shared preferences simpler