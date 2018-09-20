# 和日帳 （わにっちょう）Wanicchou
Online dictionary drawing definitions from [Sanseido](https://www.sanseido.biz/) interfaced with the AnkiDroid API to quickly add words and their definitions.

Other languages: [日本語](README.jp.md)

[<img src="https://f-droid.org/badge/get-it-on.png"
     alt="Get it on F-Droid"
     height="90">](https://f-droid.org/packages/com.waifusims.wanicchou/)

## Table of Contents
  - [Usage](#usage)
  - [Potential Development](#potential-development)
  - [Sources](#sources)
  - [LICENSE](#license)


![Screenshot](/docs/app-image.png)

## Usage
* Type a word in the search box, then press enter to conduct a search.
* The floating action button sends the searched word into AnkiDroid.
* J-J and J-E preference can be changed in the settings. If English is entered, it will automatically search for an E-J definition entry.
* If it does not find the word you wanted, change try changing the match type, or checking the related words generated from the search.
* Autodeletion and Autosave settings are turned off by default. Change it in the settings to generate and manage an offline database on search.
* Drag the top to force an online reload.
* You can view saved searches through the options menu 

## Potential Development
<s>Save searched words and definitions into an SQLiteDB for offline usage.</s>

<s>SharedPreferences for which language dictionary the search should be conducted.</s>

Automatically select the E-J based on input being only in ASCII charset.

Add a way to import sentences before Anki import. (Could scrap from Twitter? or JMDict)

Add clozed type for the sentences.

Add a way to import voices before Anki import.

Label words by JLPT level.

Everything about the UI.

Figure out if it's even okay to use Sanseido's site like this.

Parse definition text for possible words to search instead of having it as an EditText for users to copy and paste themselves.

Furigana definition text after parsing, maybe with JE dict so there's less web requests.

Maybe try to network DB from queries.

## Sources
['ankidroid/apisample'](https://github.com/ankidroid/apisample):
    app/java/util.anki/AnkiDroidHelper.java
    app/java/util.anki/AnkiDroidConfig.java

## License
GNU GPL 3.0
