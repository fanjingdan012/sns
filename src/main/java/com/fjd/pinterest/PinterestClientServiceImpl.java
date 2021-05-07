package com.fjd.pinterest;

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

import twitter4j.JSONException;
import twitter4j.JSONObject;


public class PinterestClientServiceImpl  {
	private static final Logger LOGGER = Logger.getLogger(PinterestClientServiceImpl.class);
    private static final String APIURL = "https://api.pinterest.com/";
    private final HttpCommonErrorHandler pinterestErrorHandler = new PinterestErrorHandler();
    private final HttpCommonUtil httpCommonUtil = new HttpCommonUtil("Global.Pinterest.UseMockServer", "pinterest",
            APIURL, pinterestErrorHandler, getPinterestInstagramRequestHeader());

    
    public String getAuthorizationUrl() {
        String appId = CmtClient.getInstance().getGlobalSettings("Global.Pinterest.AppId");
        String redirectUrl = CmtClient.getInstance().getGlobalSettings("Global.Pinterest.redirectUrl");
        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
        urlParameterMp.put("response_type", "code");
        urlParameterMp.put("redirect_uri", redirectUrl);
        urlParameterMp.put("client_id", appId);
        urlParameterMp.put("scope", "read_public,write_public");
        urlParameterMp.put("state", "sapanywhere");
        String url = httpCommonUtil.getUrl("oauth/", urlParameterMp);
        return url;
    }

    
    public String getAccessToken(String code) {
        LOGGER.info("getAccessToken BEGIN code:{}"+ code);
        if (StringUtils.isEmpty(code)) {
            LOGGER.warn("getAccessToken param error, code is {},return empty string");
            return "";
        }
        String token = "";
        String appId = CmtClient.getInstance().getGlobalSettings("Global.Pinterest.AppId");
        String appSecret = CmtClient.getInstance().getGlobalSettings("Global.Pinterest.AppSecret");
        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
        urlParameterMp.put("grant_type", "authorization_code");
        urlParameterMp.put("client_id", appId);
        urlParameterMp.put("client_secret", appSecret);
        urlParameterMp.put("code", code);

        String url = httpCommonUtil.getUrl("v1/oauth/token", urlParameterMp);
        Map<String, String> headerMp = new HashMap<String, String>();
        headerMp.put("Origin", "https://developers.pinterest.com");
        String ret = httpCommonUtil.sendRequest(url, "Post", headerMp, new HashMap(),
                HttpUtil.PARAMETER_TYPE_URLENCODED);
        try {
            JSONObject obj = new JSONObject(ret);
            token = obj.getString("access_token");
            return token;
        } catch (JSONException e) {
            LOGGER.error("Pinterest getAccessToken JSONException json:{}, {}"+ ret+ e);
            throw new SystemException(e);
        }

    }

