package com.fjd.instagram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fjd.cli.BusinessException;
import com.fjd.pinterest.PinterestClientServiceImpl;
import com.fjd.social.CmtClient;
import com.fjd.social.HttpCommonErrorHandler;
import com.fjd.social.HttpCommonUtil;
import com.fjd.social.HttpUtil;
import com.fjd.social.SystemException;

import twitter4j.JSONException;
import twitter4j.JSONObject;

/**
 * @author I058953
 * 
 */
public class InstagramClientServiceImpl  {
    private static final Logger LOGGER = Logger.getLogger(InstagramClientServiceImpl.class);
    private static final String APIURL = "https://api.instagram.com/";
    private static final String INSTAGRAM_IMAGE_TYPE = "image";
    private static final Integer FETCH_LIMIT = 20;
    private static final String STR_WEBSITE = "website";
    private static final String STR_PROFILE_PICTURE = "profile_picture";
    private static final String STR_USERNAME = "username";
    private static final String STR_ID = "id";
    private static final String STR_BIO = "bio";
    private static final String STR_FULL_NAME = "full_name";
    private static final String STR_DATA = "data";
    private static final String STR_TYPE = "type";
    private static final String STR_CREATED_TIME = "created_time";
    private static final String STR_CAPTION = "caption";
    private static final String STR_TEXT = "text";
    private static final String STR_FROM = "from";
    private static final String STR_PAGINATION = "pagination";
    private static final String STR_NEXT_MAX_ID = "next_max_id";
    private static final String STR_COMMENTS = "comments";
    private static final String STR_COUNT = "count";
    private static final String STR_LIKES = "likes";
    private static final String STR_TAGS = "tags";
    private static final String STR_LINK = "link";
    private static final String STR_URL = "url";
    private static final String STR_IMAGES = "images";
    private static final String STR_STANDARD_RESOLUTION = "standard_resolution";

    private static String PERMISSION_APPLIED = "basic public_content follower_list comments relationships likes";
    private final HttpCommonErrorHandler instagramErrorHandler = new InstagramErrorHandler();
    private final HttpCommonUtil httpCommonUtil = new HttpCommonUtil("Global.Instagram.UseMockServer", "instagram",
            APIURL, instagramErrorHandler, PinterestClientServiceImpl.getPinterestInstagramRequestHeader());
    private static final String INSTAGRAM = "Instagram";

    /**
     * Get instagram authorization url
     * https://api.instagram.com/oauth/authorize/?client_id=CLIENT-ID&redirect_uri=REDIRECT-URI&response_type=code&scope
     * =likes+relationships
     */
    
    public String getAuthorizationUrl() {
        LOGGER.debug("getAuthorizationUrl BEGIN");
        String appId = CmtClient.getInstance().getGlobalSettings("Global.Instagram.AppId");
        String redirectUrl = CmtClient.getInstance().getGlobalSettings("Global.Instagram.RedirectUri");
        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
        urlParameterMp.put("client_id", appId);
        urlParameterMp.put("redirect_uri", redirectUrl);
        urlParameterMp.put("response_type", "code");
        urlParameterMp.put("scope", PERMISSION_APPLIED);
        String url = httpCommonUtil.getUrl("oauth/authorize/", urlParameterMp);
        LOGGER.info("getAuthorizationUrl END url is {}"+ url);
        return url;
    }

    /**
     * Get instagram access token
     * https://api.instagram.com/oauth/access_token?client_id=CLIENT_ID&client_secret=CLIENT_SECRET&grant_type=
     * authorization_code&redirect_uri=AUTHORIZATION_REDIRECT_URI&code=CODE
     */
    
