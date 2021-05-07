package com.fjd.weibo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fjd.cli.BusinessException;
import com.fjd.social.CmtClient;
import com.fjd.social.HttpCommonErrorHandler;
import com.fjd.social.HttpCommonUtil;
import com.fjd.social.HttpUtil;
import com.fjd.social.SystemException;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;



public class WeiboClientServiceImpl {
    public static final String AUTH_BASE_URL = "https://api.weibo.com/oauth2/";
    public static final String BIZ_URL = "https://c.api.weibo.com/2/";
    public static final String BASE_URL = "https://api.weibo.com/2/";
    public static final String RM_URL = "https://rm.api.weibo.com/2/";
    private static final Logger LOGGER = Logger.getLogger(WeiboClientServiceImpl.class);
    private static final String WEIBO_TIME_FORMAT = "EEE MMM d HH:mm:ss Z yyyy";
    private static final String SECURITY_URL = "https://discover.sap.com/sapanywhere/zh-cn";
    private static final HttpCommonErrorHandler ERROR_HANDLER = new WeiboErrorHandler();
    private final HttpCommonUtil httpCommonUtil = new HttpCommonUtil("Global.Weibo.UseMockServer", "weibo", BASE_URL, ERROR_HANDLER);
    private final HttpCommonUtil httpAuthUtil = new HttpCommonUtil("Global.Weibo.UseMockServer",
            "weibo/oauth2", AUTH_BASE_URL, ERROR_HANDLER);



//    private Map<String, Object> getUserInfoByScreenName(String screenName, String accessToken) {
//        LOGGER.info("getUserInfoByScreenName BEGIN for screenName({})", screenName);
//        Map<String, Object> userInfo = new HashMap();
//        checkAccessToken(accessToken);
//        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
//        urlParameterMp.put("screen_name", screenName);
//        urlParameterMp.put("access_token", accessToken);
//        String result = HttpCommonUtil.doGet(HCPI, "users/show.json", urlParameterMp);
//
//        try {
//            LOGGER.debug("getUserInfoByScreenName result: " + result);
//            JSONObject resultObj = new JSONObject(result);
//            userInfo.put("image_url", resultObj.getString("avatar_large"));
//            userInfo.put("favourite_count", resultObj.getString("favourites_count"));
//            userInfo.put("followers_count", resultObj.getString("followers_count"));
//            userInfo.put("friends_count", resultObj.getString("friends_count"));
//            userInfo.put("status_count", resultObj.getString("statuses_count"));
//            userInfo.put("name", resultObj.getString("name"));
//            userInfo.put("screen_name", resultObj.getString("screen_name"));
//            userInfo.put("uid", resultObj.getString("id"));
//            userInfo.put("description", resultObj.getString("description"));
//            userInfo.put("url", resultObj.getString("url"));
//            LOGGER.debug("getUserInfoByScreenName END");
//            return userInfo;
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//
//    }
//    private SocialUserInfo getSocialUserInfoByScreenName(String screenName, String accessToken) {
//        Map<String, Object> weiboUserInfoMap = getUserInfoByScreenName(screenName, accessToken);
//        if (weiboUserInfoMap == null) {
//            return null;
//        }
//        SocialUserInfo userInfo = buildWeiboUserInfo(weiboUserInfoMap);
//        return userInfo;
//    }
//    private Map<String, Object> getUserInfoMapByUid(String uid, String accessToken) {
//        LOGGER.info("getUserInfoByUid BEGIN for uid({})", uid);
//        Map<String, Object> userInfo = Maps.newHashMap();
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        checkAccessToken(accessToken);
//        urlParameterMp.put("uid", uid);
//        urlParameterMp.put("access_token", accessToken);
//        String result = HttpCommonUtil.doGet(HCPI, "users/show.json", urlParameterMp);
//
//        try {
//            LOGGER.debug("getUserInfoByUid result: " + result);
//            JSONObject resultObj = new JSONObject(result);
//            userInfo.put("image_url", resultObj.getString("avatar_large"));
//            userInfo.put("favourite_count", resultObj.getString("favourites_count"));
//            userInfo.put("followers_count", resultObj.getString("followers_count"));
//            userInfo.put("friends_count", resultObj.getString("friends_count"));
//            userInfo.put("status_count", resultObj.getString("statuses_count"));
//            userInfo.put("name", resultObj.getString("name"));
//            userInfo.put("screen_name", resultObj.getString("screen_name"));
//            userInfo.put("uid", resultObj.getString("id"));
//            userInfo.put("description", resultObj.getString("description"));
//            userInfo.put("url", resultObj.getString("url"));
//            LOGGER.debug("getUserInfoByUid END");
//            return userInfo;
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//
//    }
//    public SocialUserInfo getSocialUserInfoByUid(String uid, String accessToken) {
//        Map<String, Object> result = getUserInfoMapByUid(uid, accessToken);
//        SocialUserInfo socialUserInfo = buildWeiboUserInfo(result);
//        return socialUserInfo;
//
//    }


