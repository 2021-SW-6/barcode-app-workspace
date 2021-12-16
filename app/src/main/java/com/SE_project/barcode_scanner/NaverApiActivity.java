package com.SE_project.barcode_scanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
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

public class NaverApiActivity extends AppCompatActivity {
    NaverApiHelper naver_api_helper;
    public static StringBuilder strBuilder; //
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.naver_api_test);
        System.out.println("NaverApiActivity 테스트");
        Log.d("request: ","NaverApiActivity 테스트");
        naver_api_helper = new NaverApiHelper();
        System.out.println("Url 기져오기" + naver_api_helper.getBasicUrl());

        JSONArray getArray;

        try {
            System.out.println("try안");
            String productName = URLEncoder.encode("롯데 빠다코코낫 78G", "utf-8");
            String apiURL = naver_api_helper.getBasicUrl() +
                    naver_api_helper.getQuery() + productName +
                    naver_api_helper.getDisplay() +
                    naver_api_helper.getStart() +
                    naver_api_helper.getSort();
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());

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
            strBuilder = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                strBuilder.append(line + "\n");
            }
            br.close();
            con.disconnect();
            System.out.println(strBuilder);

            JSONObject jsonObject = new JSONObject(strBuilder.toString());
            getArray = (JSONArray) jsonObject.get("items");
            for (int i = 0; i < getArray.length(); i++) {
                JSONObject object = (JSONObject) getArray.get(i);
                addTableRow(object ,this);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        findViewById(R.id.URIButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://google.com"));
                startActivity(intent);
            }
        });
    }

    public void addTableRow(JSONObject jsonObject , Context context ) throws JSONException {
        String getTitle = (String) jsonObject.get("title");
        String getPrice = (String) jsonObject.get("lprice");
        String getLink = (String) jsonObject.get("link");

        String titleFilter = getTitle.replaceAll("<b>", "");
        String title = titleFilter.replaceAll("</b>", "");

        TableLayout tableLayout = (TableLayout)findViewById(R.id.myTableLayout);

        TableRow tableRow = new TableRow(context);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        TextView tvName = new TextView(context);
        tvName.setText(title);
        tvName.setGravity(Gravity.CENTER);
        tvName.setTextColor(Color.BLACK);
        tableRow.addView(tvName);

        TextView tvPrice = new TextView(context);
        tvPrice.setText(getPrice);
        tvPrice.setGravity(Gravity.CENTER);
        tvPrice.setTextColor(Color.BLACK);
        tableRow.addView(tvPrice);

        Button btnLink = new Button(context);
        btnLink.setText("이동");
        btnLink.setGravity(Gravity.CENTER);
        btnLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getLink));
                startActivity(intent);
            }
        });
        tableRow.addView(btnLink);
        tableLayout.addView(tableRow);
    }

}