    public Map<String, Object> getAccessToken(String code) {
        LOGGER.info("getAccessToken BEGIN code is {}"+ code);
        Map<String, Object> token = new HashMap<String, Object>();
        String appId = CmtClient.getInstance().getGlobalSettings("Global.Instagram.AppId");
        String appSecret = CmtClient.getInstance().getGlobalSettings("Global.Instagram.AppSecret");
        String redirect_URI = CmtClient.getInstance().getGlobalSettings("Global.Instagram.RedirectUri");
        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
        Map<String, Object> paramMp = new HashMap<String, Object>();
        paramMp.put("client_id", appId);
        paramMp.put("client_secret", appSecret);
        paramMp.put("grant_type", "authorization_code");
        paramMp.put("redirect_uri", redirect_URI);
        paramMp.put("code", code);
        Map<String, String> headerMp = new HashMap<String, String>();
        headerMp.put("Origin", "https://www.instagram.com/developer/");
        String url = httpCommonUtil.getUrl("oauth/access_token", urlParameterMp);

        String ret = httpCommonUtil.sendRequest(url, "Post", headerMp, paramMp, HttpUtil.PARAMETER_TYPE_URLENCODED);
        try {
            JSONObject resultObj = new JSONObject(ret);
            token.put("access_token", resultObj.getString("access_token"));
            JSONObject userObj = new JSONObject(resultObj.getString("user"));
            token.put("uid", userObj.getString("id"));
            token.put("username", userObj.getString("username"));
            token.put("full_name", userObj.getString("full_name"));
            token.put("profile_picture", userObj.getString("profile_picture"));
            LOGGER.info("getAccessToken END token is {}"+ token);
            return token;
        } catch (JSONException e) {
            LOGGER.error("InstagramClientServiceImpl:Covert from JSONString To Obj Error with result:" + ret, e);
            throw new SystemException(e);
        }

    }

    /**
     * retrieve instagram messages - comments.
     * 
     * @param accessToken
     * @param sentMessageId
     * @param sinceId
     */
    
//    public List<InstagramMessageInfo> getComments(String accessToken, String sentMessageId, String sinceId) {
//        LOGGER.info("getComments BEGIN access_token:{},sentMessageId:{},sinceId:{}"+ accessToken, sentMessageId,
//                sinceId);
//        checkAccessToken(accessToken);
//        if (StringUtils.isEmpty(sentMessageId)) {
//            LOGGER.warn("getComments param error, sentMessageId is {}, return empty list", sentMessageId);
//            throw new BusinessException(SocialCommonErrorCode.INVALID_MESSAGE_ID, INSTAGRAM, sentMessageId);
//        }
//        List<InstagramMessageInfo> infos = Lists.newArrayList();
//        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
//        urlParameterMp.put("access_token", accessToken);
//
//        String strResult = httpCommonUtil.doGet("v1/media/" + sentMessageId + "/comments/", urlParameterMp);
//
//        try {
//
//            JSONObject jsonResult = new JSONObject(strResult);
//            if (jsonResult.has(STR_DATA)) {
//                JSONArray commentArray = jsonResult.getJSONArray(STR_DATA);
//                for (int i = 0; i < commentArray.length(); i++) {
//                    JSONObject comment = (JSONObject) commentArray.get(i);
//                    String msgId = comment.getString(STR_ID);
//
//                    // this message has already been saved.
//                    if ((null != sinceId) && msgId.compareToIgnoreCase(sinceId) <= 0) {
//                        continue;
//                    }
//
//                    InstagramMessageInfo info = new InstagramMessageInfo();
//                    info.setType("REPLY");
//                    info.setMessageId(msgId);
//                    JSONObject user = comment.getJSONObject(STR_FROM);
//                    info.setUserId(user.getString(STR_ID));
//                    info.setUserName(HttpCommonUtil.getJSONFieldValue(user, Arrays.asList(STR_USERNAME), false));
//                    info.setUserProfileUrl(
//                            HttpCommonUtil.getJSONFieldValue(user, Arrays.asList(STR_PROFILE_PICTURE), false));
//                    info.setCreatedTime(new DateTime(comment.getLong(STR_CREATED_TIME) * 1000L));
//                    info.setMessage(HttpCommonUtil.getJSONFieldValue(comment, Arrays.asList(STR_TEXT), false));
//                    info.setSentMessageId(sentMessageId);
//                    infos.add(info);
//                }
//
//            }
//            LOGGER.info("getComments END access_token:{},sentMessageId:{},sinceId:{}", accessToken, sentMessageId,
//                    sinceId);
//            return infos;
//        } catch (JSONException e) {
//            LOGGER.error("InstagramClientServiceImpl error: {}.", e);
//            throw new SystemException(e);
//        }
//
//    }

