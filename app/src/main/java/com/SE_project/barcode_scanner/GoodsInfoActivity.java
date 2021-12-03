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
    NaverApiHelper naver_api_helper;
    public static StringBuilder sb; //
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_info);
        setTitle("상품 정보");

        Intent intent = getIntent();
        String barcodeNumber = intent.getStringExtra("barcodeNumber");
        tv_barcode_number = findViewById(R.id.barcodeNumber);
        tv_barcode_number.setText(barcodeNumber);
        naver_api_helper = new NaverApiHelper();
        System.out.println("Goods Info 테스트");
        Log.v("GoodsInfoAcitivt: " ,"test");
        try {
            System.out.println("try안");
            String productName = URLEncoder.encode("롯데 빠다코코낫 78G", "utf-8");
            String apiURL = naver_api_helper.getBasicUrl() +
                            naver_api_helper.getQuery() + productName +
                            naver_api_helper.getDisplay() +
                            naver_api_helper.getStart() +
                            naver_api_helper.getSort();

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", naver_api_helper.getClientId());
            con.setRequestProperty("X-Naver-Client-Secret", naver_api_helper.getClientSecret());
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
                Log.d("request: ",line);
            }
            br.close();
            con.disconnect();
            System.out.println(sb);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
