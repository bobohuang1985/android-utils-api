package utils.bobo.com.boboutils.App;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import utils.bobo.com.boboutils.R;

public class WebViewActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "WebViewActivity";
    private EditText mUrlEditText;
    private WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_webview);
        this.findViewById(R.id.btGo).setOnClickListener(this);
        mWebView = (WebView) findViewById(R.id.webView);
        mUrlEditText = (EditText) findViewById(R.id.etUrl);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {

            }
        });
    }
//    private Handler mHandler= new Handler();
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btGo: {
                String url = mUrlEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(url)){
                    mWebView.loadUrl(url);
                }
                break;
            }
        }
    }
}
