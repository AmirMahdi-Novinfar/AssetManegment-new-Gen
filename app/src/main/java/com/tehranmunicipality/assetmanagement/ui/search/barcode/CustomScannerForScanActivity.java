//package com.tehranmunicipality.assetmanagement.ui.search.barcode;
//
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.zxing.ResultPoint;
//import com.google.zxing.client.android.Intents;
//import com.journeyapps.barcodescanner.BarcodeCallback;
//import com.journeyapps.barcodescanner.BarcodeResult;
//import com.journeyapps.barcodescanner.CaptureManager;
//import com.journeyapps.barcodescanner.DecoratedBarcodeView;
//import com.tehranmunicipality.assetmanagement.R;
//
//import java.util.List;
//
//public class CustomScannerForScanActivity extends AppCompatActivity implements View.OnClickListener {
//
//    private static final String TAG = "CustomScannerActivity";
//
//    private DecoratedBarcodeView barcodeView;
//    private CaptureManager capture;
//    //private ImageView ivFlash;
//
//    private boolean isFlashOn;
//
//    /**
//     * Initializes the UI and creates the detector pipeline.
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.custom_scanner_for_scan_qr_activity);
//
//        isFlashOn = false;
//
//        barcodeView = initializeContent();
//
//        capture = new CaptureManager(this, barcodeView);
//        capture.initializeFromIntent(getIntent(), savedInstanceState);
//        capture.decode();
//
////        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.AZTEC); // Set barcode type
////        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
//        barcodeView.initializeFromIntent(getIntent());
//        barcodeView.decodeSingle(callback);
//
//        bindViews();
//        setupClicks();
//
//    }
//
//    private BarcodeCallback callback = new BarcodeCallback() {
//        @Override
//        public void barcodeResult(BarcodeResult result) {
//            Log.i("DEBUG", "barcodeResult:" + result.getText()); // QR/Barcode result
//            Intent intent = new Intent();
//            intent.putExtra(Intents.Scan.RESULT, result.getText());
//            setResult(RESULT_OK, intent);
//            finish();
//        }
//
//        @Override
//        public void possibleResultPoints(List<ResultPoint> resultPoints) {
//        }
//    };
//
//    /**
//     * Check if the device's camera has a Flashlight.
//     *
//     * @return true if there is Flashlight, otherwise false.
//     */
//    private boolean hasFlash() {
//        return getApplicationContext().getPackageManager()
//                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
//    }
//
//    public void switchFlashlight() {
//        if (isFlashOn) {
//            isFlashOn = false;
//            barcodeView.setTorchOff();
//            //ivFlash.setImageResource(R.drawable.flash_off);
//        } else {
//            isFlashOn = true;
//            barcodeView.setTorchOn();
//            //ivFlash.setImageResource(R.drawable.flash_on);
//        }
//    }
//
//    public void bindViews() {
//        //ivFlash = findViewById(R.id.ivFlash);
//    }
//
//    public void setupClicks() {
//
//        //ivFlash.setOnClickListener(this);
//    }
//
//    /**
//     * Override to use a different layout.
//     *
//     * @return the DecoratedBarcodeView
//     */
//    protected DecoratedBarcodeView initializeContent() {
//        return (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        capture.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        capture.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        capture.onDestroy();
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        capture.onDestroy();
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//
////            case R.id.ivFlash:
////                switchFlashlight();
////                break;
//        }
//    }
//}
//
//