    // private String fetchMessageString(String msg) {
    // String ret = "";
    // try {
    // JSONObject jsonObj = new JSONObject(msg);
    // if (jsonObj.has("message")) {
    // ret = jsonObj.getString("message");
    // } else {
    // ret = PinterestErrorCode.ERROR_OTHER;
    // }
    // } catch (com.amazonaws.util.json.JSONException e1) {
    // LOGGER.error("getErrorCode Exception", e1);
    // throw new SystemException(e1);
    // }
    // return ret;
    // }
    //
    // private String getErrorMessage(String msg) {
    // String ret = "";
    // if (msg.indexOf("401") != -1) {
    // ret = PinterestErrorCode.ERROR_401;
    // } else if (msg.indexOf("404") != -1) {
    // ret = PinterestErrorCode.ERROR_404;
    // } else if (msg.indexOf("408") != -1) {
    // ret = PinterestErrorCode.ERROR_408;
    // } else if (msg.indexOf("429") != -1) {
    // ret = PinterestErrorCode.ERROR_429;
    // } else if (msg.indexOf("500") != -1 || msg.indexOf("502") != -1 || msg.indexOf("599") != -1) {
    // ret = PinterestErrorCode.ERROR_500;
    // } else {
    // ret = PinterestErrorCode.ERROR_OTHER;
    // }
    // return ret;
    // }
    //
    // private String getErrorCode(String msg) {
    // String ret = "";
    // try {
    // JSONObject jsonObj = new JSONObject(msg);
    // if (jsonObj.has("message")) {
    // String tmp = jsonObj.getString("message");
    // ret = getErrorMessage(tmp);
    // }
    // } catch (com.amazonaws.util.json.JSONException e) {
    // LOGGER.error("getErrorCode error", e);
    // ret = PinterestErrorCode.ERROR_OTHER;
    // }
    // return ret;
    // }

    
//    public Object getUserInfo(String accessToken) {
//        LOGGER.info("getUserInfo BEGIN token:{}", accessToken);
//        checkAccessToken(accessToken);
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        urlParameterMp.put("access_token", accessToken);
//        String ret = httpCommonUtil.doGet("v1/me/", urlParameterMp);
//        Object obj;
//        try {
//            JSONObject jsonObj = new JSONObject(ret);
//            String data = jsonObj.getString("data");
//            JSONObject objdata = new JSONObject(data);
//            obj = objdata;
//
//        } catch (JSONException e) {
//            LOGGER.error("getUserInfo error", e);
//            throw new BusinessException(PinterestErrorCode.ERROR_OTHER);
//        }
//        return obj;
//    }
//
//    
//    public Integer[] getStatistics(String pinID, String accessToken) {
//        LOGGER.info("getStatistics BEGIN pinID:{},accessToken:{}", pinID, accessToken);
//        checkAccessToken(accessToken);
//
//        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
//        urlParameterMp.put("access_token", accessToken);
//        urlParameterMp.put("fields", "counts");
//        String ret = httpCommonUtil.doGet("v1/pins/" + pinID + "/", urlParameterMp);
//
//        Integer commentsCount = 0, repostsCount = 0, likesCount = 0;
//        Integer[] statistics = new Integer[3];
//        try {
//            JSONObject jsonObj = new JSONObject(ret);
//            String data = jsonObj.getString("data");
//            JSONObject objdata = new JSONObject(data);
//            JSONObject obj = objdata.getJSONObject("counts");
//            commentsCount = obj.getInt("comments");
//            repostsCount = obj.getInt("repins");
//            likesCount = obj.getInt("likes");
//            statistics[0] = commentsCount;
//            statistics[1] = repostsCount;
//            statistics[2] = likesCount;
//            return statistics;
//        } catch (com.amazonaws.util.json.JSONException e) {
//            LOGGER.error("getStatistics JSONException {}", e);
//            throw new SystemException(e);
//        }
//
//    }
//
//    
//    public String getBoards(String token) {
//        LOGGER.info("getBoards BEGIN token:{}", token);
//        checkAccessToken(token);
//        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
//        urlParameterMp.put("access_token", token);
//        String ret = httpCommonUtil.doGet("v1/me/boards/", urlParameterMp);
//        return ret;
//    }
//
//    
//    public String createPin(PinterestMessageInfo pin, String token) {
//        LOGGER.info("createPin BEGIN pin:{},token:{}", pin, token);
//        checkAccessToken(token);
//        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
//        urlParameterMp.put("access_token", token);
//        urlParameterMp.put("board", pin.getBoardID());
//        urlParameterMp.put("note", pin.getNote());
//        urlParameterMp.put("link", pin.getLink());
//        urlParameterMp.put("image_url", pin.getImgUrl());
//        String ret = httpCommonUtil.doPost("v1/pins/", urlParameterMp);
//        try {
//            JSONObject jsonobj = new JSONObject(ret);
//            return jsonobj.getJSONObject("data").getString("id");
//        } catch (JSONException e) {
//            LOGGER.error("createPin JSONException", e);
//            throw new SystemException(e);
//        }
//    }



    
    public String searchUserWork(String accountName,String accessToken) {
        LOGGER.info("searchUser BEGIN accountName:{}"+ accountName);
        if (StringUtils.isEmpty(accountName)) {
            throw new BusinessException("PinterestErrorCode.INVALID_ACCOUNT_NAME"+accountName);
        }
        Map<String, String> urlParameterMp = new LinkedHashMap<>();
        urlParameterMp.put("access_token",accessToken);
        urlParameterMp.put("fields", "first_name,id,last_name,url,bio,account_type,created_at,counts,image,username");
        String ret;
        try {
            ret = httpCommonUtil.doGet("v1/users/" + URLEncoder.encode(accountName, "UTF-8") + "/", urlParameterMp);
            return ret;
        } catch (UnsupportedEncodingException e1) {
            LOGGER.error("Pinterest searchUser UnsupportedEncodingException {}", e1);
            throw new SystemException(e1);
        }

    }

