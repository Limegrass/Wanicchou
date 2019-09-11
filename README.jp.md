# 和日帳 （わにっちょう）Wanicchou
[三省堂](https://www.sanseido.biz)から定義を検索して、暗記ドロイドへ送られるオンライン辞書アプリです。

[<img src="https://gitlab.com/fdroid/artwork/raw/master/badge/get-it-on-jp.png"
     alt="Get it on F-Droid"
     height="90">](https://f-droid.org/packages/com.waifusims.wanicchou/)
[<img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png"
      alt="Get it on Google Play Store"
      height="90">](https://play.google.com/store/apps/details?id=com.limegrass.wanicchou)

他の言語: [English](README.md)

## 目次
  - [遣い方](#遣い方)
  - [可能な開発](#可能な開発)
  - [参考したレポジトリ](#参考したレポジトリ)
  - [ライセンス](#ライセンス)


![Screenshot](/docs/app-image.png)

## 遣い方
* 検索したい単語を検索箱に打ったら、Enterキーを打って、検索が始まります。
* FAB（桃色のボタン）を押せば、暗記ドロイドへ新しいカードが作て送ります。
* 設定には「国語」と「和英」の選択があります。英和は英語を打ったら、自動に英和の定義が出ます。
* 定義が出来なかったら、設定で別の調べ方を変えてみればいいだと思います。
* オートセーブとオートデリートは出来ますが、ディファインはしません。設定で変えられます。
* 上からスワイプして、強制にオンライン辞書に検索出来ます。
* セーブした検索はオプションメニューのボタンにボタンがあります。


## 可能な開発
<s>検索した単語をオッフラインデータベースにする</s>

<s>辞書型が選べるにする</s>

入力をみて、辞書型を自動に選ぶこと

ネートから文脈とか例文を調べてアプリに入れること。
（ツイッターとかから？JMDICTも）

CLOZED型の暗記ドロイドカード？

ネートから発音を調べ入れること
（JMDICTから？）

日本語能力試験の級を検索した単語に付けること。

UIのこと（全て）。。。

三省堂のサイトはこの遣い方は大丈夫かなあ。。。？

定義の単語を押せば、それを検索すること。

定義の漢字にふりがなを付けること。（JMDICT？）

皆の検索で自分のオンラインデータベースを作ること。

## 参考したレポジトリ
['ankidroid/apisample'](https://github.com/ankidroid/apisample):
    app/java/util.anki/AnkiDroidHelper.java
    app/java/util.anki/AnkiDroidConfig.java

## ライセンス
GNU GPL 3.0
