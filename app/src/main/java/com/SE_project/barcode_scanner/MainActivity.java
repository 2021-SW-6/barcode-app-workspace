package com.SE_project.barcode_scanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("바코드 스캐너");
        ImageView ivCamera = findViewById(R.id.ivCamera);
        ImageView ivGallery = findViewById(R.id.ivGallery);

        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });
        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(""))
            }
        });
        Button btnApiTest = (Button)findViewById(R.id.btnApiTest);
        btnApiTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NaverApiActivity.class);
                startActivity(intent);
            }
        });
    }

    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator (this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult (int requestCode , int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode , resultCode , data);
        Log.v("onActivityResult: " ,Integer.toString(requestCode));
        if(result != null) {
            if(result.getContents() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(result.getContents());
                builder.setTitle("Scanning Result");
                builder.setNeutralButton("상품 검색", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(), GoodsInfoActivity.class);
                        intent.putExtra("barcodeNumber",result.getContents());
                        startActivity(intent);
                    }
                });
                builder.setPositiveButton("scan again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        scanCode();
                    }
                }).setNegativeButton("finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

}