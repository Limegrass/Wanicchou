```mermaid
graph TD;
    SearchActivity --> SettingsActivity;
    WordListAdapter --> WordListActivity;
    SearchActivity --> WordListActivity;
    SettingsFragment --> SettingsActivity;
    WanicchouSharedPreferenceHelper --> SearchActivity;
```

```mermaid
graph TD;
    SearchActivity --> |Request| SearchViewModel;
    SearchViewModel --> || VocabularyRepository
```
Get Vocabulary
Display the first result
From Vocabulary, grab the definition of it from the repo
    There can be multiple definitions per word LiveData<List<List<Definition>>>
        List<Definition> is per word, the outer list corresponds to the word
        Foreach word in LiveData<Vocabulary>, get List<Definition>

Maybe I don't need the whole Vocabulary? this could be potentially expensive.
Should test whether it's more time expensive to get List<Vocabulary> 
    or to get the all the relevant IDs and then display the only selected, querying for it after

# DB Diagram
```mermaid
classDiagram
Definition --> Dictionary : DictionaryID
Definition --> Vocabulary : VocabularyID

DefinitionNote --> Definition : DefinitionID
VocabularyNote --> Vocabulary : VocabularyID



VocabularyRelation --> Vocabulary : VocabularyID
VocabularyTag --> Vocabulary : VocabularyID

VocabularyTag --> Tag : TagID



DefinitionNote : INT DefinitionNoteID
DefinitionNote : INT DefinitionID
DefinitionNote : NVARCHAR(MAX) NoteText

VocabularyNote : INT VocabularyNoteID
VocabularyNote : INT VocabularyID
VocabularyNote : NVARCHAR(MAX) NoteText

Tag : INT TagID
Tag : NVARCHAR(100) TagText

VocabularyTag : INT VocabularyTagID
VocabularyTag : INT VocabularyID
VocabularyTag : INT TagID


Dictionary : INT DictionaryID
Dictionary : NVARCHAR(322) DictionaryName

VocabularyRelation : INT VocabularyRelationID
VocabularyRelation : INT SearchVocabularyID
VocabularyRelation : INT ResultVocabularyID

Definition : NVARCHAR(MAX) DefinitionText
Definition : INT DefinitionID
Definition : INT DictionaryID
Definition : INT VocabularyID
Definition : VARCHAR(2) LanguageCode

Vocabulary : INT VocabularyID
Vocabulary : NVARCHAR(420) Word
Vocabulary : NVARCHAR(420) Pronunciation
Vocabulary : VARCHAR(4) Pitch
Vocabulary : VARCHAR(2) LanguageCode
```
> Note: VocabularyRelationID and VocabularyTagID can be used for sorting later

> Drag right to assign the different options?
    Dictionary, MatchType, Language
    Populate Dictionary/Lang from DB ideally
