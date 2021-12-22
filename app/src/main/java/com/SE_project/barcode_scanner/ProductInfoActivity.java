package com.SE_project.barcode_scanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductInfoActivity extends AppCompatActivity {
    NaverApiHelper naver_api_helper;
    public static StringBuilder strBuilder;

    TextView tvBarcodeNumber;
    TextView tvProductName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        setTitle("상품 조회");

        tvBarcodeNumber = findViewById(R.id.tvBarcodeNumber);
        tvProductName = findViewById(R.id.tvProductName);

        Intent intent = getIntent();
        String barcodeNumber = intent.getStringExtra("barcodeNumber");      //바코드 인식넘버 넘겨받기
        String prodName = intent.getStringExtra("prodName");                //Koreannet에서 Crawling한 상품명 넘겨받기

        tvBarcodeNumber.setText("바코드 번호 : " + barcodeNumber);
        tvProductName.setText("상품명 : " + prodName);

        naver_api_helper = NaverApiHelper.getInstance();                        //Naver Open Api 호출출 위해 싱글톤 객체 불러오기

       //sort parameter주면 상품명에 대한 정확도가 떨어지는 것 확인=>안드로이드단에서 정렬하도록 수정
        try {
            System.out.println("try안");
            String productName = URLEncoder.encode(prodName, "utf-8");
            String apiURL = naver_api_helper.getBasicUrl() +
                    naver_api_helper.getQuery() + productName +
                    naver_api_helper.getDisplay() +
                    naver_api_helper.getStart();
                    //naver_api_helper.getSort();   //정렬하면 검색 성능이 현저히 떨어져서 제거

            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");                                                            //Get방식
            con.setRequestProperty("X-Naver-Client-Id", naver_api_helper.getClientId());            //Naver Open Api를 사용하기 위한 Id setting
            con.setRequestProperty("X-Naver-Client-Secret", naver_api_helper.getClientSecret());    //Naver Open Api를 사용하기 위한 Password setting
            int responseCode = con.getResponseCode();

            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            strBuilder = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                strBuilder.append(line + "\n");
            }
            br.close();
            con.disconnect();

            /* ----- 읽어온 내용 Json화 -----*/
            JSONObject jsonObject = new JSONObject(strBuilder.toString());
            JSONArray getArray = (JSONArray) jsonObject.get("items");

            /* 가격으로 Sort (Parameter로 sort asc를 하면 상품명의 정확도가 떨어지게 되므로
            따로 정렬처리하기위함*/
            JSONArray sortedJsonArray = new JSONArray();
            List<JSONObject> jsonValues = new ArrayList<JSONObject>();

            for(int i=0; i<getArray.length(); i++) {
                jsonValues.add(getArray.getJSONObject(i));
            }

            Collections.sort(jsonValues, new Comparator<JSONObject>() {             //Collection 자료형 정렬 기능 사용
                private static final String KEY_NAME = "lprice";                    // Json 정렬 기준=> lprice 가격으로

                @Override
                public int compare(JSONObject a, JSONObject b) {                    //Sort Rule compare Override하여 정의
                    Integer valA = 0;
                    Integer valB = 0;
                    try{
                        valA = Integer.parseInt((String)(a.get(KEY_NAME)));         //  Json Object a의 lprice Integer로 casting
                        valB = Integer.parseInt((String)(b.get(KEY_NAME)));         // Json Object b의 lprice Integer로 casting
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return valA.compareTo(valB);                                    // a의 price > b의 price : 1 , 오름차순정렬
                }
            });

            for(int i=0; i<getArray.length(); i++) {
                sortedJsonArray.put(jsonValues.get(i));
            }

            /* Sorted된 sortedJsonArray를 TableRow로 추가해줌.*/
            for (int i = 0; i < getArray.length(); i++) {
                JSONObject object = (JSONObject) sortedJsonArray.get(i);
                addTableRow(object ,this);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void addTableRow(JSONObject jsonObject , Context context ) throws JSONException {
        String getTitle = (String) jsonObject.get("title");                                 //json에서 불러온 title
        String getPrice = (String) jsonObject.get("lprice");                                //json에서 불러온 lprice
        String getLink = (String) jsonObject.get("link");                                   //json에서 불러온 구매링크
        Log.v("getLink",getLink);
        //상품명에 포함된 html 요소들을 제거하기위해 replace함수적용
        String titleFilter = getTitle.replaceAll("<b>", "");
        String title = titleFilter.replaceAll("</b>", "");

        //상품명의 길이조정
        if(title.length()>20) {
            String strnew = title.substring(0,20);
            title = strnew.trim() + "...";
        }
        //mobile 버전으로 바꾸기 위해 replace함수적용으로 url link내용 변경
        String linkFilter = getLink.replaceFirst("search", "msearch");
        String link = linkFilter.replaceAll("gate.nhn[?]id=","product/");

        TableLayout tableLayout = (TableLayout)findViewById(R.id.myTableLayout);              //json의 내용들을 TableLayout으로 보여주기위함

        TableRow tableRow = new TableRow(context);                                              //addTableRow 될때마다 Table의 Row 한줄씩추가
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextView tvName = new TextView(context);    //상품명 텍스트 설정
        tvName.setText(title);
        tvName.setGravity(Gravity.CENTER);
        tvName.setTextColor(Color.BLACK);
        tableRow.addView(tvName);

        TextView tvPrice = new TextView(context);   //상품가격 텍스트 설정
        tvPrice.setText(getPrice);
        tvPrice.setGravity(Gravity.CENTER);
        tvPrice.setTextColor(Color.BLACK);
        tableRow.addView(tvPrice);

        Button btnLink = new Button(context);       //쇼핑 링크 버튼 연결
        btnLink.setText("이동");
        btnLink.setGravity(Gravity.CENTER);

        btnLink.setOnClickListener(new View.OnClickListener() {     // 쇼핑 링크 버튼 리스너
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);
            }
        });
        tableRow.addView(btnLink);

        tableLayout.addView(tableRow);
    }
//.
}