    /**
     * Add comment to Instagram post.
     * 
     * @param accessToken
     * @param sentMessageId
     * @param text
     */
    
//    public String addComment(String accessToken, String sentMessageId, String text) {
//        LOGGER.info("addComment BEGIN access_token:{},sentMessageId:{},text:{}", accessToken, sentMessageId, text);
//        checkAccessToken(accessToken);
//        if (StringUtils.isEmpty(sentMessageId)) {
//            LOGGER.warn("addComment param error, sentMessageId is {},text:{}", sentMessageId, text);
//            throw new BusinessException(SocialCommonErrorCode.INVALID_MESSAGE_ID, INSTAGRAM, sentMessageId);
//        }
//        if (StringUtils.isEmpty(text)) {
//            LOGGER.warn("addComment param error, sentMessageId is {},text:{}", sentMessageId, text);
//            throw new BusinessException(SocialCommonErrorCode.CONTENT_NOT_EXIST);
//        }
//
//        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
//        Map<String, Object> paramMp = new HashMap<String, Object>();
//        paramMp.put("access_token", accessToken);
//        paramMp.put("text", text);
//        String url = httpCommonUtil.getUrl("v1/media/" + sentMessageId + "/comments/", urlParameterMp);
//        Map<String, String> headerMp = new HashMap<String, String>();
//        headerMp.put("Origin", APIURL);
//        String strResult = null;
//
//        try {
//            LOGGER.info("Add instagram comment: {}", url);
//            strResult = httpCommonUtil.sendRequest(url, "Post", headerMp, paramMp, HttpUtil.PARAMETER_TYPE_URLENCODED);
//            JSONObject jsonResult = new JSONObject(strResult);
//            JSONObject data = jsonResult.getJSONObject(STR_DATA);
//            String msgId = HttpCommonUtil.getJSONFieldValue(data, Arrays.asList(STR_ID), true);
//            LOGGER.info("addComment END, msgId :{}", msgId);
//            return msgId;
//        } catch (JSONException e) {
//            LOGGER.error("InstagramClientServiceImpl error: {}.", e);
//            throw new SystemException(e);
//        }
//    }

    /**
     * retrieve instagram messages - likes.
     * 
     * @param accessToken
     * @param sentMessageId
     * @param likeList
     */
    
//    public List<InstagramMessageInfo> getLikes(String accessToken, String sentMessageId, List<String> likeList) {
//        LOGGER.info("getLikes BEGIN access_token:{},sentMessageId:{}", accessToken, sentMessageId);
//        checkAccessToken(accessToken);
//        if (StringUtils.isEmpty(sentMessageId)) {
//            LOGGER.warn("getLikes param error, sentMessageId is {}", sentMessageId);
//            throw new BusinessException(SocialCommonErrorCode.INVALID_MESSAGE_ID, INSTAGRAM, sentMessageId);
//        }
//        List<InstagramMessageInfo> infos = Lists.newArrayList();
//        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
//        urlParameterMp.put("access_token", accessToken);
//        String strResult = null;
//        try {
//
//            strResult = httpCommonUtil.doGet("v1/media/" + sentMessageId + "/likes/", urlParameterMp);
//            JSONObject jsonResult = new JSONObject(strResult);
//            if (jsonResult.has(STR_DATA)) {
//                JSONArray likeArray = jsonResult.getJSONArray(STR_DATA);
//                for (int i = 0; i < likeArray.length(); i++) {
//                    JSONObject like = (JSONObject) likeArray.get(i);
//                    String userId = like.getString(STR_ID);
//                    if (likeList.contains(userId)) {
//                        continue;
//                    }
//
//                    InstagramMessageInfo info = new InstagramMessageInfo();
//                    info.setType("LIKE");
//                    info.setUserId(userId);
//                    info.setUserName(HttpCommonUtil.getJSONFieldValue(like, Arrays.asList(STR_USERNAME), false));
//                    info.setSentMessageId(sentMessageId);
//                    infos.add(info);
//                }
//            }
//            LOGGER.info("getLikes END access_token:{},sentMessageId:{}", accessToken, sentMessageId);
//            return infos;
//        } catch (JSONException e) {
//            LOGGER.error("InstagramClientServiceImpl error: {}.", e);
//            throw new SystemException(e);
//        }
//
//    }

