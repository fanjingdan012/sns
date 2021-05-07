package com.fjd.facebook;

import org.junit.Test;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;

public class FacebookClientServiceImplTest {
	private static String appId = "";
	private static String appSecret = "";
	private static String accessToken = "";
	private static String redirectUri = "";
	private static String proxyHost = "";
	private static String proxyPort = "";
	
	private static String veryVeryVeryGoodBooksPageAccessToken = "";
	@Test
	public void testApi(){
		FacebookClientServiceImpl fbcs = new FacebookClientServiceImpl();
		Facebook facebook = new FacebookTemplate(veryVeryVeryGoodBooksPageAccessToken);
		FacebookClientServiceImpl.addProxy(proxyHost, proxyPort, null);
	    fbcs.getPosts(facebook);
//fbcs.getPageAccounts(facebook);
	}
}
