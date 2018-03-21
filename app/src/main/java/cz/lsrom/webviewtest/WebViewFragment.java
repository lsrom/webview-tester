package cz.lsrom.webviewtest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Lukas Srom <lukas.srom@gmail.com>
 */
public class WebViewFragment extends Fragment implements MainActivity.FragmentLifecycle {
    private static final String TAG = WebViewFragment.class.getSimpleName();

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS", Locale.getDefault());
    private static final String SEPARATOR = " : ";
    private static final String LEFT = " [";
    private static final String RIGHT = "] ";

    @BindView(R.id.webview)
    WebView webView;

    Unbinder unbinder;

    public WebViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        unbinder = ButterKnife.bind(this, view);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setSupportMultipleWindows(true);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                StringBuilder builder = new StringBuilder();
                builder
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
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResumeFragment() {
    }

    @Override
    public void reloadWebView() {
        Log.d(TAG, "Loading URL: " + SettingsFragment.getUrl());
        webView.loadUrl(SettingsFragment.getUrl());
    }
}
