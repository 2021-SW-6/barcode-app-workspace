package com.SE_project.barcode_scanner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
<<<<<<< HEAD
    TextView tv_lowest_price;   //최저가 나중에 정보읽어와서 넣어주어야하는부분
=======
>>>>>>> c26b5cd92f9de0963d0981ca871e8e1d0a137834
    //NaverApiHelper naver_api_helper;          =>기능개발 완료되면 customize
    //public static StringBuilder strBuilder;   =>기능개발 완료되면 customize
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_info);
        setTitle("상품 정보");
        Log.v("onCreate","onCreate func");

        tvBarcodeNumber = findViewById(R.id.tvBarcodeNumber);   //텍스트뷰 연결
        tvProductName = findViewById(R.id.tvProductName);

        Intent intent = getIntent();
<<<<<<< HEAD
        String barcodeNumber = intent.getStringExtra("barcodeNumber");
        tvBarcodeNumber = findViewById(R.id.tvBarcodeNumber);
        tvBarcodeNumber.setText(barcodeNumber);
        String URL = "http://www.koreannet.or.kr/home/hpisSrchGtin.gs1?gtin=";
        URL += barcodeNumber;
        final String requestURL =  URL;
        new Thread() {
            @Override
            public void run() {
                Log.v("Thread run() " , "Thread run함수안");
                Bundle bundle = new Bundle();
                boolean isEmpty;
                try {
                    Log.v("Try " , "try 구문시작");
                    Document doc = Jsoup.connect(requestURL).get();
                    Log.v("Document doc 아래", "Document doc 아래");
                    Elements prodInfo = doc.select(".productTit");
                    Log.v("Elements prodInfo 아래", "Elements prodInfo 아래");
                    isEmpty = prodInfo.isEmpty(); //빼온 값 null체크
                    Log.d("Tag", "isNull? : " + isEmpty); //로그캣 출력

                    if(isEmpty == false) { //null값이 아니면 크롤링 실행
                        //String prodName = prodInfo.get(15).text().substring(0); //크롤링 하면 "현재온도30'c" 이런식으로 뽑아와지기 때문에, 현재온도를 잘라내고 30'c만 뽑아내는 코드
                        bundle.putString("prodName", prodInfo.toString()); //prodName에 prodInfo.toString() 찍어줌=> 8801062522064 &nbsp;&nbsp;&nbsp;&nbsp; 롯데 빠다코코낫 78G
                        Log.v("prodInfo.toString()" ,prodInfo.toString());

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Bundle bundle = msg.getData();    //new Thread에서 작업한 결과물 받기
                tvProductName = findViewById(R.id.tvProductName);
                tvProductName.setText(bundle.getString("prodName"));    //받아온 데이터 textView에 출력
            };
        };
=======
        String barcodeNumber = intent.getStringExtra("barcodeNumber");  //MainActivity에서 barcodeNumber 전달받음
        tvBarcodeNumber.setText("바코드 번호 : " + barcodeNumber);

        String URL = "http://www.koreannet.or.kr/home/hpisSrchGtin.gs1?gtin=";
        URL += barcodeNumber;
        final String requestURL =  URL;         //크롤링할 주소를 설정

        final Handler handler = new Handler(Looper.getMainLooper()) {     //받아온 상품정보를 화면에 반영
            @Override
            public void handleMessage(@NonNull Message msg) {
                Bundle bundle = msg.getData();    //new Thread에서 상품정보가 담긴 bundle 추출
                String prodName = bundle.getString("prodName");     // bundle에서 상품정보 추출
                tvProductName.setText("상품명 : " + prodName);    //받아온 상품정보 textView에 출력
            };
        };

        new Thread() {  //크롤링을 진행하기 위한 쓰레드 생성
            @Override
            public void run() {
                Bundle bundle = new Bundle();   //상품 정보를 담기 위한 bundle
                String prodName;
                boolean isEmpty;    //상품 정보를 잘 읽어왔는 지 확인하기 위한 플래그

                try {
                    Document doc = Jsoup.connect(requestURL).get();  //크롤링할 주소의 html가져옴
                    Elements prodInfo = doc.select(".productTit");  //html에서 .productTit 클래스의 내용 선택 (바코드 번호 + 공백 + 상품 이름)

                    isEmpty = prodInfo.isEmpty();       //상품 정보를 제대로 읽어왔는지 확인
                    Log.d("Tag", "isEmpty? : " + isEmpty);       //로그캣

                    if(isEmpty == false) {      //뽑아온 값이 null이 아니면 크롤링 실행
                        prodName = prodInfo.get(0).text().substring(14);     //크롤링 했을 때 뽑아온 텍스트의 필요한 부분만 잘라냄

                        Message msg = new Message();    //핸들러로 정보를 넘겨주기 위해 선언한 Message
                        bundle.putString("prodName", prodName);     //bundle에 상품정보를 담음 (Key : prdoName)

                        msg.setData(bundle);
                        handler.sendMessage(msg);   //handler로 상품정보가 담긴 bundle 전달
                    }

                    else {      //뽑아온 값이 null이면 등록되지 않은 바코드 번호임을 알림
                        GoodsInfoActivity.this.runOnUiThread(new Runnable() {   //스레드에서 토스트 메시지를 띄우기 위해 runOnUiThread메소드 호출
                            @Override
                            public void run() {
                                Toast.makeText(GoodsInfoActivity.this, "코리안넷에 등록되지 않은 바코드 번호입니다.", Toast.LENGTH_SHORT).show();
                                finish();   //메인 액티비티로 복귀
                            }
                        });

                    }

                } catch (IOException e) {       //예외 처리
                    e.printStackTrace();
                }
            }
        }.start();
>>>>>>> c26b5cd92f9de0963d0981ca871e8e1d0a137834
    }
}