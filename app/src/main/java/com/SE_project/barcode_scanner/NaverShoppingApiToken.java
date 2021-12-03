package com.SE_project.barcode_scanner;
//github에 api key들이 push되지 않기위해 가려지는 내용들
public class NaverShoppingApiToken {
    private static final String clientId = "";      //"" 상태로 최초 push 후 .gitignore로 가림
    private static final String clientSecret = "";  //"" 상태로 최초 push 후 .gitignore로 가림

    protected String getClientId() {
        return this.clientId;
    }
    protected String getClientSecret() {
        return this.clientSecret;
    }
}
