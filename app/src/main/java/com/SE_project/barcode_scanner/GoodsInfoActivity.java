package com.SE_project.barcode_scanner;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;

/**
 * 여기 클래스 수정했습니다
 */

public class GoodsInfoActivity {
    private String prodName;    //상품 이름
    boolean isEmpty;        //상품 정보를 잘 읽어왔는 지 확인하기 위한 플래그

    public GoodsInfoActivity() {
        this.prodName = null;
        this.isEmpty = true;
    }

    public String inquireGoodsInfo (String barcodeNumber){
        Log.d("testlog","2 테스트");
        String requestURL = "http://www.koreannet.or.kr/home/hpisSrchGtin.gs1?gtin=";  //코리안넷 URL
        requestURL += barcodeNumber;   //코리안넷 URL에 바코드 번호를 붙여 상품정보가 담긴 페이지로

        Log.d("testlog","3 테스트");
        try {
            Log.d("testlog","4-1 테스트");
            Document doc = Jsoup.connect(requestURL).get();  //크롤링할 페이지의 html가져옴
            Log.d("testlog","4-2 테스트");
            Elements prodInfo = doc.select(".productTit");  //html에서 .productTit 클래스의 내용 선택 (바코드 번호 + 공백 + 상품 이름)
            Log.d("testlog","4-3 테스트");
            isEmpty = prodInfo.isEmpty();       //상품 정보를 제대로 읽어왔는지 확인

            if(isEmpty == false)        //뽑아온 값이 null이 아니면 크롤링 실행
                prodName = prodInfo.get(0).text().substring(14);     //크롤링 했을 때 뽑아온 텍스트의 필요한 부분만 잘라냄
            Log.d("testlog","5 테스트");
        } catch (IOException e) {       //예외 처리
            e.printStackTrace();
            Log.d("testlog","error 테스트");
        }
        Log.d("testlog","6 테스트");
        return prodName;    //상품정보 반환
    }
}