    /**
     * get statistics - comments/likes for a specific sent message.
     * 
     * @param sentMessageId
     * @param accessToken
     */
    
//    public Integer[] getStatistics(String sentMessageId, String accessToken) {
//        LOGGER.info("getStatistics BEGIN access_token:{},sentMessageId:{}"+ accessToken, sentMessageId);
//        checkAccessToken(accessToken);
//        if (StringUtils.isEmpty(sentMessageId)) {
//            LOGGER.warn("getStatistics param error, sentMessageId is {}", sentMessageId);
//            throw new BusinessException(SocialCommonErrorCode.INVALID_MESSAGE_ID, INSTAGRAM, sentMessageId);
//        }
//        JSONObject jsonObj = null;
//        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
//        urlParameterMp.put("access_token", accessToken);
//        String strResult = httpCommonUtil.doGet("v1/media/" + sentMessageId, urlParameterMp);
//        Integer[] statistics = { 0, 0 };
//        try {
//
//            JSONObject jsonResult = new JSONObject(strResult);
//            if (jsonResult.has(STR_DATA)) {
//                jsonObj = jsonResult.getJSONObject(STR_DATA);
//                Integer commentsCount = 0, likesCount = 0;
//                commentsCount = jsonObj.getJSONObject("comments").getInt("count");
//                likesCount = jsonObj.getJSONObject("likes").getInt("count");
//                statistics[0] = Integer.valueOf(commentsCount);
//                statistics[1] = Integer.valueOf(likesCount);
//            }
//            LOGGER.debug("getStatistics END");
//            return statistics;
//        } catch (JSONException e) {
//            LOGGER.error("getStatistics JSONException, json:{}, {}.", strResult, e);
//            throw new SystemException(e);
//        }
//    }

    /**
     * search users based on the passed keywords.
     * won't throw Exception
     * 
     * https://api.instagram.com/v1/users/search/?access_token={accessToken}&q={keywords}
     * 
     * @param query
     */
    
    public String searchUsersWork(String query,String accessToken) {
        LOGGER.info("searchUsers: query {}."+ query);

        if (accessToken == null || accessToken == "") {
            LOGGER.error(
                    "searchUsers: Global Setting didn't have access token of Instagram Configured, the function will not work.");
            return "accesstoken is empty";
        }

        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
        urlParameterMp.put("access_token", accessToken);
        urlParameterMp.put("q", query);
        String strResult = httpCommonUtil.doGet("v1/users/search/", urlParameterMp);
       
            return strResult;
       

    }

    /**
     * get user details based on userId.
     * 
     * https://api.instagram.com/v1/users/{userId}/?access_token={accessToken}
     * 
     * @param userId
     */
    
//    public List<String> getUserDetail(String userId) {
//        LOGGER.info("getUserDetail BEGIN, userId: {}.", userId);
//        List<String> res = Lists.newArrayList();
//        SocialUserInfo sui = lookupUser(userId);
//        if (sui != null) {
//            res.add(sui.getWebsite());
//            res.add(sui.getDescription());
//        }
//        return res;
//
//    }

