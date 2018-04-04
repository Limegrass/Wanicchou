# JJLD
J-J Dictionary drawing definitions from [Sanseido](https://www.sanseido.biz/) interfaced with the AnkiDroid API to quickly add words and their definitions.

![Screenshot](/docs/app-image.png)

## USAGE
Type a word in the search box, then press enter to conduct a search.
The floating action button sends the searched word into AnkiDroid

## TODO
Add related words as links.
Save searched words and definitions into an SQLiteDB for offline usage.
SharedPreferences for which language dictionary the search should be conducted.
Add a way to import sentences before Anki import.
Add clozed type for the sentences.
Add a way to import voices before Anki import.
Label words by JLPT level.
Everything about the UI.
Figure out if it's even okay to use Sanseido's site like this.

## SOURCES
['ankidroid/apisample'](https://github.com/ankidroid/apisample):
    app/java/util.anki/AnkiDroidHelper.java
    app/java/util.anki/AnkiDroidConfig.java

## LICENSE
Apache 2.0 as per the apisample and Apache 2.0 Redistribution Clause.
