package com.fjd.wechat;

import com.fjd.cli.SocialCmd;
import com.fjd.pinterest.PinterestClientServiceImpl;
import com.fjd.social.BareBonesBrowserLaunch;
import com.fjd.social.CmtClient;
import com.fjd.wechat.WeChatClientServiceImpl;

import java.io.IOException;


public class WeChatCmd implements SocialCmd {
    private static String appId = "";
    private static String appSecret = "";
    private static String redirectUri = "";
    private static String proxyHost = "";
    private static String proxyPort = "";



    @Override
    public String validateAccessToken(String accessToken) throws IOException {
        WeChatClientServiceImpl wcs = new WeChatClientServiceImpl();
        String status = "no function now";//wcs.searchUserWork("a", accessToken);
        System.out.println("is working:" + status);
        return status;
    }

    @Override
    public String getAccessToken(String code) {
        try {
            WeChatClientServiceImpl pcs = new WeChatClientServiceImpl();
            String accessToken = pcs.getAccessToken(appId,appSecret);
            // Date time = new Date(expireTime);
            System.out.println("accessToken:" + accessToken);
            return accessToken;
        } catch (Exception e) {
            System.out.println("Excpetion" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public WeChatCmd(CmtClient cfgBundle) {
        appId = cfgBundle.getGlobalSettings("Global.WeChat.AppId");
        appSecret = cfgBundle.getGlobalSettings("Global.WeChat.AppSecret");
        redirectUri = cfgBundle.getGlobalSettings("Global.WeChat.redirectUrl");
        proxyHost = cfgBundle.getGlobalSettings("proxyHost");
        proxyPort = cfgBundle.getGlobalSettings("proxyPort");
    }

    @Override
    public void getCode() {


    }
}