    /**
     * get user details based on userId.
     * 
     * https://api.instagram.com/v1/users/{userId}/?access_token={accessToken}
     * 
     * @param userId
     */
//    private SocialUserInfo lookupUser(String userId) {
//        LOGGER.info("lookupUser BEGIN userId:{}.", userId);
//        SocialUserInfo userInfo = new SocialUserInfo();
//        if (StringUtils.isEmpty(userId)) {
//            LOGGER.warn("lookupUser: Passed userId is null or empty.");
//            throw new BusinessException(InstagramErrorCode.INVALID_USER_ID, userId);
//        }
//
//        String accessToken = CmtClient.getInstance().getGlobalSettings("Global.Instagram.AccessToken");
//        if (StringUtils.isEmpty(accessToken)) {
//            LOGGER.error(
//                    "lookupUser: Vault didn't have access token of Instagram Configured, the function will not work.");
//            return null;
//        }
//
//        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
//        urlParameterMp.put("access_token", accessToken);
//        String strResult = httpCommonUtil.doGet("v1/users/" + userId + "/", urlParameterMp);
//        try {
//            JSONObject jsonResult = new JSONObject(strResult);
//            JSONObject user = jsonResult.getJSONObject(STR_DATA);
//            userInfo.setName(HttpCommonUtil.getJSONFieldValue(user, Arrays.asList(STR_USERNAME), false));
//            userInfo.setScreenName(HttpCommonUtil.getJSONFieldValue(user, Arrays.asList(STR_ID), false));
//            userInfo.setDescription(HttpCommonUtil.getJSONFieldValue(user, Arrays.asList(STR_BIO), false));
//            userInfo.setWebsite(HttpCommonUtil.getJSONFieldValue(user, Arrays.asList(STR_WEBSITE), false));
//            userInfo.setImageUrl(HttpCommonUtil.getJSONFieldValue(user, Arrays.asList(STR_PROFILE_PICTURE), false));
//            userInfo.setInsFullName(HttpCommonUtil.getJSONFieldValue(user, Arrays.asList(STR_FULL_NAME), false));
//            userInfo.setHomepageUrl("https://www.instagram.com/" + userInfo.getName());
//            LOGGER.debug("lookupUser END");
//            return userInfo;
//        } catch (JSONException e) {
//            LOGGER.error("Instagram lookupUser - JSONString error with result: {},{}.", strResult, e);
//            throw new SystemException(e);
//        }
//
//    }
//
//    
//    public List<SocialUserInfo> lookupUsersByUids(String[] uids) {
//        LOGGER.info("lookupUsersByUids BEGIN");
//        List<SocialUserInfo> res = Lists.newArrayList();
//        for (int i = 0; i < uids.length; i++) {
//            SocialUserInfo userInfo = lookupUser(uids[i]);
//            if (userInfo != null && userInfo.getScreenName() != null) {
//                res.add(userInfo);
//            }
//
//        }
//        LOGGER.debug("lookupUsersByUids END");
//        return res;
//    }

    /**
     * retrieve instagram posts.
     * 
     * @param accessToken
     * @param userId
     * @param nextMaxId
     * 
     * @return
     *         instagram message content as json object.
     * 
     *         https://api.instagram.com/v1/users/{userId}/media/recent/?access_token={accessToken}&count=COUNT&max_id=
     *         {nextMaxId}
     */
//    private String getInstagramPostsSinglePage(String accessToken, String userId, String nextMaxId) {
//        LOGGER.info("getInstagramPostsSinglePage BEGIN access_token:{},userId:{},nextMaxId:{}", accessToken, userId,
//                nextMaxId);
//        checkAccessToken(accessToken);
//        if (StringUtils.isEmpty(userId)) {
//            LOGGER.warn("retrieveInstagramPosts param error, userId is {}", userId);
//            throw new BusinessException(InstagramErrorCode.INVALID_USER_ID, userId);
//        }
//
//        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
//        urlParameterMp.put("access_token", accessToken);
//        urlParameterMp.put("count", FETCH_LIMIT + "");
//        urlParameterMp.put("max_id", nextMaxId);
//        urlParameterMp.put("access_token", accessToken);
//
//        String strRet = httpCommonUtil.doGet("v1/users/" + userId + "/media/recent/", urlParameterMp);
//        LOGGER.debug("getInstagramPostsSinglePage END, result:{}", strRet);
//        return strRet;
//    }

