package com.fjd.pinterest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;

import com.fjd.cli.SocialCmd;
import com.fjd.facebook.FacebookClientServiceImpl;
import com.fjd.facebook.FacebookOauthServiceImpl;
import com.fjd.social.BareBonesBrowserLaunch;
import com.fjd.social.CmtClient;

public class PinterestCmd implements SocialCmd {

    private static String appId = "";
    private static String appSecret = "";
    private static String redirectUri = "";
    private static String proxyHost = "";
    private static String proxyPort = "";

    @Override
    public String validateAccessToken(String accessToken) throws IOException {
        PinterestClientServiceImpl pcs = new PinterestClientServiceImpl();
        String status = pcs.searchUserWork("a", accessToken);
        System.out.println("is working:" + status);
        return status;
    }

    @Override
    public String getAccessToken(String code) {
        try {
            PinterestClientServiceImpl pcs = new PinterestClientServiceImpl();
            String accessToken = pcs.getAccessToken(code);
            // Date time = new Date(expireTime);
            System.out.println("accessToken:" + accessToken);
            return accessToken;
        } catch (Exception e) {
            System.out.println("Excpetion" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public PinterestCmd(CmtClient cfgBundle) {
        appId = cfgBundle.getGlobalSettings("Global.Pinterest.AppId");
        appSecret = cfgBundle.getGlobalSettings("Global.Pinterest.AppSecret");
        redirectUri = cfgBundle.getGlobalSettings("Global.Pinterest.redirectUrl");
        proxyHost = cfgBundle.getGlobalSettings("proxyHost");
        proxyPort = cfgBundle.getGlobalSettings("proxyPort");
    }

    @Override
    public void getCode() {
        PinterestClientServiceImpl pcs = new PinterestClientServiceImpl();
        String url = pcs.getAuthorizationUrl();
        try {
            // java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
            BareBonesBrowserLaunch.openURL(url);
        } catch (Exception e) {
            System.out.println("getCode exception");
        }
        // BareBonesBrowserLaunch.openURL(fbos.getAuthorizationUrl());

    }

}