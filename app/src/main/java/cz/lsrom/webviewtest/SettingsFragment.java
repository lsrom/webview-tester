package cz.lsrom.webviewtest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Lukas Srom <lukas.srom@gmail.com>
 */
public class SettingsFragment extends Fragment implements MainActivity.FragmentLifecycle {
    @BindView(R.id.url)
    EditText url;

    @BindView(R.id.logs)
    TextView logs;

    private static String urlString = "";
    private static String logString = "";

    public static String getUrl (){
        return urlString;
    }

    public static void addLog (String log){
        logString = log + "\r\n" + logString;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick(R.id.ok_btn)
    public void clickOkBtn (){
        urlString = url.getText().toString().trim();
    }

    @OnClick(R.id.clear_btn)
    public void clickClearBtn (){
        logString = "";
        logs.setText(logString);
    }

    @Override
    public void onResumeFragment() {
        logs.setText(logString);
    }

    @Override
    public void reloadWebView() {
        // no web view in this fragment
    }


}
