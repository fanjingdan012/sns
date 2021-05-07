package com.fjd.facebook;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.stereotype.Service;
import twitter4j.JSONObject;

/**
 * @author I313061
 * 
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FacebookOauthServiceImpl  {

    // @Autowired
    // SldServiceForSU sldService;

    private static String PERMISSIONS = "public_profile,publish_actions,user_posts,manage_pages,publish_pages";
 
    /**
     * Get facebook authorization url
     */
    public String getAuthorizationUrl(String appId,String appSecret,String redirectUri) {
        OAuth2Operations oauthOperations = null;
            FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(appId, appSecret);
            oauthOperations = connectionFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();

        params.setScope(PERMISSIONS);
        params.setRedirectUri(redirectUri);
        String authorizeUrl = oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, params);
        return authorizeUrl;
    }

    public Map<String, Object>  getAccessToken(String appId,String appSecret,String proxyHost,String proxyPort,String redirectUri,String code) throws Exception{
        Map<String, Object> token = new HashMap();
        OAuth2Operations oauthOperations = null;
       
            FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(appId, appSecret);
            oauthOperations = connectionFactory.getOAuthOperations();
            FacebookClientServiceImpl.addProxy(proxyHost,proxyPort,null);
        AccessGrant accessGrant = oauthOperations.exchangeForAccess(code,redirectUri , null);
        if (token != null) {
            token.put("access_token", accessGrant.getAccessToken());
            token.put("refresh_token", accessGrant.getRefreshToken());
            token.put("expire_time", accessGrant.getExpireTime());

            
        }
        return token;
    }

    public String getMe(String accessToken)throws  Exception{
        Facebook facebook = null;
        facebook = new FacebookTemplate(accessToken);
        String jsonResult = facebook.fetchObject("me", String.class, "id", "name");
        System.out.println(jsonResult);
            JSONObject result;
                result = new JSONObject(jsonResult);
                System.out.print(result.get("id")+""+ result.get("name"));
        return jsonResult;
    }




}
