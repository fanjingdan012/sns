package com.fjd.facebook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.PatternSyntaxException;

import com.fjd.cli.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.social.ApiException;
import org.springframework.social.DuplicateStatusException;
import org.springframework.social.ExpiredAuthorizationException;
import org.springframework.social.InsufficientPermissionException;
import org.springframework.social.InvalidAuthorizationException;
import org.springframework.social.MissingAuthorizationException;
import org.springframework.social.OperationNotPermittedException;
import org.springframework.social.RateLimitExceededException;
import org.springframework.social.ResourceNotFoundException;
import org.springframework.social.RevokedAuthorizationException;
import org.springframework.social.ServerException;
import org.springframework.social.UncategorizedApiException;
import org.springframework.social.facebook.api.Account;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.PagingParameters;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


/**
 * @author I313061
 * 
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FacebookClientServiceImpl {
    private static Map<String, Facebook> facebookMap = new HashMap<String, Facebook>();

    private static final String LOG_SOCIAL_ACCOUNT_DOESNT_EXIST = "Social Account {} does not exist.";

    private static final int FACEBOOK_USER_NUMBER_EACH_PAGE = 20;
    private static final String MESSAGE_TYPE_POST = "POST";
    private static final String MESSAGE_TYPE_REPLY = "REPLY";
    private static final String MESSAGE_TYPE_REPLY_TO_COMMENT = "REPLY_TO_COMMENT";

    // character "{"
    private static final String LEFT_CURLY_BRACKET = "%7B";
    // character "}"
    private static final String RIGHT_CURLY_BRACKET = "%7D";
    private static final String COMMA = ",";
    private static final String SLASH = "/";
    private static final String PICTURE = "picture";
    private static final String COMMENTS = "comments";
    private static final String TAGGED = "tagged";
    private static final String FETCH_PICTURE = SLASH + PICTURE;
    private static final String FETCH_COMMENTS = SLASH + COMMENTS;
    private static final String FETCH_POSTS_TO_PAGE = SLASH + TAGGED;
    private static final String FETCH_FEED = SLASH + "feed/";
    private static final String FETCH_ME_FEED = "me/feed/";
    private static final String FETCH_LIKES = SLASH + "likes";
    private static final String COMMENT_FIELDS = "created_time,from,message,attachment";
    private static final String FEED_FIELDS = "created_time,from,message,full_picture,comments";
    private static final String QUESTION_MARK_FIELDS = "?fields=";
    private static final String AMPERSAND_FIELDS = "&fields=";
    private static final String QUESTION_MARK_IDS = "?ids=";
    private static final String PARAM_MESSAGE = "message";
    private static final String PARAM_LINK = "link";
    private static final String PARAM_PICTURE = "picture";
    private static final String PARAM_ATTACHMENT_URL = "attachment_url";
    private static final String PARAM_DATA = "data";
    private static final String PARAM_WEBSITE = "website";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_ID = "id";
    private static final String PARAM_ABOUT = "about";
    private static final String PARAM_URL = "url";
    private static final String PARAM_Q = "q";
    private static final String PARAM_LIMIT = "limit";
    private static final String PARAM_OFFSET = "offset";
    private static final String PARAM_SHARES = "shares";
    private static final String PARAM_SUMMARY = "summary";
    private static final String PARAM_COUNT = "count";
    private static final String PARAM_TOTAL_COUNT = "total_count";
    private static final String PARAM_TYPE = "type";

   
   

    public void getPosts(Facebook facebook){
    	PagedList<Post> result = facebook.feedOperations().getFeed();
    	result.getTotalCount();
    	
    }
    
    public void getPageAccounts(Facebook facebook){
    	PagedList<Account> accounts = facebook.pageOperations().getAccounts();
    	
    }
    

    

    public String searchUsersWork(Facebook facebook,String query, int page) {

        try {
            MultiValueMap<String, String> queryMap = new LinkedMultiValueMap<String, String>();
            queryMap.add(PARAM_Q, query);
            queryMap.add(PARAM_TYPE, "user");
            queryMap.add(PARAM_LIMIT, String.valueOf(FACEBOOK_USER_NUMBER_EACH_PAGE));
            queryMap.add(PARAM_OFFSET, String.valueOf((page - 1) * FACEBOOK_USER_NUMBER_EACH_PAGE));
            String strUsers = facebook.fetchObject("search", String.class, queryMap);
            return strUsers;
        } catch (MissingAuthorizationException | InvalidAuthorizationException | RevokedAuthorizationException
                | ExpiredAuthorizationException e1) {
        	return e1.getMessage();
        }
    }
    
   




//    private void refreshAccessToken(Long accountId, Facebook facebook) {
//        SocialAccount account = boFacade.getBORepository(SocialAccount.class).load(accountId);
//        if (account == null) {
//            LOGGER.warn(LOG_SOCIAL_ACCOUNT_DOESNT_EXIST, accountId);
//            throw new BusinessException(FacebookErrorCode.INVALID_SOCIAL_ACCOUNT_ID);
//        }
//
//        FacebookAccount fAccount = null;
//        if (!account.getIsPage()) {
//            fAccount = (FacebookAccount) account;
//        } else {
//            FacebookPageAccount fpAccount = (FacebookPageAccount) account;
//            fAccount = boFacade.getBORepository(FacebookAccount.class).load(fpAccount.getFacebookAccountId());
//        }
//
//        String appId = CmtClient.getInstance().getGlobalSettings("Global.Facebook.AppId");
//        String appSecret = CmtClient.getInstance().getGlobalSettings("Global.Facebook.AppSecret");
//        FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(appId, appSecret);
//        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
//        AccessGrant access = oauthOperations.refreshAccess(fAccount.getRefreshToken(), null);
//        fAccount.setAccessToken(access.getAccessToken());
//        fAccount.setRefreshToken(access.getRefreshToken());
//        fAccount.setExpireTime(new DateTime(access.getExpireTime()));
//        fAccount.update();
//        facebook = new FacebookTemplate(access.getAccessToken());
//        String pageAccountSql = "select fpa from FacebookPageAccount fpa where fpa.facebookAccountId = ?1";
//        List<FacebookPageAccount> pageAccounts = boFacade.createQuery(pageAccountSql, FacebookPageAccount.class)
//                .setParameter(1, accountId).getResultList();
//        for (FacebookPageAccount pageAccount : pageAccounts) {
//            String pageAccessToken = facebook.pageOperations().getAccessToken(pageAccount.getPageId());
//            pageAccount.setAccessToken(pageAccessToken);
//            pageAccount.update();
//        }
//        LOGGER.debug("refreshAccessToken END");
//    }



    public static void addProxy(String host,String port,String noProxy) {
        Properties props = System.getProperties();
        if (props.getProperty("https.proxyHost") == null && props.getProperty("https.proxyPort") == null) {
            String proxyHost = host;
            String proxyPort = port;
            if (!StringUtils.isEmpty(proxyHost) && !StringUtils.isEmpty(proxyPort)) {
                props.setProperty("https.proxyHost", proxyHost);
                props.setProperty("https.proxyPort", proxyPort);
                if (!StringUtils.isEmpty(noProxy)) {
                    props.setProperty("http.nonProxyHosts", noProxy);
                }
            }
        }
    }


    private ObjectMapper getObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        return om;
    }

    public String uploadImageToAlbum(Facebook facebook,String caption,boolean published,String url){
        try {
//            MultiValueMap<String, String> queryMap = new LinkedMultiValueMap<String, String>();
//            queryMap.add(PARAM_Q, query);
//            queryMap.add(PARAM_TYPE, "user");
//            queryMap.add(PARAM_LIMIT, String.valueOf(FACEBOOK_USER_NUMBER_EACH_PAGE));
//            queryMap.add(PARAM_OFFSET, String.valueOf((page - 1) * FACEBOOK_USER_NUMBER_EACH_PAGE));
//            String strUsers = facebook.publish("me/photos/", null, queryMap);
            MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
            data.add("caption", caption);
            //data.add("published", published);
            data.add("url", url);
            String result = "";
            try {

                result=facebook.publish("me/photos", null, data);

            } catch (ApiException e) {
                return e.getMessage();
            }
            return result;
        } catch (MissingAuthorizationException | InvalidAuthorizationException | RevokedAuthorizationException
                | ExpiredAuthorizationException e1) {
            return e1.getMessage();
        }
    }
    public String postToFacebookWithImage(Facebook facebook, String content, String link, List<String> imageUrls, Long accountId) {


        if (StringUtils.isEmpty(content)) {
            throw new BusinessException("CONTENT_NOT_EXIST");
        }


        String statusId = "";
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
        data.add(PARAM_MESSAGE, content);
        if (link != null) {
           // data.add(PARAM_LINK, link);
        }
//        for (String imageUrl : imageUrls) {
//            data.add(PARAM_PICTURE, imageUrl);
//        }
        String object_attachment = imageUrls.get(0);
        data.add("object_attachment",object_attachment);
        try {

                //statusId =
                        facebook.post("me/feed",  data);

        } catch (ApiException e) {
            return e.getMessage();
        }

        return statusId;
    }



}
