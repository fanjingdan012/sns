package com.fjd.mailchimp;

import com.fjd.cli.SocialCmd;
import com.fjd.instagram.InstagramClientServiceImpl;
import com.fjd.mailchimp.MailchimpClientService;
import com.fjd.pinterest.PinterestClientServiceImpl;
import com.fjd.social.BareBonesBrowserLaunch;
import com.fjd.social.CmtClient;
import com.fjd.wechat.WeChatClientServiceImpl;

import java.io.IOException;


public class MailchimpCmd implements SocialCmd {
    // static String accessToken =
    // "CAAMqW3yZA0CQBAGMPQ2q0nbi5gFdH1XTmbRqIOvmQW5j6uU7OSkZCV59FRVokPJbe95nZAWxfiOIciTXxyvLzDeAtug41XMKmUSxZBewl7QvlsxP0IlZCbAHV0iomVyGknUIoaSpLexmiImEr4JsyFmF14M6EQQfif4ZChxAtgThZCyD4RwMhCjMQd5weGkvvUZD";

    private static String appId = "";
    private static String appSecret = "";
    private static String redirectUri = "";
    private static String proxyHost = "";
    private static String proxyPort = "";



    @Override
    public String validateAccessToken(String accessToken) throws IOException {
        MailchimpClientService mcs = new MailchimpClientService();
        String status = mcs.listsRead("us9", accessToken);
        System.out.println("is working:" + status);
        return status;
    }

    @Override
    public String getAccessToken(String code) {
        try {
            MailchimpClientService pcs = new MailchimpClientService();
            String accessToken = pcs.getToken(code);
            // Date time = new Date(expireTime);
            System.out.println("accessToken:" + accessToken);
            return accessToken;
        } catch (Exception e) {
            System.out.println("Excpetion" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public MailchimpCmd(CmtClient cfgBundle) {
        appId = cfgBundle.getGlobalSettings("Global.WeChat.AppId");
        appSecret = cfgBundle.getGlobalSettings("Global.WeChat.AppSecret");
        redirectUri = cfgBundle.getGlobalSettings("Global.WeChat.redirectUrl");
        proxyHost = cfgBundle.getGlobalSettings("proxyHost");
        proxyPort = cfgBundle.getGlobalSettings("proxyPort");
    }

    @Override
    public void getCode() {

        MailchimpClientService cs = new MailchimpClientService();
        String url =cs.getAuthorizationUrl();
        try {
            // java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
            BareBonesBrowserLaunch.openURL(url);
        } catch (Exception e) {
            System.out.println("getCode exception");
        }
        // BareBonesBrowserLaunch.openURL(fbos.getAuthorizationUrl());

    }
}