    public List<String> searchUsers(String query, String accessToken) {
        LOGGER.info("searchUsers BEGIN for query({"+query+"})");
        List<String> res = new ArrayList();
        checkAccessToken(accessToken);
        Map<String, String> urlParameterMp = new LinkedHashMap<>();

        urlParameterMp.put("q", query);
        urlParameterMp.put("access_token", accessToken);
        String result = httpCommonUtil.doGet( "search/suggestions/users.json", urlParameterMp);
        JSONArray resultArray;

        try {
            LOGGER.debug("getUserInfoByUid result: " + result);
            resultArray = new JSONArray(result);
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject obj = resultArray.getJSONObject(i);
                String screenName = obj.getString("screen_name");
                res.add(screenName);
            }
            LOGGER.debug("searchUsers END");
            return res;
        } catch (JSONException e) {
            throw new SystemException(e);
        }

    }

//    private HttpCommonParamInfo getHttpCommonParamBiz() {
//        HttpCommonParamInfo hcpi = new HttpCommonParamInfo("Global.Weibo.UseMockServer", "weibo/biz", BIZ_URL,
//                ERROR_HANDLER);
//        return hcpi;
//    }
//    public List<Object[]> getCustomerFeeds(String uid, DateTime startTime, Boolean isFirstTime, String accessToken) {
//        LOGGER.info("getCustomerFeeds BEGIN for uid("+uid+"), accessToken is {"+accessToken+"}");
//        List<Object[]> res = Lists.newArrayList();
//        checkAccessToken(accessToken);
//
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        urlParameterMp.put("ids", uid);
//        urlParameterMp.put("access_token", accessToken);
//        if (isFirstTime) {
//            urlParameterMp.put("endtime", DateTime.now().getMillis() / 1000 + "");
//        } else {
//            urlParameterMp.put("starttime", startTime.getMillis() / 1000 + "");
//        }
//        HttpCommonParamInfo hcpiBiz = getHttpCommonParamBiz();
//        String result = HttpCommonUtil.doGet(hcpiBiz, "search/statuses/limited.json?count=50", urlParameterMp);
//        JSONObject resultObj;
//        try {
//            LOGGER.debug("getCustomerFeeds result: " + result);
//            resultObj = new JSONObject(result);
//            JSONArray resultArray = resultObj.getJSONArray("statuses");
//            for (int i = 0; i < resultArray.length(); i++) {
//                Object[] weibo = new Object[3];
//                JSONObject obj = resultArray.getJSONObject(i);
//                weibo[0] = obj.getString("id");
//                weibo[1] = obj.getString("text");
//                weibo[2] = obj.getString("created_at");
//                res.add(weibo);
//            }
//            LOGGER.debug("getCustomerFeeds END");
//            return res;
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//
//    }
//    public String shareStatus(String content, List<File> imageFiles, String accessToken) {
//        LOGGER.info("shareStatus BEGIN with content({})", content);
//        checkAccessToken(accessToken);
//        Map<String, Object> parameterMp = new HashMap<>();
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        urlParameterMp.put("access_token", accessToken);
//        String url = HttpCommonUtil.getUrl(HCPI, "statuses/share.json", urlParameterMp);
//        String result;
//        content = content + " " + SECURITY_URL;
//        if (CollectionUtils.isNotEmpty(imageFiles)) {
//            parameterMp.put("pic", imageFiles.get(0));
//            parameterMp.put("status", content);
//            result = HttpCommonUtil.doPostMultipart(HCPI, "statuses/share.json", urlParameterMp, parameterMp);
//        } else {
//            urlParameterMp.put("status", content);
//            result = HttpCommonUtil.doPost(HCPI, "statuses/share.json", urlParameterMp);
//        }
//        JSONObject resultObj;
//        try {
//            LOGGER.debug("shareStatus result: " + result);
//            resultObj = new JSONObject(result);
//            String messageId = resultObj.getString("idstr");
//            LOGGER.debug("shareStatus END, messageId:({})", messageId);
//            return messageId;
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//    }
//    /**
//     * get comments of a weibo
//     *
//     * @param accessToken
//     * @param sentMessageId
//     * @param sinceId
//     *            default "0"
//     * @return
//     */
//    public List<WeiboMessageInfo> getComments(String accessToken, String sentMessageId, String sinceId) {
//        LOGGER.info("getComments BEGIN for sentMessageId({})", sentMessageId);
//        checkAccessToken(accessToken);
//        List<WeiboMessageInfo> infos = Lists.newArrayList();
//        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
//        urlParameterMp.put("id", sentMessageId);
//        urlParameterMp.put("access_token", accessToken);
//        if (sinceId != null) {
//            urlParameterMp.put("since_id", sinceId);
//        }
//        urlParameterMp.put("count", "200");
//        String result = HttpCommonUtil.doGet(HCPI, "comments/show.json", urlParameterMp);
//        JSONObject resultObj;
//        try {
//            LOGGER.debug("getComments result:" + result);
//            JSONArray comments = new JSONArray();
//            if (result.startsWith("{")) {
//                resultObj = new JSONObject(result);
//                comments = resultObj.getJSONArray("comments");
//            } else if (result.startsWith("[")) {
//                comments = new JSONArray(result);
//            }
//            int length = comments.length();
//            LOGGER.debug("got {} comments", length);
//            for (int i = 0; i < length; i++) {
//                JSONObject comment = (JSONObject) comments.get(i);
//                WeiboMessageInfo info = new WeiboMessageInfo();
//                info.setType("REPLY");
//                info.setMessageId(comment.getString("id"));
//                JSONObject user = comment.getJSONObject("user");
//                info.setUserId(user.getString("id"));
//                info.setUserName(user.getString("screen_name"));
//                info.setUserImageUrl(user.getString("avatar_large"));
//                DateTimeFormatter formatter = DateTimeFormat.forPattern(WEIBO_TIME_FORMAT);
//                info.setCreatedTime(DateTime.parse(comment.getString("created_at"), formatter));
//                info.setMessage(getMessageContentUnderLengthlimit(comment.getString("text")));
//                info.setSentMessageId(sentMessageId);
//                infos.add(info);
//            }
//            LOGGER.debug("getComments END");
//            return infos;
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//    }

    private String getMessageContentUnderLengthlimit(String messageContent) {
        if (messageContent.length() > 255) {
            return messageContent.substring(0, 255);
        } else {
            return messageContent;
        }
    }

