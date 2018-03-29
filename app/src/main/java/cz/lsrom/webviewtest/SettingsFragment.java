package cz.lsrom.webviewtest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Lukas Srom <lukas.srom@gmail.com>
 */
public class SettingsFragment extends Fragment implements FragmentLifecycle, ILogSender {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final String LOG_FILE_NAME = "%s-logs.txt";

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
        if (log == null){
            return;
        }
        logString = log + "\r\n" + logString;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        ButterKnife.bind(this, view);

        String stringUrl = ((MainActivity)getActivity()).getUrl();
        if (stringUrl != null){
            url.setText(stringUrl);
        }

        url.setSelection(url.getText().length());

        return view;
    }

    @OnClick(R.id.ok_btn)
    public void clickOkBtn (){
        urlString = url.getText().toString().trim();
        ITabChanger tabChanger = ((ITabChanger)getActivity());
        if (tabChanger == null){
            return;
        }
        tabChanger.showWebViewTab();
    }

    @OnClick(R.id.clear_btn)
    public void clickClearBtn (){
        logString = "";
        logs.setText(logString);
    }

    @Override
    public void onResumeFragment() {
        if (logs == null){
            return;
        }
        logs.setText(logString);
    }

    @Override
    public void reloadWebView() {
        // no web view in this fragment
    }

    @Override
    public void sendLogs() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject));
        i.putExtra(Intent.EXTRA_TEXT   , getString(R.string.mail_body));
        try {
            i.putExtra(Intent.EXTRA_STREAM, logsToFile());
        } catch (IOException e) {
            Log.e(TAG, "Can't create file with logs: " + e.getMessage());
        }
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Log.d(TAG, "Can't create chooser for email: " + ex.getMessage());
        }
    }

    private Uri logsToFile () throws IOException {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                String.format(LOG_FILE_NAME, sdf.format(new Date())));

        if (!file.exists()){
            file.createNewFile();
        }
        FileOutputStream outputStreamWriter = new FileOutputStream(file);
        try {
            outputStreamWriter.write(logString.getBytes("UTF-8"));
        } catch (IOException e) {
            Log.e(TAG, "UTF-8 is unsupported encoding.");
        }
        outputStreamWriter.close();
        return Uri.fromFile(file);
    }
}