    public static Map<String, String> getPinterestInstagramRequestHeader() {
        Map<String, String> headerMp = new HashMap<String, String>();
        headerMp.put("accept", "*/*");
        headerMp.put("connection", "Keep-Alive");
        headerMp.put("user-agent", HttpUtil.USER_AGENT);
        return headerMp;
    }

    /**
     * @param accessToken
     */
//    private void checkAccessToken(String accessToken) {
//        if (StringUtils.isEmpty(accessToken)) {
//            throw new BusinessException(SocialCommonErrorCode.AUTH_EXPIRE, "Pinterest");
//        }
//    }



}

class PinterestErrorHandler implements HttpCommonErrorHandler {
    private static final Logger LOGGER = Logger.getLogger(PinterestErrorHandler.class);

    private void handleErrorMessage(String errorCode, String errmsg) {
        String businessMessage = getErrorMessage(errmsg);
        if (StringUtils.isNotEmpty(businessMessage)) {
            throw new BusinessException(businessMessage);
        } else {
            throw new BusinessException("SocialCommonErrorCode.SOCIAL_API_RETURN_ERROR"+ "Pinterest"+ errorCode+ errmsg);
        }

    }

    private String getErrorMessage(String msg) {
        String ret = "";
        if (msg.indexOf("401") != -1) {
            ret = "PinterestErrorCode.ERROR_401";
        } else if (msg.indexOf("404") != -1) {
            ret = "PinterestErrorCode.ERROR_404";
        } else if (msg.indexOf("408") != -1) {
            ret = "PinterestErrorCode.ERROR_408";
        } else if (msg.indexOf("429") != -1) {
            ret = "PinterestErrorCode.ERROR_429";
        } else if (msg.indexOf("500") != -1 || msg.indexOf("502") != -1 || msg.indexOf("599") != -1) {
            ret = "PinterestErrorCode.ERROR_500";
        }
        return ret;
    }

    
    public void handleError(String result) {
        try {
            if (!result.startsWith("{")) {
                return;
            }
            JSONObject resultJsonObj = new JSONObject(result);
//            String[] fieldArr = JSONObject.getNames(resultJsonObj);
//            if (ArrayUtils.contains(fieldArr, "message") && ArrayUtils.contains(fieldArr, "type")
//                    && fieldArr.length == 2) {
//                String type = resultJsonObj.getString("type");
//                if (!StringUtils.isEmpty(type)) {
//                    LOGGER.warn("Pinterest Server Return Error with result:" + result);
//                    String errmsg = resultJsonObj.getString("message");
//                    handleErrorMessage(type, errmsg);
//                }
//            }
            return;
        } catch (JSONException e) {
            LOGGER.error("PinterestServiceImpl: Covert from JSONString To Obj Error with result:" + result, e);
            throw new SystemException(e);
        }

    }
}