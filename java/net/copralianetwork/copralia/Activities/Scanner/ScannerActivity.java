package net.copralianetwork.copralia.Activities.Scanner;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;



import net.copralianetwork.copralia.R;
import net.copralianetwork.copralia.Utils.APIRequest;
import net.copralianetwork.copralia.Utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcos on 14/11/16.
 */

public class ScannerActivity extends AppCompatActivity implements  Detector.Processor<Barcode>,SurfaceHolder.Callback{

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private TextView barcodeInfo;
    private Boolean started = false;
    private Boolean result = false;
    private Handler mainHandler;

    private List<String> lastResults;
    private ProgressDialog carga;

    private Context mContext;

    public int SCAN_TYPE;
    public static final int PROD_SCAN = 1;
    public static final int USER_SCAN = 2;


    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        mContext = this;

        setContentView(R.layout.actividad_scanner);

        mainHandler = new Handler(this.getMainLooper());
        lastResults = new ArrayList<>();
        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        cameraView.getHolder().addCallback(this);

        barcodeInfo = (TextView) findViewById(R.id.code_info);

        BarcodeDetector.Builder builder = new BarcodeDetector.Builder(this);

        SCAN_TYPE = getIntent().getIntExtra("type",0);
        switch (SCAN_TYPE){
            case  PROD_SCAN:
                builder.setBarcodeFormats(Barcode.EAN_13 | Barcode.EAN_8);
                break;
            case USER_SCAN:
                builder.setBarcodeFormats(Barcode.QR_CODE);
                break;
            default:
                finish();
                break;
        }

        barcodeDetector = builder.build();
        barcodeDetector.setProcessor(this);

        cameraSource = new CameraSource
            .Builder(this, barcodeDetector)
            .setRequestedPreviewSize(640, 480)
            .setAutoFocusEnabled(true)
            .build();

        findViewById(R.id.scanner_keyboard_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openKeyboard();
            }
        });
    }
    public void scannerResult(String result){
        carga = ProgressDialog.show(this,"Analizando","Buscando codigo",true);

        pauseScan();
        final String barcodeValue = result;

        switch (SCAN_TYPE){
            case USER_SCAN:

                //if(!value.startsWith("userID:") && value.length() != 22 ){
                    releaseScan();
                    //break;
                //}

                //APIRequest request = new APIRequest()
                break;

            case PROD_SCAN:

                final APIRequest request = new APIRequest(this, Request.Method.GET, "http://192.168.1.218:8080/prod/ean/" + barcodeValue ,null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(final JSONObject response) {
                                try {
                                    carga.cancel();
                                    final String prodID = String.valueOf(response.getInt("ID"));
                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                                    View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_prod_confirm, null);
                                    dialogBuilder.setTitle(response.getString("nombre"));
                                    ((TextView) dialogView.findViewById(R.id.prod_marca)).setText(response.getString("marca"));
                                    Glide.with(mContext).load(response.getString("imagen")).into((ImageView)dialogView.findViewById(R.id.prod_imagen));


                                    dialogBuilder.setView(dialogView);
                                    dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent();
                                            intent.putExtra("prodID",prodID);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                    });
                                    dialogBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            releaseScan();
                                            dialog.cancel();
                                        }
                                    });
                                    dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            releaseScan();
                                            dialog.cancel();
                                        }
                                    });
                                    dialogBuilder.show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            releaseScan();
                            Toast.makeText(mContext,"Producto no encontrado",Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(request);
                break;

        }
        barcodeInfo.setText(result);
    }


    public void openKeyboard(){
        pauseScan();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Introducir código");

        final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_scanner_keyboard, null);

        /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view.findViewById(R.id.barcode_text), InputMethodManager.SHOW_IMPLICIT);
        */
        builder.setView(view);

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String result = ((TextView) view.findViewById(R.id.barcode_text)).getText().toString();
                scannerResult(result);
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                releaseScan();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                releaseScan();
            }
        });

        builder.show();
    }

    public void releaseScan(){
        this.result = false;
        barcodeInfo.setText("Released");
        if(carga != null){
            carga.cancel();
        }
    }

    public void pauseScan(){
        this.result = true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScanner();
                } else {
                    Toast.makeText(getBaseContext(), "Permiso denegado", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void startScanner() {
        if(!started){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                try {
                    started = true;
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException e) {
                    finish();
                }
            }
        }
    }


    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {
        final SparseArray<Barcode> barcodes = detections.getDetectedItems();
        if (barcodes.size() != 0) {
            if(!result){
                /* Pasamos el resultado al thread principal */
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        String barcode = barcodes.valueAt(0).displayValue;
                        if(!lastResults.contains(barcode)){
                            lastResults.add(barcode);
                            scannerResult(barcode);
                        }
                    }
                };
                mainHandler.post(myRunnable);
            }
        }
    }
    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startScanner();
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraSource.stop();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
    @Override
    public void release() {}
}
