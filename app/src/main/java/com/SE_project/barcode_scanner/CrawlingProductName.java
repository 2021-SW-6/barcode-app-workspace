package com.SE_project.barcode_scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;

public class CrawlingProductName {
    private String prodName = null;    //상품 이름
    boolean isEmpty = true;        //상품 정보를 잘 읽어왔는 지 확인하기 위한 플래그

    public String inquireGoodsInfo (String barcodeNumber){
        String URL = "http://www.koreannet.or.kr/home/hpisSrchGtin.gs1?gtin=";  //코리안넷 URL
        URL += barcodeNumber;   //코리안넷 URL에 바코드 번호를 붙여 상품정보가 담긴 페이지로
        final String requestURL = URL;

        Thread CrawlingThread = new Thread(){       //크롤링 작업을 위한 Thread
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(requestURL).get();  //크롤링할 페이지의 html가져옴
                    Elements prodInfo = doc.select(".productTit");  //html에서 .productTit 클래스의 내용 선택 (바코드 번호 + 공백 + 상품 이름)
                    isEmpty = prodInfo.isEmpty();       //상품 정보를 제대로 읽어왔는지 확인

                    if(isEmpty == false)        //뽑아온 값이 null이 아니면 크롤링 실행
                        prodName = prodInfo.get(0).text().substring(14);     //크롤링 했을 때 뽑아온 텍스트의 필요한 부분만 잘라냄

                } catch (IOException e) {       //예외 처리
                    e.printStackTrace();
                }
            }
        };
        CrawlingThread.start();     //CrawlingThread 시작

        try {
            CrawlingThread.join();      //CrawlingThread의 작업이 완료될 때까지 대기
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        return prodName;    //상품정보 반환
    }
}



