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

public class SanseidoSearchWebView extends WebView {

    private static String HTML_PARSER_NAME = "HtmlParser";

    private Document mHtml;
    private Context mContext;
    private DictionaryType currentDicType;
    private List<String> relatedWordLinks;
    private SanseidoSearch mSanseidoSearch;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public SanseidoSearchWebView(final Context context, String wordToSearch, DictionaryType dictionaryType, MatchType matchType, OnJavaScriptCompleted listener) throws IOException {
        super(context);
        mContext = context;
        currentDicType = dictionaryType;

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
        mSanseidoSearch = new SanseidoSearch(pageSource, dictionaryType);
        mContext = context;
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
            mSanseidoSearch = new SanseidoSearch(mHtml, DictionaryType.EJ);
            relatedWordLinks = findJSLinks(mHtml);
            listener.onJavaScriptCompleted();
        }
    }

    public void navigateRelatedWordLink(int index){
        currentDicType = mSanseidoSearch.getRelatedWords().get(index).getDictionaryType();
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


    public SanseidoSearch getmSanseidoSearch() {
        return mSanseidoSearch;
    }

    public void setmSanseidoSearch(SanseidoSearch mSanseidoSearch) {
        this.mSanseidoSearch = mSanseidoSearch;
    }

}
