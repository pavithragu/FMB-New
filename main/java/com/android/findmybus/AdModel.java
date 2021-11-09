package com.android.findmybus;

public class AdModel {

    private String adImageUrl, adLinkUrl;

    public AdModel() {
    }

    public AdModel(String adImageUrl, String adLinkUrl) {
        this.adImageUrl = adImageUrl;
        this.adLinkUrl = adLinkUrl;
    }

    public String getAdImageUrl() {
        return adImageUrl;
    }

    public String getAdLinkUrl() {
        return adLinkUrl;
    }

}