    /**
     * Instagram messages id like 1428809307587996228_3082989981
     * 1428809307587996228 is for message, which increases over time
     * 3082989981 is userId of author
     * The api doc said retrieving messages newer than "min_id" and older than "max_id" with "count"
     * however min_id doesn't work actually, need to filter it manually.
     * always starts with max_id=0 for latest.
     * This method only return type=images (no videos)
     * 

     * 
     * @return
     */
//    private List<InstagramPostInfo> getInstagramPosts(String accessToken, String userId, String minId) {
//        LOGGER.info("getInstagramPosts BEGIN userId is {},sinceId is {}", userId, minId);
//        List<InstagramPostInfo> posts = new ArrayList<>();
//        String maxId = "0";
//        outer: do {
//            String jsonCurrentPageStr = getInstagramPostsSinglePage(accessToken, userId, maxId);
//
//            try {
//                JSONObject jsonCurrentPage = new JSONObject(jsonCurrentPageStr);
//                maxId = HttpCommonUtil.getJSONFieldValue(jsonCurrentPage,
//                        Arrays.asList(STR_PAGINATION, STR_NEXT_MAX_ID), true);
//                JSONArray jsonCurrentPagePosts = jsonCurrentPage.optJSONArray(STR_DATA);
//                if (jsonCurrentPagePosts != null) {
//                    for (int i = 0; i < jsonCurrentPagePosts.length(); i++) {
//                        JSONObject post = jsonCurrentPagePosts.optJSONObject(i);
//
//                        String currentMessageId = post.getString(STR_ID);
//                        // Use the type field to differentiate between image and
//                        // video media in the response.
//                        String type = HttpCommonUtil.getJSONFieldValue(post, Arrays.asList(STR_TYPE), false);
//                        // if current message id is smaller than/ equal to(means older than) the minId(sinceId), stop!
//                        if (minId.compareToIgnoreCase(currentMessageId) >= 0) {
//                            break outer;
//                        }
//                        // Use the type field to differentiate between image and video media in the response.
//                        if (0 != INSTAGRAM_IMAGE_TYPE.compareToIgnoreCase(type)) {
//                            continue;
//                        }
//                        // start to prepare the instagram content.
//                        InstagramPostInfo ipi = new InstagramPostInfo();
//                        List<String> tags = new ArrayList<String>();
//                        if (post.has(STR_TAGS)) {
//                            JSONArray jsonTags = post.optJSONArray(STR_TAGS);
//                            for (int j = 0; j < jsonTags.length(); j++) {
//                                tags.add(jsonTags.getString(j).toLowerCase());
//                            }
//                        }
//                        ipi.setTags(tags);
//                        ipi.setImageUrl(HttpCommonUtil.getJSONFieldValue(post,
//                                Arrays.asList(STR_IMAGES, STR_STANDARD_RESOLUTION, STR_URL), false));
//                        ipi.setLink(HttpCommonUtil.getJSONFieldValue(post, Arrays.asList(STR_LINK), false));
//                        ipi.setCreateTime(new DateTime(post.getLong(STR_CREATED_TIME) * 1000L));
//                        ipi.setId(currentMessageId);
//                        ipi.setText(
//                                HttpCommonUtil.getJSONFieldValue(post, Arrays.asList(STR_CAPTION, STR_TEXT), false));
//                        ipi.setLikesCount(Integer.valueOf(
//                                HttpCommonUtil.getJSONFieldValue(post, Arrays.asList(STR_LIKES, STR_COUNT), false)));
//                        ipi.setCommentsCount(Integer.valueOf(
//                                HttpCommonUtil.getJSONFieldValue(post, Arrays.asList(STR_COMMENTS, STR_COUNT), false)));
//                        ipi.setType(type);
//
//                        posts.add(ipi);
//                    }
//                }
//            } catch (JSONException e) {
//                LOGGER.error("getInstagramPosts JSONException {},userId:{}", e, userId);
//                maxId = null;
//                break;
//
//            }
//
//        } while (null != maxId);
//
//        LOGGER.info("getInstagramPosts END, {} posts retrieved.", posts.size());
//        return posts;
//    }

    
//    public List<CustomerSocialMessageInfo> getCustomerFeeds(String userId, String minId) {
//        LOGGER.info("getCustomerFeeds BEGIN,  userId is {}, minId is {}."+ userId+ minId);
//        String accessToken = CmtClient.getInstance().getGlobalSettings("Global.Instagram.AccessToken");
//        List<InstagramPostInfo> posts = getInstagramPosts(accessToken, userId, minId);
//        List<CustomerSocialMessageInfo> result = new ArrayList<>();
//        for (InstagramPostInfo post : posts) {
//            CustomerSocialMessageInfo feed = new CustomerSocialMessageInfo();
//            feed.setId(post.getId());
//            feed.setText(post.getText());
//            feed.setCreatedAt(post.getCreateTime());
//            result.add(feed);
//        }
//        LOGGER.info("getCustomerFeeds END, {} feeds retrieved.", result.size());
//        return result;
//    }
//
//    
//    public List<InstagramPostInfo> getInstagramPostsWithHashTag(String accessToken, String userId, String minId,
//            String hashTag) {
//        LOGGER.info("getInstagramPostsWithHashTag BEGIN, userId is {},minId is {},hashTag is {}."+userId+ minId+
//                hashTag);
//        if (StringUtils.isEmpty(hashTag)) {
//            throw new BusinessException("InstagramErrorCode.CAMPAIGN_HASHTAG_INVALID");
//        }
//        List<InstagramPostInfo> posts = getInstagramPosts(accessToken, userId, minId);
//        List<InstagramPostInfo> result = new ArrayList<>();
//        for (InstagramPostInfo post : posts) {
//            if (post.getTags().contains(hashTag)) {
//                result.add(post);
//            }
//
//        }
//        LOGGER.info("getInstagramPostsWithHashTag END. {} posts retrieved."+result.size());
//        return result;
//    }

