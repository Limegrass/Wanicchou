package data.vocab.search;

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

import data.vocab.DictionaryType;
import data.vocab.MatchType;

public class SanseidoSearchWebView extends SanseidoSearch {

    WebView mWebView;
    Document html;
    DictionaryType currentDicType;
    List<String> relatedWordLinks;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public SanseidoSearchWebView(Context context, String wordToSearch, DictionaryType dictionaryType, MatchType matchType) throws IOException {
        super(wordToSearch, dictionaryType, matchType);
        currentDicType = dictionaryType;

        URL searchUrl = super.buildQueryURL(wordToSearch, dictionaryType, matchType);
        mWebView = new WebView(context);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.addJavascriptInterface(new HtmlParserInterface(), "HtmlParser");
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                parsePage();
                relatedWordLinks = findJSLinks(html);
            }
        });
        mWebView.loadUrl(searchUrl.toString());
    }

    public void navigateRelatedWordLink(int index){
        currentDicType = getRelatedWords().get(index).getDictionaryType();
        mWebView.loadUrl(relatedWordLinks.get(index));
        parsePage();
        relatedWordLinks = findJSLinks(html);
    }

    public void parsePage(){
        mWebView.loadUrl("javascript:window.HtmlParser.parse"+
                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
    }

    private class HtmlParserInterface{
        HtmlParserInterface(){
        }
        @JavascriptInterface
        public void parse(String html){
            SanseidoSearchWebView.this.html = Jsoup.parse(html);
            SanseidoSearch relatedWordSearch = new SanseidoSearch(SanseidoSearchWebView.this.html, DictionaryType.EJ);
            setVocabulary(relatedWordSearch.getVocabulary());
            setRelatedWords(relatedWordSearch.getRelatedWords());
        }
    }

    private List<String> findJSLinks(Document html){
        List<String> jsLinks = new ArrayList<>();

        Element table = html.select("table").get(RELATED_WORDS_TABLE_INDEX);
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

    public WebView getmWebView() {
        return mWebView;
    }

    public void setmWebView(WebView mWebView) {
        this.mWebView = mWebView;
    }

    public Document getHtml() {
        return html;
    }

    public void setHtml(Document html) {
        this.html = html;
    }

    public DictionaryType getCurrentDicType() {
        return currentDicType;
    }

    public void setCurrentDicType(DictionaryType currentDicType) {
        this.currentDicType = currentDicType;
    }

    public List<String> getRelatedWordLinks() {
        return relatedWordLinks;
    }

    public void setRelatedWordLinks(List<String> relatedWordLinks) {
        this.relatedWordLinks = relatedWordLinks;
    }
}