//    public List<WeiboMessageInfo> getReposts(String accessToken, String sentMessageId, String sinceId) {
//        LOGGER.info("getReposts BEGIN for sentMessageId({})", sentMessageId);
//        checkAccessToken(accessToken);
//        List<WeiboMessageInfo> infos = Lists.newArrayList();
//        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
//
//        urlParameterMp.put("id", sentMessageId);
//        urlParameterMp.put("access_token", accessToken);
//        if (sinceId != null) {
//            urlParameterMp.put("since_id", sinceId);
//        }
//        urlParameterMp.put("count", "200");
//        String result = HttpCommonUtil.doGet(HCPI, "statuses/repost_timeline.json", urlParameterMp);
//        JSONObject resultObj;
//
//        try {
//            LOGGER.debug("getReposts result:" + result);
//            JSONArray reposts = new JSONArray();
//            if (result.startsWith("{")) {
//                resultObj = new JSONObject(result);
//                reposts = resultObj.getJSONArray("reposts");
//            } else if (result.startsWith("[")) {
//                reposts = new JSONArray(result);
//            }
//            int length = reposts.length();
//            LOGGER.debug("got {} reposts", length);
//            for (int i = 0; i < length; i++) {
//                JSONObject repost = (JSONObject) reposts.get(i);
//                WeiboMessageInfo info = new WeiboMessageInfo();
//                info.setType("REPOST");
//                info.setMessageId(repost.getString("id"));
//                JSONObject user = repost.getJSONObject("user");
//                info.setUserId(user.getString("id"));
//                info.setUserName(user.getString("screen_name"));
//                info.setUserImageUrl(user.getString("avatar_large"));
//                DateTimeFormatter formatter = DateTimeFormat.forPattern(WEIBO_TIME_FORMAT);
//                info.setCreatedTime(DateTime.parse(repost.getString("created_at"), formatter));
//                info.setMessage(getMessageContentUnderLengthlimit(repost.getString("text")));
//                info.setSentMessageId(sentMessageId);
//                infos.add(info);
//            }
//            LOGGER.debug("getReposts END");
//            return infos;
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//    }
//
//    public List<WeiboMessageInfo> getMentions(String accessToken, String sinceId) {
//        LOGGER.info("getMentions BEGIN");
//        checkAccessToken(accessToken);
//        List<WeiboMessageInfo> infos = Lists.newArrayList();
//        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
//
//        urlParameterMp.put("access_token", accessToken);
//        if (sinceId != null) {
//            urlParameterMp.put("since_id", sinceId);
//        }
//        urlParameterMp.put("count", "200");
//        String result = HttpCommonUtil.doGet(HCPI, "statuses/mentions.json", urlParameterMp);
//        JSONObject resultObj;
//
//        try {
//            LOGGER.debug("getMentions result:" + result);
//            JSONArray statuses = new JSONArray();
//            if (result.startsWith("{")) {
//                resultObj = new JSONObject(result);
//                statuses = resultObj.getJSONArray("statuses");
//            } else if (result.startsWith("[")) {
//                statuses = new JSONArray(result);
//            }
//            int length = statuses.length();
//            LOGGER.debug("got {} mentions", length);
//            for (int i = 0; i < length; i++) {
//                JSONObject status = (JSONObject) statuses.get(i);
//                WeiboMessageInfo info = new WeiboMessageInfo();
//                info.setType("DIRECT_MESSAGE");
//                info.setMessageId(status.getString("id"));
//                JSONObject user = status.getJSONObject("user");
//                info.setUserId(user.getString("id"));
//                info.setUserName(user.getString("screen_name"));
//                info.setUserImageUrl(user.getString("avatar_large"));
//                DateTimeFormatter formatter = DateTimeFormat.forPattern(WEIBO_TIME_FORMAT);
//                info.setCreatedTime(DateTime.parse(status.getString("created_at"), formatter));
//                info.setMessage(getMessageContentUnderLengthlimit(status.getString("text")));
//                info.setSentMessageId("-1");
//                infos.add(info);
//            }
//            LOGGER.debug("getMentions END");
//            return infos;
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//    }
//
//    public Integer[] getStatistics(String sentMessageId, String accessToken) {
//        LOGGER.info("getWeiboStatistics BEGIN with sentMessageId({})", sentMessageId);
//        checkAccessToken(accessToken);
//        Integer[] statistics = new Integer[3];
//        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
//        urlParameterMp.put("access_token", accessToken);
//        urlParameterMp.put("ids", sentMessageId);
//        String result = HttpCommonUtil.doGet(HCPI, "statuses/count.json", urlParameterMp);
//        JSONArray resultArray;
//        try {
//            LOGGER.debug("getWeiboStatistics result:" + result);
//            resultArray = new JSONArray(result);
//            for (int i = 0; i < resultArray.length(); i++) {
//                JSONObject counts = resultArray.getJSONObject(i);
//                String commentResult = counts.getString("comments");
//                String shareResult = counts.getString("reposts");
//                // String likeResult = counts.getString("likes");
//                statistics[0] = Integer.parseInt(commentResult);
//                statistics[1] = Integer.parseInt(shareResult);
//                statistics[2] = 0;// like is not supported
//            }
//            LOGGER.debug("getWeiboStatistics END");
//            return statistics;
//        } catch (JSONException e) {
//            HttpCommonUtil.handleJsonException(e, result);
//            throw new SystemException(e);
//        }
//    }
//
//
//    public List<SocialUserInfo> lookupUsersByUids(String[] uids, String accessToken) {
//        List<SocialUserInfo> res = Lists.newArrayList();
//        checkAccessToken(accessToken);
//        for (int i = 0; i < uids.length; i++) {
//            SocialUserInfo socialUserInfo = getSocialUserInfoByUid(uids[i], accessToken);
//            res.add(socialUserInfo);
//        }
//        return res;
//    }
//
//    /**
//     * build weibo SocialUserInfo according to weibo user info map
//     *
//     * @param weiboUserInfoMap
//     * @return SocialUserInfo
//     */
//    private SocialUserInfo buildWeiboUserInfo(Map<String, Object> weiboUserInfoMap) {
//        SocialUserInfo userInfo = new SocialUserInfo();
//        userInfo.setName((String) weiboUserInfoMap.get("name"));
//        userInfo.setScreenName((String) weiboUserInfoMap.get("uid"));
//        userInfo.setDescription((String) weiboUserInfoMap.get("description"));
//        userInfo.setImageUrl((String) weiboUserInfoMap.get("image_url"));
//        userInfo.setHomepageUrl("http://www.weibo.com/u/" + (String) weiboUserInfoMap.get("uid"));
//        userInfo.setWebsite((String) weiboUserInfoMap.get("url"));
//        return userInfo;
//    }
//
//    /**
//     *
//     * @param screenNames
//     * @return
//     */
//
//    public List<SocialUserInfo> getSocialUserInfosByScreenNames(Set<String> screenNames, String accessToken) {
//        List<SocialUserInfo> userInfos = new ArrayList<>();
//        for (String screenName : screenNames) {
//            try {
//                SocialUserInfo userInfo = getSocialUserInfoByScreenName(screenName, accessToken);
//                if (userInfo != null) {
//                    userInfos.add(userInfo);
//                }
//
//            } catch (BusinessException e) {
//                LOGGER.warn("find weibo user {} error {}, skip this one.", screenName, e);
//                continue;
//            }
//        }
//        return userInfos;
//    }
//
//
    public String getAuthorizationUrl() {
        String client_ID = CmtClient.getInstance().getGlobalSettings("Global.Weibo.AppId");
        String redirect_URI = CmtClient.getInstance().getGlobalSettings("Global.Weibo.redirect_URI");
        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
        urlParameterMp.put("client_id", client_ID);
        urlParameterMp.put("redirect_uri", redirect_URI);
        urlParameterMp.put("response_type", "code");
        String url = httpAuthUtil.getUrl( "authorize", urlParameterMp);
        return url;
    }

    
    public Map<String, Object> getAccessToken(String code) {
        LOGGER.info("getAccessToken BEGIN code is ({"+code+"})");
        String client_ID = CmtClient.getInstance().getGlobalSettings("Global.Weibo.AppId");
        String redirect_URI = CmtClient.getInstance().getGlobalSettings("Global.Weibo.redirect_URI");
        String client_Secret = CmtClient.getInstance().getGlobalSettings("Global.Weibo.AppSecret");
        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
        urlParameterMp.put("client_id", client_ID);
        urlParameterMp.put("client_secret", client_Secret);
        urlParameterMp.put("grant_type", "authorization_code");
        urlParameterMp.put("redirect_uri", redirect_URI);
        urlParameterMp.put("code", code);
        String result = httpAuthUtil.doPost( "access_token", urlParameterMp);
        Map<String, Object> token = new HashMap();
        try {
            LOGGER.debug("getAccessToken result {"+result+"}");
            JSONObject resultObj = new JSONObject(result);
            token.put("access_token", resultObj.getString("access_token"));
            token.put("remind_in", resultObj.getString("remind_in"));
            token.put("expires_in", resultObj.getString("expires_in"));
            token.put("uid", resultObj.getString("uid"));
        } catch (JSONException e) {
            throw new SystemException(e);
        }
        LOGGER.debug("getAccessToken END");
        return token;
    }

    private void checkAccessToken(String accessToken) {
        if (StringUtils.isEmpty(accessToken)) {
            throw new BusinessException("AUTH_EXPIRE");
        }
    }

    private static class WeiboErrorHandler implements HttpCommonErrorHandler {
        private void handleErrorMessage(String errorCode, String errmsg) {
            switch (errorCode) {
            case "21315":
            case "21327":
            case "21332":
                throw new BusinessException("ACCESS_TOKEN_EXPIRE");
            case "10022":
            case "10023":
            case "10024":
                throw new BusinessException("OUT_OF_RATE_LIMIT");
            default:
                throw new BusinessException("WEIBO_API_RETURN_ERROR");
            }

        }

        
        public void handleError(String result) {
            try {
                // warn: may have xml api in the future
                if (!result.startsWith("{")) {
                    return;
                }
                JSONObject resultJsonObj = new JSONObject(result);
//                String[] fieldArr = JSONObject.getNames(resultJsonObj);
//                if (ArrayUtils.contains(fieldArr, "error_code")) {
//                    String errorCode = resultJsonObj.getString("error_code");
//                    if (!StringUtils.isEmpty(errorCode)) {
//                        LOGGER.warn("Weibo Server Return Error with result:" + result);
//                        String errmsg = resultJsonObj.getString("error");
//                        handleErrorMessage(errorCode, errmsg);
//                    }
//                }
                return;
            } catch (JSONException e) {
                throw new SystemException(e);
            }

        }

    }

}
