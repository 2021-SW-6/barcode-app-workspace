package com.SE_project.barcode_scanner;

public class NaverApiHelper {
    NaverShoppingApiToken naver_api_token;  //NaverApi clientId,clientSecret key들을 불러오기위함
    protected String clientId;              //X-Naver-Client-Id
    protected String clientSecret;          //X-Naver-Client-Secret
    protected final static String basicUrl = "https://openapi.naver.com/v1/search/shop.json?";
    protected String query = "query=";      // 필수여부(Y)
    protected String display = "&display=6"; // 필수여부(N) 기본값(10) 최대값(100)
    protected String start = "&start=1";     // 필수여부(N) 기본값(1) 최대값(1000)
    protected String sort = "&sort=asc";    // 필수여부(N) 기본값(sim) , date , asc , dsc
    protected String fullUrl;               //Get 방식으로 Request를 보낼 Url

    NaverApiHelper() {
        naver_api_token = new NaverShoppingApiToken();          //NaverApi Key들을 불러오기위한 생성자 생성
        this.clientId=naver_api_token.getClientId();            //Secret file인 NaverShoppingApiToken에서 key값을 가져와 API Key를 github에 가린다.
        this.clientSecret=naver_api_token.getClientSecret();    //Secret file인 NaverShoppingApiToken에서 key값을 가져와 API Key를 github에 가린다.
    }

    /* set Method => query:상품이름 , fullUrl:request 보내기 위한 최종 Url */
    protected void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl; //request 보내는 Url
    }
    protected void setQuery(String query) {
        this.query = query; //상품이름
    }

    /* get Method */
    protected String getClientId() {
        return clientId;
    }
    protected String getClientSecret() {
        return clientSecret;
    }

    protected String getQuery() {
        return query;
    }

    protected String getDisplay() {
        return display;
    }

    protected String getStart() {
        return start;
    }

    protected String getSort() {
        return sort;
    }
    protected String getBasicUrl() {
        return basicUrl;
    }

    protected String getFullUrl() {
        return fullUrl;
    }

}
