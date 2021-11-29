package com.SE_project.barcode_scanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("바코드 스캐너");

        ImageView ivCamera = findViewById(R.id.ivCamera);
        ImageView ivGallery = findViewById(R.id.ivGallery);

        ivCamera.setOnClickListener(this);
        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(""))
            }
        });
    }

    @Override
    public void onClick(View view) {
        scanCode();
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
            /*else {
                Toast.makeText(this,"아무것도 인식하지 못했습니다",Toast.LENGTH_SHORT).show();

            }*/
        }
        else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

}