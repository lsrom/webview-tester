package cz.lsrom.webviewtest;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Lukas Srom <lukas.srom@gmail.com>
 */
public class WebViewFragment extends Fragment implements FragmentLifecycle {
    private static final String TAG = WebViewFragment.class.getSimpleName();

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS", Locale.getDefault());
    private static final String SEPARATOR = " : ";
    private static final String LEFT = " [";
    private static final String RIGHT = "] ";

    @BindView(R.id.webview)
    WebView webView;

    private Unbinder unbinder;
    private boolean trustSsl = false;

    public WebViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        unbinder = ButterKnife.bind(this, view);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setSupportMultipleWindows(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                StringBuilder builder = new StringBuilder()
                        .append(sdf.format(new Date()))
                        .append(LEFT)
                        .append(consoleMessage.lineNumber())
                        .append(RIGHT)
                        .append(consoleMessage.messageLevel().name())
                        .append(SEPARATOR)
                        .append(consoleMessage.message());
                SettingsFragment.addLog(builder.toString());

                return super.onConsoleMessage(consoleMessage);
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.d(TAG, "Received error.");
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // show ssl error and proceed
                SettingsFragment.addLog("SSL error: " + error.getUrl() + ": " + error.toString());
                if (trustSsl){
                    handler.proceed();
                    SettingsFragment.addLog("!!!Proceeding to load the page despite insecure content!!!");
                } else {
                    handler.cancel();
                    SettingsFragment.addLog("Not loading insecure content. You can change this behavior by flipping the switch above.");
                }
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.d(TAG, "HTTP error: " + errorResponse.getReasonPhrase());
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "onPageStarted: " + url);
                showProgressDialog(url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished: " + url);
                hideProgressDialog();
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                Log.d(TAG, "onLoadResource: " + url);
            }
        });

        SettingsFragment.addLog(webView.getSettings().getUserAgentString());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResumeFragment() {
        trustSsl = SPManager.trustAllSslCerts(getContext());
        Log.d(TAG, "Trust all SSL certificates set to: " + trustSsl);
    }

    @Override
    public void reloadWebView() {
        Log.d(TAG, "Loading URL: " + SettingsFragment.getUrl());
        if (webView != null) webView.loadUrl(SettingsFragment.getUrl());
    }

    private void showProgressDialog (String url){
        // todo
    }

    private void hideProgressDialog (){
        // todo
    }
}