    private void checkAccessToken(String accessToken) {
        if (StringUtils.isEmpty(accessToken)) {
            throw new BusinessException("SocialCommonErrorCode.AUTH_EXPIRE"+ "Instagram");
        }
    }

}

class InstagramErrorHandler implements HttpCommonErrorHandler {
    private static final Logger LOGGER = Logger.getLogger(InstagramErrorHandler.class);

    private void handleErrorMessage(Integer errorCode, String errmsg) {

        throw new BusinessException("SocialCommonErrorCode.SOCIAL_API_RETURN_ERROR"+"Instagram"+ errorCode+ errmsg);

    }

    
    public void handleError(String result) {
        try {
            // warn: may have xml api in the future
            if (!result.startsWith("{")) {
                return;
            }
            JSONObject resultJsonObj = new JSONObject(result);
//            String[] fieldArr = JSONObject.getNames(resultJsonObj);
//            if (ArrayUtils.contains(fieldArr, "meta") && fieldArr.length == 1) {
//                JSONObject meta = resultJsonObj.optJSONObject("meta");
//                Integer code = meta.optInt("code");
//                String message = meta.optString("message");
//                if (!StringUtils.isEmpty(message)) {
//                    LOGGER.warn("Instagram Server Return Error with result:" + result);
//
//                    handleErrorMessage(code, message);
//                }
//
//                String errMsg = meta.optString("error_message");
//                if (!StringUtils.isEmpty(errMsg)) {
//                    LOGGER.warn("Instagram Server Return Error with result:" + result);
//
//                    handleErrorMessage(code, errMsg);
//                }
//            }
//            if (ArrayUtils.contains(fieldArr, "error_message") && ArrayUtils.contains(fieldArr, "error_type")
//                    && fieldArr.length == 3) {
//                Integer code = resultJsonObj.optInt("code");
//                String message = resultJsonObj.optString("error_message");
//                if (!StringUtils.isEmpty(message)) {
//                    LOGGER.warn("Instagram Server Return Error with result:" + result);
//
//                    handleErrorMessage(code, message);
//                }
//            }
            return;
        } catch (JSONException e) {
            LOGGER.error("InstagramClientServiceImpl: Covert from JSONString To Obj Error with result:" + result, e);
            throw new SystemException(e);
        }

    }

}