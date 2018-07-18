package data.vocab.jp.search.sanseido;

import android.annotation.SuppressLint;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

import data.vocab.WordListEntry;
import data.vocab.jp.JapaneseDictionaryType;
import data.vocab.models.DictionaryType;
import data.vocab.models.DictionaryWebPage;
import data.vocab.OnJavaScriptCompleted;
import data.vocab.models.SearchResult;
import data.vocab.models.Vocabulary;

public class SanseidoSearchWebView extends WebView implements DictionaryWebPage {

    private static String RELATED_WORDS_PAGER_ID = "_ctl0_ContentPlaceHolder1_ibtGoNext";
    private static String HTML_PARSER_NAME = "HtmlParser";

    private Document mHtml;
    private DictionaryType currentDictionaryType;
    private SearchResult mSearchResult;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public SanseidoSearchWebView(final Context context,
                                 String wordToSearch,
                                 DictionaryType dictionaryType,
                                 SanseidoMatchType matchType,
                                 OnJavaScriptCompleted listener) throws IOException {
        super(context);
        currentDictionaryType = dictionaryType;

        URL searchUrl = SanseidoSearchResult.buildQueryURL(wordToSearch, dictionaryType, matchType);
        this.addJavascriptInterface(new HtmlParserInterface(listener), HTML_PARSER_NAME);
        this.getSettings().setJavaScriptEnabled(true);
        this.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //Calls parsePage, which calls the parseRelatedWordsPage
                parsePage();
            }
        });
        this.loadUrl(searchUrl.toString());
    }

    //TODO: Maybe don't ned DicType, just grab from page
    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public SanseidoSearchWebView(Context context,
                                 String baseUrl,
                                 String pageSource,
                                 DictionaryType dictionaryType,
                                 OnJavaScriptCompleted listener){
        super(context);
        mSearchResult = new SanseidoSearchResult(pageSource, dictionaryType);
        this.getSettings().setJavaScriptEnabled(true);
        this.addJavascriptInterface(new HtmlParserInterface(listener), HTML_PARSER_NAME);

        this.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                parsePage();
            }
        });

        String mimeType = "text/html"; //Defaults to text/html if null
        String encoding = "UTF-8";
        String historyUrl = null;
        this.loadDataWithBaseURL(baseUrl, pageSource, mimeType, encoding, historyUrl);
    }

    public void parsePage(){
        this.loadUrl("javascript:window.HtmlParser.parsePage"+
                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
    }


    private class HtmlParserInterface {
        OnJavaScriptCompleted listener;
        HtmlParserInterface(OnJavaScriptCompleted listener){
            this.listener = listener;
        }
        @JavascriptInterface
        public void parsePage(String html){
            mHtml = Jsoup.parse(html);
            mSearchResult = new SanseidoSearchResult(mHtml, currentDictionaryType);
            listener.onJavaScriptCompleted();
        }
    }

    @Override
    public void navigateRelatedWord(WordListEntry relatedWord){
        //TODO: When navigating to Related Word, the related words doesn't change.
        currentDictionaryType = relatedWord.getDictionaryType();
        String link = relatedWord.getLink();
        this.loadUrl(link);
    }


    public DictionaryType getCurrentDictionaryType() {
        return currentDictionaryType;
    }

    public void setCurrentDictionaryType(JapaneseDictionaryType dictionaryType) {
        this.currentDictionaryType = dictionaryType;
    }


    @Override
    public Vocabulary getVocabulary(){
        return mSearchResult.getVocabulary();
    }

    @Override
    public SearchResult getSearch() {
        return mSearchResult;
    }

    @Override
    public String getUrl() {
        return super.getUrl();
    }

    @Override
    public Document getHtmlDocument() {
        return mHtml;
    }

}
