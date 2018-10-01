package it.therickys93.wiki;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.google.zxing.Result;

public class QRCodeScannerActivity extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private static final int RC_PERMISSION = 10;
    private boolean mPermissionGranted;
    private CodeScannerView scannerView;
    private int cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);
        scannerView = (CodeScannerView) findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = false;
                requestPermissions(new String[] {Manifest.permission.CAMERA}, RC_PERMISSION);
            } else {
                mPermissionGranted = true;
                permissionOk();
            }
        } else {
            mPermissionGranted = true;
            permissionOk();
        }

    }

    private void permissionOk(){
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                QRCodeScannerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
                        vb.vibrate(100);
                        Toast.makeText(QRCodeScannerActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                        SharedPreferences settings = getSharedPreferences("MySettingsWiki", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("WIKI_SERVER", result.getText());
                        editor.commit();
                        finish();
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.qrcode_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.change_camera:
                if(cameraID == Camera.CameraInfo.CAMERA_FACING_FRONT)
                    cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
                else if(cameraID == Camera.CameraInfo.CAMERA_FACING_BACK)
                    cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
                mCodeScanner.setCamera(cameraID);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == RC_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true;
                mCodeScanner.startPreview();
            } else {
                mPermissionGranted = false;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mPermissionGranted) {
            mCodeScanner.startPreview();
            mCodeScanner.setCamera(cameraID);
        }
    }

    @Override
    protected void onPause() {
        if(mPermissionGranted) {
            mCodeScanner.releaseResources();
        }
        super.onPause();
    }
}
