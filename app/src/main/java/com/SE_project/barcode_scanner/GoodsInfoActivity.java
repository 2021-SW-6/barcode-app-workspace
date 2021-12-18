package com.SE_project.barcode_scanner;

import android.os.AsyncTask;//
import android.text.method.ScrollingMovementMethod;//



import android.content.Intent;
import android.os.Bundle; //
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;//

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class GoodsInfoActivity extends AppCompatActivity {
    TextView tvBarcodeNumber; //인식한 내용 출력되는 것 확인
    TextView tvProductName;   //상품명 나중에 정보읽어와서 넣어주어야하는부분
    TextView tv_lowest_price;   //최저가 나중에 정보읽어와서 넣어주어야하는부분
    NaverApiHelper naver_api_helper;         // =>기능개발 완료되면 customize
    public static StringBuilder strBuilder;   //=>기능개발 완료되면 customize
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_info);
        setTitle("상품 정보");


        Intent intent = getIntent();
        String barcodeNumber = intent.getStringExtra("barcodeNumber");
        tvBarcodeNumber = findViewById(R.id.barcodeNumber);
        tvBarcodeNumber.setText(barcodeNumber);

        new Thread() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                boolean isEmpty;
                try {
                    Document doc = Jsoup.connect("http://www.koreannet.or.kr/home/hpisSrchGtin.gs1?gtin=8801062522064").get();
                    Elements prodInfo = doc.select(".productTit");

                    isEmpty = prodInfo.isEmpty(); //빼온 값 null체크
                    Log.d("Tag", "isNull? : " + isEmpty); //로그캣 출력


                    if(isEmpty == false) { //null값이 아니면 크롤링 실행 38
                        String prodName = prodInfo.get(0).text().substring(38); //크롤링 하면 "현재온도30'c" 이런식으로 뽑아와지기 때문에, 현재온도를 잘라내고 30'c만 뽑아내는 코드
                        bundle.putString("prodName", prodName); //bundle 이라는 자료형에 뽑아낸 결과값 담아서 main Thread로 보내기
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg)
            {
                Bundle bundle = msg.getData();    //new Thread에서 작업한 결과물 받기
                tvProductName = findViewById(R.id.tvProductName);
                tvProductName.setText(bundle.getString("prodName"));    //받아온 데이터 textView에 출력
            };
        };

    }


}

//