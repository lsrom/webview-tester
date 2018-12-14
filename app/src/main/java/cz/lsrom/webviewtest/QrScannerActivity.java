package cz.lsrom.webviewtest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * @author Lukas Srom <lukas.srom@gmail.com>
 */
public class QrScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final String TAG = QrScannerActivity.class.getSimpleName();

    @BindView(R.id.qr_scanner_view)
    protected ZXingScannerView scannerView;

    public static Intent getStartIntent (@NonNull Context context){
        return new Intent(context, QrScannerActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qr_scanner);
        ButterKnife.bind(this, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (scannerView != null) {
            scannerView.stopCamera();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (scannerView != null) {
            scannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
            scannerView.startCamera();          // Start camera on resume
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        String result = rawResult.getText();
        Log.d(TAG, result);

        startActivity(MainActivity.getStartIntent(result, this));
    }


}
