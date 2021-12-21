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
    private final int CAMERA_SCAN = 49374;      //request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("바코디");

        ImageView ivCamera = findViewById(R.id.ivCamera);
        ImageView ivGallery = findViewById(R.id.ivGallery);

        ivCamera.setOnClickListener(new View.OnClickListener() {    //카메라 이미지를 눌렀을 때 작동할 기능
            @Override
            public void onClick(View view) {
                scanCodeWithCamera();
            }   //카메라로 바코드 스캔
        });

        ivGallery.setOnClickListener(new View.OnClickListener() {   //갤러리 이미지를 눌렀을 때 작동할 기능
            @Override
            public void onClick(View view) { scanCodeWithGallery(); }   //갤러리에 있는 이미지에서 바코드 스캔
        });
    }

    /**
     * 카메라로 바코드를 인식하기 위한 method
     */
    private void scanCodeWithCamera() {
        IntentIntegrator integrator = new IntentIntegrator (this);
        integrator.setCaptureActivity(CaptureAct.class);        //카메라로 바코드를 인식하기 위한 클래스 지정
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);   //모든 종류의 바코드를 인식 가능하도록 지정
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }

    /**
     * 갤러리의 사진에서 바코드를 인식하기 위한 method
     */
    private void scanCodeWithGallery(){
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickIntent, GET_GALLERY_IMAGE);      //사진을 가져오기 위해 갤러리 열기
    }

    /**
     * 상품검색을 진행할지 다시 스캔할지 묻는 Dialog를 띄우는 method
     * @param barcodeNumber
     * @param requestCode
     */
    private void sendMessage(String barcodeNumber, int requestCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(barcodeNumber);
        builder.setTitle("Barcode Scan Result");

        builder.setNeutralButton("상품 검색", new DialogInterface.OnClickListener() {   //상품 검색 선택
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CrawlingProductName crawlingProductName = CrawlingProductName.getInstance();    //Design Pattern(Creational) : Singleton Pattern
                String prodName;

                prodName = crawlingProductName.inquireGoodsInfo(barcodeNumber);   //바코드 번호로 상품명을 조회

                if(prodName != null) {      //상품명 조회에 성공한 경우
                    Intent prodInfoIntent = new Intent(getApplicationContext(), ProductInfoActivity.class);
                    prodInfoIntent.putExtra("barcodeNumber", barcodeNumber);
                    prodInfoIntent.putExtra("prodName", prodName);              //intent에 상품정보 싣기
                    startActivity(prodInfoIntent);      //상품정보화면으로 이동
                }
                else        //상품명이 조회되지 않으면 (ex. 코리안넷에 등록되지 않은 경우)
                    Toast.makeText(getApplicationContext(), "코리안넷에 등록되지 않은 상품입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        if(requestCode == GET_GALLERY_IMAGE) {      //scanCodeWithGallery()를 진행했던 경우
            builder.setPositiveButton("scan again", new DialogInterface.OnClickListener() {     //재스캔을 원하면
                @Override
                public void onClick(DialogInterface dialogInterface, int i) { scanCodeWithGallery(); }  //갤러리 화면으로 복귀
            });
        }
        else if(requestCode == CAMERA_SCAN){    //scanCodeWithCamera()를 진행했던 경우
            builder.setPositiveButton("scan again", new DialogInterface.OnClickListener() {     //재스캔을 원하면
                @Override
                public void onClick(DialogInterface dialogInterface, int i) { scanCodeWithCamera(); }   //카메라 스캔 화면으로 복귀
            });
        }

        AlertDialog dialog = builder.create();
        dialog.show();      //Show dialog
    }

    /**
     * 갤러리 및 카메라에서 얻은 결과를 돌려주는 method
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult (int requestCode , int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //barcode_scan via gallery  * Reference : https://stackoverflow.com/questions/29649673/scan-barcode-from-an-image-in-gallery-android
        if(requestCode == GET_GALLERY_IMAGE){
            if(data == null || data.getData()==null) {      //데이터가 null인 경우 (유저가 이미지 선택 프로세스를 취소했을 가능성 농후)
                Log.e("TAG", "The uri is null, probably the user cancelled the image selection process using the back button.");    //에러 로그 출력
                return;
            }
            Uri uri = data.getData();   //데이터를 Uri로

            try
            {
                InputStream inputStream = getContentResolver().openInputStream(uri);    //uri를 통해 이미지를 받음
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);    //이미지를 비트맵으로 변환

                if (bitmap == null)
                {
                    Log.e("TAG", "uri is not a bitmap," + uri.toString());      //에러 로그 출력 : 비트맵이 uri가 아님
                    Toast.makeText(getApplicationContext(), "잘못된 이미지입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                int width = bitmap.getWidth(), height = bitmap.getHeight();
                int[] pixels = new int[width * height];

                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                bitmap.recycle();       //메모리 누수 방지
                bitmap = null;          //메모리 누수 방지

                RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
                MultiFormatReader reader = new MultiFormatReader();

                try
                {
                    Result result = reader.decode(bBitmap);     //바코드 인식
                    sendMessage(result.getText(), requestCode);     //바코드 인식 결과 (바코드번호)를 얻고 Dialog 띄움
                }
                catch (NotFoundException e)     //바코드 인식 실패
                {
                    Toast.makeText(getApplicationContext(), "바코드를 인식하지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            catch (FileNotFoundException e)
            {
                Toast.makeText(getApplicationContext(), "파일을 열 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        //barcode_scan via camera
        else if(requestCode == CAMERA_SCAN){
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {     //정상적으로 인식되면
                    sendMessage(result.getContents(), requestCode);     //바코드 인식 결과 (바코드번호)를 얻고 Dialog 띄움
                }
            }
        }
    }
}