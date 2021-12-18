package com.SE_project.barcode_scanner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;//
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;

public class GoodsInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        final String barcodeNumber = intent.getStringExtra("barcodeNumber");  //MainActivity에서 barcodeNumber 전달받음


        String URL = "http://www.koreannet.or.kr/home/hpisSrchGtin.gs1?gtin=";
        URL += barcodeNumber;
        final String requestURL =  URL;         //크롤링할 주소를 설정

        final Handler handler = new Handler(Looper.getMainLooper()) {     //받아온 상품정보를 화면에 반영
            @Override
            public void handleMessage(@NonNull Message msg) {
                Bundle bundle = msg.getData();    //new Thread에서 상품정보가 담긴 bundle 추출
                String prodName = bundle.getString("prodName");     // bundle에서 상품정보 추출

                Intent intent = new Intent(getApplicationContext(),NaverApiActivity.class);
                intent.putExtra("barcodeNumber",barcodeNumber); //넘겨줄 데이터
                intent.putExtra("prodName",prodName); // 넘겨줄 데이터
                startActivity(intent); //NaverApiActivity 실행
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
    }
}



