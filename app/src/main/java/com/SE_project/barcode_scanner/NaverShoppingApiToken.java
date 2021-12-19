package com.SE_project.barcode_scanner;
//github에 api key들이 push되지 않기위해 가려지는 내용들
public class NaverShoppingApiToken {

    public static NaverShoppingApiToken token;
    private static final String clientId = "";      //"" 상태로 git update-index --assume-unchanged로 =>github에 push 되지않도록함
    private static final String clientSecret = "";            //"" 상태로 git update-index --assume-unchanged => github에 push 되지않도록함

    //Design Pattern(Creational) : Singleton Pattern
    public static NaverShoppingApiToken getInstance() {
        if(token==null) {
            token = new NaverShoppingApiToken();
        }
        return token;
    }
    protected String getClientId() {
        return this.clientId;
    }
    protected String getClientSecret() {
        return this.clientSecret;
    }
}
