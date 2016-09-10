package com.luigivincent.pennappsxiv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.regex.Pattern;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private static final Pattern codePattern = Pattern.compile("^\\w+,.+@.+,(\\d{3}-\\d{3}-\\d{4})?,.+,.+,[y|n]$");

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        String result = Encryptor.decode(rawResult.getText());
        Log.d("Decrypted result", result);
        if (codePattern.matcher(result).matches()) {
            Intent intent = new Intent(this, MailActivity.class);
            String[] delimited = result.split(",");
            intent.putExtra("name", delimited[0]);
            intent.putExtra("email", delimited[1]);
            intent.putExtra("number", delimited[2]);
            intent.putExtra("provider", delimited[3]);
            intent.putExtra("item", delimited[4]);
            intent.putExtra("public", delimited[5]);
            startActivity(intent);
        } else {
            Toast.makeText(ScannerActivity.this, R.string.invalid_code, Toast.LENGTH_SHORT).show();
            mScannerView.resumeCameraPreview(this);
        }
    }
}