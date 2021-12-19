package com.SE_project.barcode_scanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private final int GET_GALLERY_IMAGE = 200;
    private final int CAMERA_SCAN = 49374;      //request code 설정

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("바코드 스캐너");
        ImageView ivCamera = findViewById(R.id.ivCamera);
        ImageView ivGallery = findViewById(R.id.ivGallery);

        ivCamera.setOnClickListener(new View.OnClickListener() {    //카메라 이미지를 눌렀을 때 작동할 기능
            @Override
            public void onClick(View view) {
                scanCode();
            }   //카메라로 바코드 스캔
        });

        ivGallery.setOnClickListener(new View.OnClickListener() {   //갤러리 이미지를 눌렀을 때 작동할 기능
            @Override
            public void onClick(View view) {        //갤러리에 있는 이미지에서 바코드 스캔
                Intent pickIntent = new Intent(Intent.ACTION_PICK);
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(pickIntent, GET_GALLERY_IMAGE);
            }
        });

        Button btnApiTest = (Button)findViewById(R.id.btnApiTest);  //네이버 api를 테스트하기 위한 버튼
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

    private void sendMessage(String barcodeNumber, int requestCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(barcodeNumber);
        builder.setTitle("Scanning Result");

        builder.setNeutralButton("상품검색", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                GoodsInfoActivity goodsInfoActivity = new GoodsInfoActivity();
                String prodName;

                prodName = goodsInfoActivity.inquireGoodsInfo(barcodeNumber);

                Intent intent = new Intent(getApplicationContext(), NaverApiActivity.class);
                intent.putExtra("barcodeNumber", barcodeNumber);
                intent.putExtra("prodName", prodName);
                startActivity(intent);
            }
        });

        if(requestCode == GET_GALLERY_IMAGE) {
            builder.setPositiveButton("scan again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
        }
        else if(requestCode == CAMERA_SCAN){
            builder.setPositiveButton("scan again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) { scanCode(); }
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult (int requestCode , int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //barcode_scan via gallery
        if(requestCode == GET_GALLERY_IMAGE){
            if(data == null || data.getData()==null) {
                Log.e("TAG", "The uri is null, probably the user cancelled the image selection process using the back button.");
                return;
            }
            Uri uri = data.getData();

            try
            {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                if (bitmap == null)
                {
                    Log.e("TAG", "uri is not a bitmap," + uri.toString());
                    Toast.makeText(getApplicationContext(), "잘못된 이미지입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                int width = bitmap.getWidth(), height = bitmap.getHeight();
                int[] pixels = new int[width * height];

                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                bitmap.recycle();
                bitmap = null;

                RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
                MultiFormatReader reader = new MultiFormatReader();

                try
                {
                    Result result = reader.decode(bBitmap);
                    sendMessage(result.getText(), requestCode);
                }
                catch (NotFoundException e)
                {
                    Log.e("TAG", "decode exception", e);
                    Toast.makeText(getApplicationContext(), "바코드를 인식하지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            catch (FileNotFoundException e)
            {
                Log.e("TAG", "can not open file" + uri.toString(), e);
                Toast.makeText(getApplicationContext(), "파일을 열 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        //barcode_scan via camera
        else if(requestCode == CAMERA_SCAN){
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {
                    sendMessage(result.getContents(), requestCode);
                }
            }
        }
    }

}