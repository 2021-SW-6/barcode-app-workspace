package com.SE_project.barcode_scanner;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.util.Log;

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
                Log.d("request: ",line);
            }
            br.close();
            con.disconnect();
            System.out.println(strBuilder);

            JSONObject jsonObject = new JSONObject(strBuilder.toString());
            JSONArray getArray = (JSONArray) jsonObject.get("items");
            for (int i = 0; i < getArray.length(); i++) {
                JSONObject object = (JSONObject) getArray.get(i);

                String getTitle = (String) object.get("title");
                String getPrice = (String) object.get("lprice");
                String getLink = (String) object.get("link");

                String titleFilter = getTitle.replaceAll("<b>", "");
                String title = titleFilter.replaceAll("</b>", "");


                Log.v("test", title);
                Log.v("test", getPrice);
                Log.v("test", getLink);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
