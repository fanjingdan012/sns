package com.fjd.facebook;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;

import com.fjd.cli.SocialCmd;
import com.fjd.social.CmtClient;

public class FacebookCmd implements SocialCmd {
	private static String appId = "";
	private static String appSecret = "";
	private static String redirectUri = "";
	private static String proxyHost = "";
	private static String proxyPort = "";

	@Override
	public String validateAccessToken(String accessToken) throws IOException {
		FacebookClientServiceImpl fbcs = new FacebookClientServiceImpl();
		Facebook facebook = new FacebookTemplate(accessToken);
		FacebookClientServiceImpl.addProxy(proxyHost, proxyPort, null);
		String status = fbcs.searchUsersWork(facebook,"a",1);
		//List<String> urls = new ArrayList<>();
		//urls.add("479408025754478");

		//String status = fbcs.uploadImageToAlbum(facebook, "a http://www.sap.com",true,"http://img.frbiz.com/nimg/6f/43/ce50bfdc66a9d14443349691884e-0x0-1/orangutan_3d_oil_painting_picture.jpg");
		//String status = fbcs.postToFacebookWithImage(facebook, "content", "www.sap.com",urls,1L);

		System.out.println("is working:" + status);
		return status;
	}

	@Override
	public String getAccessToken(String code) {
		try {
			FacebookOauthServiceImpl fbos = new FacebookOauthServiceImpl();
			Map<String, Object> result = fbos.getAccessToken(appId, appSecret, proxyHost, proxyPort, redirectUri, code);
			String accessToken = (String) result.get("access_token");
			String refreshToken = (String) result.get("refresh_token");
			Long expireTime = (Long) result.get("expire_time");
			// Date time = new Date(expireTime);
			System.out.println("accessToken:" + accessToken);
			System.out.println("refreshToken:" + refreshToken);
			System.out.println("expireTime:" + expireTime);
			return accessToken;
		} catch (Exception e) {
			System.out.println("Excpetion" + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void getCode() {
		FacebookOauthServiceImpl fbos = new FacebookOauthServiceImpl();
		String url = fbos.getAuthorizationUrl(appId, appSecret, redirectUri);
		try {
			java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
			// BareBonesBrowserLaunch.openURL(url);
		} catch (Exception e) {
			System.out.println("getCode exception");
		}
		// BareBonesBrowserLaunch.openURL(fbos.getAuthorizationUrl());

	}

	public FacebookCmd(CmtClient cfg) {
		appId = cfg.getGlobalSettings("Global.Facebook.AppId");
		appSecret = cfg.getGlobalSettings("Global.Facebook.AppSecret");
		redirectUri = cfg.getGlobalSettings("Global.Facebook.RedirectUri");
		proxyHost = cfg.getGlobalSettings("proxyHost");
		proxyPort = cfg.getGlobalSettings("proxyPort");
	}

}
