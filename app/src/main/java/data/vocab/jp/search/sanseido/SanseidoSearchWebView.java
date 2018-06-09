package data.vocab.jp.search.sanseido;

import android.annotation.SuppressLint;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import data.vocab.jp.JapaneseDictionaryType;
import data.vocab.models.DictionaryType;
import data.vocab.models.DictionaryWebPage;
import data.vocab.OnJavaScriptCompleted;
import data.vocab.models.Search;
import data.vocab.models.Vocabulary;

public class SanseidoSearchWebView extends WebView implements DictionaryWebPage {

    private static String HTML_PARSER_NAME = "HtmlParser";

    private Document mHtml;
    private DictionaryType currentDictionaryType;
    private List<String> relatedWordLinks;
    private Search mSearch;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public SanseidoSearchWebView(final Context context, String wordToSearch, DictionaryType dictionaryType, SanseidoMatchType matchType, OnJavaScriptCompleted listener) throws IOException {
        super(context);
        currentDictionaryType = dictionaryType;

        URL searchUrl = SanseidoSearch.buildQueryURL(wordToSearch, dictionaryType, matchType);
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

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public SanseidoSearchWebView(Context context, String baseUrl, String pageSource, DictionaryType dictionaryType, OnJavaScriptCompleted listener){
        super(context);
        mSearch = new SanseidoSearch(pageSource, dictionaryType);
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
            relatedWordLinks = findJSLinks(mHtml);
            mSearch = new SanseidoSearch(mHtml, currentDictionaryType);
            listener.onJavaScriptCompleted();
        }
    }

    @Override
    public void navigateRelatedWordLinks(int index){
        currentDictionaryType = mSearch.getRelatedWords().get(index).getDictionaryType();
        this.loadUrl(relatedWordLinks.get(index));
        parsePage();
    }

    private List<String> findJSLinks(Document html){

        List<String> jsLinks = new ArrayList<>();

        Element table = html.select("table").get(SanseidoSearch.RELATED_WORDS_TABLE_INDEX);
        Elements rows = table.select("tr");

        for (Element row : rows) {

            Elements columns = row.select("td");

            Element entry = columns.select("a").first();

            if(entry != null){
                jsLinks.add(entry.attr("abs:href").toString());
            }
        }
        return jsLinks;
    }

    public DictionaryType getCurrentDictionaryType() {
        return currentDictionaryType;
    }

    public void setCurrentDictionaryType(JapaneseDictionaryType dictionaryType) {
        this.currentDictionaryType = dictionaryType;
    }

    public List<String> getRelatedWordLinks() {
        return relatedWordLinks;
    }

    public void setRelatedWordLinks(List<String> relatedWordLinks) {
        this.relatedWordLinks = relatedWordLinks;
    }


    @Override
    public Vocabulary getVocabulary(){
        return mSearch.getVocabulary();
    }

    @Override
    public Search getSearch() {
        return mSearch;
    }
}
