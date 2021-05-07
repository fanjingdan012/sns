package com.fjd.twitter;

import com.fjd.cli.SocialCmd;
import com.fjd.pinterest.PinterestClientServiceImpl;
import com.fjd.social.BareBonesBrowserLaunch;
import com.fjd.social.CmtClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TwitterCmd implements SocialCmd {

    private static String appId = "";
    private static String appSecret = "";
    private static String redirectUri = "";
    private static String proxyHost = "";
    private static String proxyPort = "";

    @Override
    public String validateAccessToken(String accessTokenAndSecret) throws IOException {
        TwitterClientServiceImpl wcs = new TwitterClientServiceImpl();
        String[] accessTokenAndSecret1=accessTokenAndSecret.split("#");
        String result = wcs.searchUsers("a",0, accessTokenAndSecret1[0],accessTokenAndSecret1[1]);
        System.out.println("is working:" + result);
        return result;
    }

    @Override
    public String getAccessToken(String code) {
        try {
            TwitterClientServiceImpl pcs = new TwitterClientServiceImpl();
            String[] accessTokenAndSecret1=code.split("#");
            Map<String,Object > accessTokenMp = pcs.getAccessToken(accessTokenAndSecret1[0],accessTokenAndSecret1[1]);
            String accessToken = (String)accessTokenMp.get("token");
            String accessTokenSecret = (String)accessTokenMp.get("token_secret");
            // Date time = new Date(expireTime);
            System.out.println("accessToken:" + accessToken);
            System.out.println("accessTokenSecret:" + accessTokenSecret);
            return accessToken+"#"+accessTokenSecret;
        } catch (Exception e) {
            System.out.println("Excpetion" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public TwitterCmd(CmtClient cfgBundle) {
        appId = cfgBundle.getGlobalSettings("Global.Twitter.ConsumerKey");
        appSecret = cfgBundle.getGlobalSettings("Global.Twitter.ConsumerSecret");
        redirectUri = cfgBundle.getGlobalSettings("Global.Twitter.redirectUrl");
        proxyHost = cfgBundle.getGlobalSettings("proxyHost");
        proxyPort = cfgBundle.getGlobalSettings("proxyPort");
    }

    @Override
    public void getCode() {
        TwitterClientServiceImpl pcs = new TwitterClientServiceImpl();
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