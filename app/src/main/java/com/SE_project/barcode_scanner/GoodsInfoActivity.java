package com.SE_project.barcode_scanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class GoodsInfoActivity extends AppCompatActivity {
    TextView tv_barcode_number; //인식한 내용 출력되는 것 확인
    TextView tv_product_name;   //상품명 나중에 정보읽어와서 넣어주어야하는부분
    TextView tv_lowest_price;   //최저가 나중에 정보읽어와서 넣어주어야하는부분
    //NaverApiHelper naver_api_helper;          =>기능개발 완료되면 customize
    //public static StringBuilder strBuilder;   =>기능개발 완료되면 customize
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_info);
        setTitle("상품 정보");

        Intent intent = getIntent();
        String barcodeNumber = intent.getStringExtra("barcodeNumber");
        tv_barcode_number = findViewById(R.id.barcodeNumber);
        tv_barcode_number.setText(barcodeNumber);

    }
}
