package com.fjd.weibo;

import com.fjd.cli.SocialCmd;
import com.fjd.pinterest.PinterestClientServiceImpl;
import com.fjd.social.BareBonesBrowserLaunch;
import com.fjd.social.CmtClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WeiboCmd implements SocialCmd {

    private static String appId = "";
    private static String appSecret = "";
    private static String redirectUri = "";
    private static String proxyHost = "";
    private static String proxyPort = "";

    @Override
    public String validateAccessToken(String accessToken) throws IOException {
        WeiboClientServiceImpl wcs = new WeiboClientServiceImpl();
        List<String> result = wcs.searchUsers("a", accessToken);
        String status = result==null?"null":result.toString();
        System.out.println("is working:" + status);
        return status;
    }

    @Override
    public String getAccessToken(String code) {
        try {
            WeiboClientServiceImpl pcs = new WeiboClientServiceImpl();
            Map<String,Object > accessTokenMp = pcs.getAccessToken(code);
            String accessToken = (String)accessTokenMp.get("access_token");
            // Date time = new Date(expireTime);
            System.out.println("accessToken:" + accessToken);
            return accessToken;
        } catch (Exception e) {
            System.out.println("Excpetion" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public WeiboCmd(CmtClient cfgBundle) {
        appId = cfgBundle.getGlobalSettings("Global.Weibo.ClientId");
        appSecret = cfgBundle.getGlobalSettings("Global.Weibo.ClientSecret");
        redirectUri = cfgBundle.getGlobalSettings("Global.Weibo.redirectUrl");
        proxyHost = cfgBundle.getGlobalSettings("proxyHost");
        proxyPort = cfgBundle.getGlobalSettings("proxyPort");
    }

    @Override
    public void getCode() {
        WeiboClientServiceImpl pcs = new WeiboClientServiceImpl();
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