package data.vocab.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
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

public class SanseidoSearchWebView extends SanseidoSearch implements Parcelable {

    private static String HTML_PARSER_NAME = "HtmlParser";

    Document mHtml;
    Context mContext;
    WebView mWebView;
    DictionaryType currentDicType;
    List<String> relatedWordLinks;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public SanseidoSearchWebView(Context context, String wordToSearch, DictionaryType dictionaryType, MatchType matchType) throws IOException {
        super(wordToSearch, dictionaryType, matchType);
        mContext = context;
        currentDicType = dictionaryType;

        URL searchUrl = super.buildQueryURL(wordToSearch, dictionaryType, matchType);
        mWebView = new WebView(context);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.addJavascriptInterface(new HtmlParserInterface(), HTML_PARSER_NAME);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //Calls parsePage, which calls the parseRelatedWordsPage
                parsePage();
            }
        });
        mWebView.loadUrl(searchUrl.toString());
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public SanseidoSearchWebView(Context context, String baseUrl, String pageSource, DictionaryType dictionaryType){
        super(pageSource, dictionaryType);
        mContext = context;
        mWebView = new WebView(context);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new HtmlParserInterface(), HTML_PARSER_NAME);

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                parsePage();
            }
        });

        String mimeType = "text/html"; //Defaults to text/html if null
        String encoding = "UTF-8";
        String historyUrl = null;
        mWebView.loadDataWithBaseURL(baseUrl, pageSource, mimeType, encoding, historyUrl);
    }

    public void parsePage(){
        mWebView.loadUrl("javascript:window.HtmlParser.parsePage"+
                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
    }

    private class HtmlParserInterface{
        HtmlParserInterface(){
        }
        @JavascriptInterface
        public void parsePage(String html){
            mHtml = Jsoup.parse(html);
            SanseidoSearch relatedWordSearch = new SanseidoSearch(mHtml, DictionaryType.EJ);
            setVocabulary(relatedWordSearch.getVocabulary());
            setRelatedWords(relatedWordSearch.getRelatedWords());
            relatedWordLinks = findJSLinks(mHtml);
        }
    }

    public void navigateRelatedWordLink(int index){
        currentDicType = getRelatedWords().get(index).getDictionaryType();
        mWebView.loadUrl(relatedWordLinks.get(index));
        parsePage();
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

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(mWebView.getUrl());
        parcel.writeString(mHtml.toString());
        parcel.writeString(currentDicType.toString());
        parcel.writeValue(relatedWordLinks);
        parcel.writeValue(mContext);
    }



    /**
     * Creator for parcelization.
     */
    public static final Parcelable.Creator<SanseidoSearchWebView> CREATOR
            = new Parcelable.Creator<SanseidoSearchWebView>(){
        @Override
        public SanseidoSearchWebView createFromParcel(Parcel parcel) {
            return new SanseidoSearchWebView(parcel);
        }

        @Override
        public SanseidoSearchWebView[] newArray(int size) {
            return new SanseidoSearchWebView[size];
        }
    };

    /**
     * Constructor from a parcel.
     * @param parcel The parcel to read from.
     */
    private SanseidoSearchWebView(Parcel parcel) {
        super(parcel);
        final ClassLoader classLoader = getClass().getClassLoader();
        String url = parcel.readString();
        String html = parcel.readString();

        currentDicType = DictionaryType.fromString(parcel.readString());
        relatedWordLinks = (List<String>)parcel.readValue(classLoader);
        mContext = (Context) parcel.readValue(classLoader);


        String mimeType = "text/html"; //Defaults to text/html if null
        String encoding = "UTF-8";
        String historyUrl = null;

        mWebView = new WebView(mContext);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new HtmlParserInterface(), HTML_PARSER_NAME);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                parsePage();
            }
        });

        mWebView.loadDataWithBaseURL(url, html, mimeType, encoding, historyUrl);
    }




}
