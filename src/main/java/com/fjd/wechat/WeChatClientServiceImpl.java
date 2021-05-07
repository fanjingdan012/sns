package com.fjd.wechat;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;




public class WeChatClientServiceImpl  {

    public static final String BASE_URL = "https://api.weixin.qq.com/";
    public static final String BASE_URL_COMPONENT = "https://api.weixin.qq.com/cgi-bin/component/";
    public final static String callbackURIFormatterString = "callback/%s/%s";
    private static final HttpCommonErrorHandler ERROR_HANDLER = new WeChatErrorHandler();
    private static final HttpCommonUtil httpCommonUtil = new HttpCommonUtil("Global.WeChat.UseMockServer", "wechat",
            BASE_URL, ERROR_HANDLER);
//    private static final HttpCommonParamInfo HCPI_COMPONENT = new HttpCommonParamInfo("Global.WeChat.UseMockServer",
//            "wechatComponent", BASE_URL_COMPONENT, ERROR_HANDLER);
//    @Autowired
//    private WeChatInnerService weChatInnerService;

    // @Autowired
    // private SldServiceForSU sldService;

    private static final Logger LOGGER = Logger.getLogger(WeChatClientServiceImpl.class);

    // for eshop wechat, would get javax.net.ssl.SSLPeerUnverifiedException:
    // Host name
    // 'attachment-e2e-soul.s3.ap-northeast-2.amazonaws.com' does not match the
    // certificate subject provided by the peer
    // (CN=*.s3.ap-northeast-2.amazonaws.com, O=Amazon.com Inc., L=Seattle,
    // ST=Washington, C=US)
    // deprecated and not used now. remove it for security issue
    // 
    // public File downloadCoverImageFromAwsStorage(String url) {
    // HttpUtil httpUtil = new HttpUtil();
    // List<String> acceptableFileType = new ArrayList<>();
    // acceptableFileType.add("png");
    // acceptableFileType.add("jpeg");
    // File file;
    // try {
    // file = httpUtil.downLoadFile(url, null, null, "wechat",
    // acceptableFileType);
    // } catch (IOException e) {
    // throw new SystemException(e);
    // } catch (FileNotSupportedException e) {
    // LOGGER.warn("downloadCoverImageFromAwsStorage FileNotSupportedException
    // {}", e);
    // throw new BusinessException("COVERIMAGE_TYPE_ERROR");
    // }
    // return file;
    //
    // }

//    public List<String> getUserList(Long accountId) {
//        String urlPattern = "/cgi-bin/user/get";
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        urlParameterMp.clear();
//        String accessToken = getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        parameterMp.clear();
//        String result = tryWeChatApi(urlPattern, "Get", urlParameterMp, parameterMp, "json", null, null, accountId);
//        String newAccessToken = getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", newAccessToken);
//        LOGGER.debug("wechat get user list:" + result);
//        JSONObject resultJsonObj;
//        List<String> openIds = new ArrayList<>();
//        try {
//            resultJsonObj = new JSONObject(result);
//            String[] fieldArr = JSONObject.getNames(resultJsonObj);
//            Long total = resultJsonObj.getLong("total");
//            Long count = resultJsonObj.getLong("count");
//            if (total == 0L) {
//                return openIds;
//            }
//            JSONArray openIdList = resultJsonObj.getJSONObject("data").getJSONArray("openid");
//            for (int i = 0; i < count; i++) {
//                String openId = (String) openIdList.get(i);
//                openIds.add(openId);
//            }
//            String nextOpenIdParamName = "next_openid";
//            String nextOpenId = resultJsonObj.getString(nextOpenIdParamName);
//            while (total > openIds.size()) {
//                urlParameterMp.put(nextOpenIdParamName, nextOpenId);
//                result = tryWeChatApi(urlPattern, "Get", urlParameterMp, parameterMp, "json", null, null, accountId);
//                LOGGER.info("wechat get user list:" + result);
//                resultJsonObj = new JSONObject(result);
//                fieldArr = JSONObject.getNames(resultJsonObj);
//                count = resultJsonObj.getLong("count");
//                openIdList = resultJsonObj.getJSONObject("data").getJSONArray("openid");
//                for (int i = 0; i < count; i++) {
//                    String openId = (String) openIdList.get(i);
//                    openIds.add(openId);
//                }
//                nextOpenId = resultJsonObj.getString(nextOpenIdParamName);
//            }
//            return openIds;
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//    }
//
//
//    public void sendMessageByOpenId(String msgType, String content, List<String> openIds, Long accountId) {
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = this.getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        List<String> touser = new ArrayList<>();
//        Map<String, String> mediaMp = new HashMap<>();
//        switch (msgType) {
//        case "text":
//            mediaMp.put("content", content);
//            break;
//        case "wxcard":
//            mediaMp.put("card_id", content);
//            break;
//        default:
//            mediaMp.put("media_id", content);
//            break;
//        }
//        touser.addAll(openIds);
//        parameterMp.put(msgType, mediaMp);
//        parameterMp.put("msgtype", msgType);
//        parameterMp.put("touser", touser);
//        tryWeChatApi("cgi-bin/message/mass/send", "Post", urlParameterMp, parameterMp, "json", null, null, accountId);
//        return;
//    }
//
//
//    public String getUserInfo(String openId, Long accountId) {
//        Map<String, Object> parameterMp = new HashMap<>();
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        String accessToken = this.getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        urlParameterMp.put("openid", openId);
//        urlParameterMp.put("lang", "zh_CN");
//        String result = tryWeChatApi("cgi-bin/user/info", "Get", urlParameterMp, parameterMp, "json", null, null,
//                accountId);
//        return result;
//    }
//
//
//    public void sendMessageByGroup(String groupId, String messageType, String content, Long accountId) {
//        Map<String, Object> parameterMp = new HashMap<>();
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        String accessToken = this.getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        Map<String, Object> filterMp = new HashMap<>();
//        if (StringUtils.isEmpty(groupId)) {
//            filterMp.put("is_to_all", true);
//        } else {
//            filterMp.put("is_to_all", false);
//            filterMp.put("group_id", groupId);
//        }
//        Map<String, Object> contentMp = new HashMap<>();
//        if ("text".equals(messageType)) {
//            contentMp.put("content", content);
//        } else {
//            contentMp.put("media_id", content);
//        }
//        parameterMp.put("filter", filterMp);
//        parameterMp.put(messageType, contentMp);
//        parameterMp.put("msgtype", messageType);
//        tryWeChatApi("cgi-bin/message/mass/sendall", "Post", urlParameterMp, parameterMp, "json", null, null,
//                accountId);
//        return;
//    }
//
//
//    public String uploadMedia(File file, String mediaType, Long accountId) {
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        urlParameterMp.put("type", mediaType);
//        parameterMp.put("media", file);
//        String result = tryWeChatApi("cgi-bin/media/upload", "Post", urlParameterMp, parameterMp,
//                HttpUtil.PARAMETER_TYPE_MULTIPART, null, null, accountId);
//        JSONObject jsonObj;
//        try {
//            jsonObj = new JSONObject(result);
//            String mediaId = jsonObj.getString("media_id");
//            LOGGER.debug("uploadMedia END. media_id:({})", mediaId);
//            return mediaId;
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//    }




    
    public String getAccessToken(String appId, String appSecret) {
        LOGGER.info("getAccessToken BEGIN");
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(appSecret)) {
            LOGGER.warn("getAccessToken: arg invalid:appid is ({"+appId+"}),appSecret is ({"+appSecret+"})");
            throw new BusinessException("INVALID_APPID_OR_SECRET");
        }
        Map<String, String> urlParameterMp = new LinkedHashMap<>();
        urlParameterMp.put("grant_type", "client_credential");
        urlParameterMp.put("appid", appId);
        urlParameterMp.put("secret", appSecret);
        String result = httpCommonUtil.doGet( "cgi-bin/token", urlParameterMp);
        try {
            JSONObject resultJsonObj = new JSONObject(result);
            return resultJsonObj.getString("access_token");
        } catch (JSONException e) {
            throw new SystemException(e);
        }

    }

//    private String showQrcodeUrl(String ticket) {
//        return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket;
//    }

    
//    public String createWeChatCardQrCode(String cardId) {
//        String appId = CmtClient.getInstance().getGlobalSettings("Global.WeChat.AppId");
//        String appSecret = CmtClient.getInstance().getGlobalSettings("Global.WeChat.AppSecret");
//        String accessToken = CmtClient.getInstance().getGlobalSettings("Global.WeChat.AccessToken");
//        return createWeChatCardQrCode(cardId, accessToken, appId, appSecret, null);
//    }
//
//
//    public String createWeChatCardQrCode(String cardId, Long accountId) {
//        String accessToken = this.getAccessTokenByAccount(accountId);
//        return createWeChatCardQrCode(cardId, accessToken, null, null, accountId);
//    }
//
//    private String createWeChatCardQrCode(String cardId, String accessToken, String appId, String appSecret,
//            Long accountId) {
//        LOGGER.info("createWeChatCardQrCode BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        urlParameterMp.put("access_token", accessToken);
//        Map<String, Object> actionInfoMap = new HashMap<>();
//        Map<String, Object> cardMap = new HashMap<>();
//        parameterMp.put("action_name", "QR_CARD");
//        parameterMp.put("action_info", actionInfoMap);
//        actionInfoMap.put("card", cardMap);
//        cardMap.put("card_id", cardId);
//        String result = tryWeChatApi("card/qrcode/create", "Post", urlParameterMp, parameterMp, "json", null, null,
//                accountId);
//        JSONObject jsonObj;
//        try {
//            jsonObj = new JSONObject(result);
//            String ticket = jsonObj.getString("ticket");
//            LOGGER.debug("createWeChatCardQrCode END ticket:" + ticket);
//            return showQrcodeUrl(ticket);
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//    }
//
//
//    public Boolean checkWeChatCardConsume(String cardId, String code) {
//        LOGGER.info("checkWeChatCardConsume BEGIN");
//        String appId = CmtClient.getInstance().getGlobalSettings("Global.WeChat.AppId");
//        String appSecret = CmtClient.getInstance().getGlobalSettings("Global.WeChat.AppSecret");
//        String accessToken = CmtClient.getInstance().getGlobalSettings("Global.WeChat.AccessToken");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        urlParameterMp.put("access_token", accessToken);
//        parameterMp.put("card_id", cardId);
//        parameterMp.put("code", code);
//        parameterMp.put("check_consume", true);
//        tryWeChatApi("card/code/get", "Post", urlParameterMp, parameterMp, "json", appId, appSecret, null);
//        LOGGER.debug("checkWeChatCardConsume END");
//        return true;
//    }
//
//
//    public String consumeWeChatCard(String cardId, String code) {
//        LOGGER.info("consumeWeChatCard BEGIN");
//        String appId = CmtClient.getInstance().getGlobalSettings("Global.WeChat.AppId");
//        String appSecret = CmtClient.getInstance().getGlobalSettings("Global.WeChat.AppSecret");
//        String accessToken = CmtClient.getInstance().getGlobalSettings("Global.WeChat.AccessToken");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        urlParameterMp.put("access_token", accessToken);
//        parameterMp.put("code", code);
//        String result = tryWeChatApi("card/code/consume", "Post", urlParameterMp, parameterMp, "json", appId, appSecret,
//                null);
//        JSONObject jsonObj;
//        try {
//            jsonObj = new JSONObject(result);
//            String openId = jsonObj.optString("openid");
//            LOGGER.debug("consumeWeChatCard END openId:{}", openId);
//            return openId;
//        } catch (JSONException e) {
//            HttpCommonUtil.handleJsonException(e, result);
//            throw new SystemException(e);
//        }
//
//    }

    
//    public void activateMemberCard(String code, String cardId, Long accountId, Integer initBonus, Integer initBalance,
//            String membershipNumber) {
//        LOGGER.info("activateMemberCard BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = this.getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        parameterMp.put("init_bonus", initBonus);
//        parameterMp.put("init_balance", initBalance);
//        parameterMp.put("membership_number", membershipNumber);
//        parameterMp.put("code", code);
//        parameterMp.put("card_id", cardId);
//        tryWeChatApi("card/membercard/activate", "Post", urlParameterMp, parameterMp, "json", null, null, accountId);
//        LOGGER.debug("activateMemberCard END");
//    }
//
//
//    public void setMemberCardActivateUserFormFields(String cardId, Long accountId) {
//        LOGGER.info("setMemberCardActivateUserFormFields BEGIN");
//        List<String> requiredCommonFields = new ArrayList<>();
//        requiredCommonFields.add("USER_FORM_INFO_FLAG_MOBILE");
//        requiredCommonFields.add("USER_FORM_INFO_FLAG_NAME");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = this.getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        parameterMp.put("card_id", cardId);
//        Map<String, Object> common_field_id_listMap = new HashMap<>();
//        common_field_id_listMap.put("common_field_id_list", requiredCommonFields);
//        parameterMp.put("required_form", common_field_id_listMap);
//        tryWeChatApi("card/membercard/activateuserform/set", "Post", urlParameterMp, parameterMp, "json", null, null,
//                accountId);
//        LOGGER.debug("setMemberCardActivateUserFormFields END");
//    }

    
//    public WeChatMemberCardUserInfoModel getMembercardUserInfo(String code, String cardId, Long accountId) {
//        LOGGER.info("getMembercardUserInfo BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = this.getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        parameterMp.put("card_id", cardId);
//        parameterMp.put("code", code);
//        String result = tryWeChatApi("card/membercard/userinfo/get", "Post", urlParameterMp, parameterMp, "json", null,
//                null, accountId);
//        JSONObject jsonObj;
//        try {
//            jsonObj = new JSONObject(result);
//            WeChatMemberCardUserInfoModel userInfo = new WeChatMemberCardUserInfoModel();
//            userInfo.setOpenId(jsonObj.optString("openid"));
//            userInfo.setNickName(jsonObj.optString("nickname"));
//            JSONArray common_field_list = jsonObj.optJSONObject("user_info").optJSONArray("common_field_list");
//            for (int i = 0; i < common_field_list.length(); i++) {
//                JSONObject commonField = common_field_list.getJSONObject(i);
//                switch (commonField.optString("name")) {
//                case "USER_FORM_INFO_FLAG_MOBILE":
//                    userInfo.setMobile(commonField.optString("value"));
//                    break;
//                case "USER_FORM_INFO_FLAG_NAME":
//                    userInfo.setName(commonField.optString("value"));
//                    break;
//                default:
//                    break;
//                }
//            }
//            LOGGER.debug("getMembercardUserInfo END");
//            return userInfo;
//        } catch (JSONException e) {
//            HttpCommonUtil.handleJsonException(e, result);
//            throw new SystemException(e);
//        }
//    }

//    public String getComponentAccessToken(String appId, String appSecret, String component_verify_ticket) {
//        Map<String, Object> parameterMp = new HashMap<>();
//        parameterMp.put("component_appid", appId);
//        parameterMp.put("component_appsecret", appSecret);
//
//        String result = HttpCommonUtil.doPost(HCPI_COMPONENT, "api_component_token", new HashMap<>(), parameterMp);
//        try {
//            JSONObject jsonObj = new JSONObject(result);
//            String accessToken = jsonObj.getString("component_access_token");
//            return accessToken;
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//    }
//
//    public String checkCardAgentQuolification(String accessToken) {
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        urlParameterMp.put("access_token", accessToken);
//        String result = HttpCommonUtil.doGet(HCPI_COMPONENT, "check_card_agent_qualification", urlParameterMp);
//        return result;
//    }
//
//    public String uploadCardAgentQuolification(String register_capital, String business_license_media_id,
//            String tax_registration_certificate_media_id, String last_quarter_tax_listing_media_id,
//            String accessToken) {
//        LOGGER.info("uploadCardAgentQuolification BEGIN");
//        Map<String, Object> parameterMp = new HashMap<>();
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//
//        urlParameterMp.clear();
//        urlParameterMp.put("access_token", accessToken);
//        parameterMp.clear();
//        parameterMp.put("register_capital", register_capital);
//        parameterMp.put("business_license_media_id", business_license_media_id);
//        parameterMp.put("tax_registration_certificate_media_id", tax_registration_certificate_media_id);
//        parameterMp.put("last_quarter_tax_listing_media_id", last_quarter_tax_listing_media_id);
//        String result = HttpCommonUtil.doPost(HCPI_COMPONENT, "upload_card_agent_qualification", urlParameterMp,
//                parameterMp);
//        LOGGER.debug("uploadCardAgentQuolification END");
//        return result;
//    }
//
//
//    public String createCard(String cardJsonString) {
//        String appId = CmtClient.getInstance().getGlobalSettings("Global.WeChat.AppId");
//        String appSecret = CmtClient.getInstance().getGlobalSettings("Global.WeChat.AppSecret");
//        String accessToken = CmtClient.getInstance().getGlobalSettings("Global.WeChat.AccessToken");
//        return createCard(cardJsonString, accessToken, appId, appSecret, null);
//
//    }

    
//    public String createCard(String cardJsonString, Long accountId) {
//        String accessToken = this.getAccessTokenByAccount(accountId);
//        return createCard(cardJsonString, accessToken, null, null, accountId);
//    }
//
//    private String createCard(String cardJsonString, String accessToken, String appId, String appSecret,
//            Long accountId) {
//        LOGGER.info("createCard BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        urlParameterMp.clear();
//        urlParameterMp.put("access_token", accessToken);
//        String result = tryWeChatApi("card/create", "Post", urlParameterMp, cardJsonString, null, null, accountId);
//        JSONObject jsonObj;
//        try {
//            jsonObj = new JSONObject(result);
//            String cardId = jsonObj.getString("card_id");
//            LOGGER.debug("getCardHtml END. cardId is {}", cardId);
//            return cardId;
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//    }
//
//
//    public String getCardHtml(Long accountId, String cardId) {
//        LOGGER.info("getCardHtml BEGIN");
//        Map<String, Object> parameterMp = new HashMap<>();
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        String accessToken = this.getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        parameterMp.put("card_id", cardId);
//        String result = tryWeChatApi("card/mpnews/gethtml", "Post", urlParameterMp, parameterMp, "json", null, null,
//                accountId);
//        LOGGER.debug("getCardHtml END. result is {}", result);
//        return result;
//    }
//
//
//    public void setCardWhiteList(List<String> wxnames, Long accountId) {
//        LOGGER.info("setCardWhiteList BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        urlParameterMp.put("access_token", getAccessTokenByAccount(accountId));
//        parameterMp.put("username", wxnames);
//        tryWeChatApi("card/testwhitelist/set", "Post", urlParameterMp, parameterMp, "json", null, null, accountId);
//        LOGGER.debug("setCardWhiteList END");
//        return;
//    }
//
//
//    public String getCardInfo(String cardId, Long accountId) {
//        LOGGER.info("getCardInfo BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        urlParameterMp.put("access_token", getAccessTokenByAccount(accountId));
//        parameterMp.put("card_id", cardId);
//        String result = tryWeChatApi("card/get", "Post", urlParameterMp, parameterMp, "json", null, null, accountId);
//        LOGGER.debug("getCardInfo END");
//        return result;
//    }
//
//
//    public Boolean updateCardInfo(String cardJsonString, Long accountId) {
//        LOGGER.info("updateCardInfo BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
//        urlParameterMp.put("access_token", getAccessTokenByAccount(accountId));
//        String result = tryWeChatApi("card/update", "Post", urlParameterMp, cardJsonString, null, null, accountId);
//        JSONObject jsonObj;
//        try {
//            jsonObj = new JSONObject(result);
//            String sendCheck = jsonObj.getString("send_check");
//            LOGGER.debug("updateCardInfo END. send check is {}", sendCheck);
//
//            return Boolean.valueOf(sendCheck);
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//    }
//
//
//    public void modifyCardStock(Long increaseStockValue, Long reduceStockValue, String cardId, Long accountId) {
//        LOGGER.info("modifyCardStock BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        urlParameterMp.put("access_token", getAccessTokenByAccount(accountId));
//        parameterMp.put("card_id", cardId);
//        parameterMp.put("increase_stock_value", increaseStockValue);
//        parameterMp.put("reduce_stock_value", reduceStockValue);
//        tryWeChatApi("card/modifystock", "Post", urlParameterMp, parameterMp, "json", null, null, accountId);
//        LOGGER.debug("modifyCardStock END.");
//    }
//
//
//    public String updateMemberCardUserPoints(String cardId, String code, Long addBonus, String recordBonus,
//            Long accountId) {
//        LOGGER.info("updateMemberCardUserPoints BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = this.getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        parameterMp.put("card_id", cardId);
//        parameterMp.put("code", code);
//        parameterMp.put("record_bonus", recordBonus);
//        parameterMp.put("add_bonus", addBonus.intValue());
//        String result = tryWeChatApi("card/membercard/updateuser", "Post", urlParameterMp, parameterMp, "json", null,
//                null, accountId);
//        JSONObject jsonObj;
//        try {
//            jsonObj = new JSONObject(result);
//            String resultBonus = jsonObj.optString("result_bonus");
//            LOGGER.debug("updateMemberCardUserPoints END, result bonus:{}", resultBonus);
//            return resultBonus;
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//    }
//
//
//    public String updateMemberCardUserPointsAll(String cardId, String code, Long bonus, String recordBonus,
//            Long accountId) {
//        LOGGER.info("updateMemberCardUserPoints BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = this.getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        parameterMp.put("card_id", cardId);
//        parameterMp.put("code", code);
//        parameterMp.put("record_bonus", recordBonus);
//        parameterMp.put("bonus", bonus.intValue());
//        String result = tryWeChatApi("card/membercard/updateuser", "Post", urlParameterMp, parameterMp, "json", null,
//                null, accountId);
//        JSONObject jsonObj;
//        try {
//            jsonObj = new JSONObject(result);
//            String resultBonus = jsonObj.optString("result_bonus");
//            LOGGER.debug("updateMemberCardUserPoints END, result bonus:{}", resultBonus);
//            return resultBonus;
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//    }

    /**
     *
     * @param urlPattern
     * @param method
     * @param urlParameterMp
     * @param parameter
     * @param parameterType
     * @param appId
     *            if use WeChatAccount, pass null
     * @param appSecret
     *            if use WeChatAccount, pass null
     * @param accountId
     *            if no WeChatAccount, pass null
     * @return result from HttpUtil(succeed result only)
     */
    private String tryWeChatApi(String urlPattern, String method, Map<String, String> urlParameterMp,
            Map<String, Object> parameter, String parameterType, String appId, String appSecret, Long accountId) {
        LOGGER.info("tryWeChatApi BEGIN");
        String result;
        String url = httpCommonUtil.getUrl( urlPattern, urlParameterMp);
        Map<String, String> headerMp = new HashMap<>();
        try {
            result = httpCommonUtil.sendRequest( url, method, headerMp, parameter, parameterType);
        } catch (BusinessException e) {
            if ("ACCESS_TOKEN_EXPIRE".equals(e.getDescription())) {
                LOGGER.debug("tryWeChatApi: access toke invalid will get new one and try again.");
                String newAccessToken;

                newAccessToken = getAccessToken(appId, appSecret);

                urlParameterMp.put("access_token", newAccessToken);
                url = httpCommonUtil.getUrl( urlPattern, urlParameterMp);
                result = httpCommonUtil.sendRequest( url, method, headerMp, parameter, parameterType);
            } else {
                throw e;
            }
        }
        LOGGER.debug("tryWeChatApi END. result:{"+result+"}");
        return result;
    }

    /**
     * support json parameter
     *
     * @param urlPattern
     * @param method
     * @param urlParameterMp
     * @param json
     * @param appId
     * @param appSecret
     * @param accountId
     * @return result from HttpUtil(succeed result only)
     */
    private String tryWeChatApi(String urlPattern, String method, Map<String, String> urlParameterMp, String json,
            String appId, String appSecret, Long accountId) {
        LOGGER.info("tryWeChatApi BEGIN");
        String result;
        String url = httpCommonUtil.getUrl( urlPattern, urlParameterMp);

        try {
            result = httpCommonUtil.sendJsonRequest( url, method, json);
        } catch (BusinessException e) {
            if ("ACCESS_TOKEN_EXPIRE".equals(e.getDescription())) {
                LOGGER.debug("tryWeChatApi: access toke invalid will get new one and try again.");
                String newAccessToken;
                newAccessToken = getAccessToken(appId, appSecret);
                urlParameterMp.put("access_token", newAccessToken);
                url = httpCommonUtil.getUrl( urlPattern, urlParameterMp);
                return httpCommonUtil.sendJsonRequest( url, method, json);
            } else {
                throw e;
            }
        }
        LOGGER.debug("tryWeChatApi END. result:{"+result+"}");
        return result;
    }

//
//    public void preview(String msgType, String content, String wxname, Long accountId) {
//        LOGGER.info("preview BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = this.getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        parameterMp.clear();
//        parameterMp.put("towxname", wxname);
//        parameterMp.put("msgtype", msgType);
//        Map<String, String> mediaMp = new HashMap<>();
//        switch (msgType) {
//        case "text":
//            mediaMp.put("content", content);
//            break;
//        case "wxcard":
//            mediaMp.put("card_id", content);
//            break;
//        default:
//            mediaMp.put("media_id", content);
//            break;
//        }
//        parameterMp.put(msgType, mediaMp);
//        String result = tryWeChatApi("cgi-bin/message/mass/preview", "Post", urlParameterMp, parameterMp, "json", null,
//                null, accountId);
//        LOGGER.debug("preview END. result:", result);
//        return;
//    }
//
//
//    public String uploadMpnews(List<Map<String, String>> articles, Long accountId) {
//        LOGGER.info("uploadMpnews BEGIN");
//        Map<String, Object> parameterMp = new HashMap<>();
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        parameterMp.put("articles", articles);
//        String accessToken = getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        String result = tryWeChatApi("cgi-bin/media/uploadnews", "Post", urlParameterMp, parameterMp, "json", null,
//                null, accountId);
//        try {
//            JSONObject resultJsonObj = new JSONObject(result);
//            String mediaId = resultJsonObj.getString("media_id");
//            LOGGER.debug("uploadMpnews END, mediaId:{}", mediaId);
//            return mediaId;
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//    }
//
//
//    public String uploadPermanentImage(File file, Long accountId) {
//        LOGGER.info("uploadPermanentImage BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        parameterMp.put("media", file);
//        String result = tryWeChatApi("cgi-bin/media/uploadimg", "Post", urlParameterMp, parameterMp,
//                HttpUtil.PARAMETER_TYPE_MULTIPART, null, null, accountId);
//        JSONObject jsonObj;
//        try {
//            jsonObj = new JSONObject(result);
//            String mediaId = jsonObj.getString("url");
//            LOGGER.debug("uploadPermanentImage END. url:({})", mediaId);
//            return mediaId;
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//    }
//
//
//    public Integer uploadCardCode(String cardId, List<String> codes, Long accountId) {
//        LOGGER.info("uploadCardCode BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        parameterMp.put("card_id", cardId);
//        parameterMp.put("code", codes);
//        String result = tryWeChatApi("card/code/deposit", "Post", urlParameterMp, parameterMp, "json", null, null,
//                accountId);
//        JSONObject jsonObj;
//        try {
//            jsonObj = new JSONObject(result);
//            Integer succeedCount = jsonObj.optInt("succ_code");
//            LOGGER.debug("uploadCardCode END. succeedCount:({})", succeedCount);
//            return succeedCount;
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//    }
//
//
//    public List<String> checkCardCodeNotUploaded(String cardId, List<String> codes, Long accountId) {
//        LOGGER.info("checkUploadedCardCode BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        parameterMp.put("card_id", cardId);
//        parameterMp.put("code", codes);
//        String result = tryWeChatApi("card/code/checkcode", "Post", urlParameterMp, parameterMp, "json", null, null,
//                accountId);
//        JSONObject jsonObj;
//        try {
//            jsonObj = new JSONObject(result);
//            JSONArray notExistCodes = jsonObj.optJSONArray("not_exist_code");
//            LOGGER.debug("checkUploadedCardCode END. notExistCodes:({})", notExistCodes);
//            List<String> notExistCodesList = new ArrayList<>();
//            if (notExistCodes.length() != 0) {
//                int length = notExistCodes.length();
//                for (int i = 0; i < length; i++) {
//                    notExistCodesList.add(notExistCodes.getString(i));
//                }
//            }
//            return notExistCodesList;
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//    }
//
//
//    @BusinessServiceAction(name = "validateServerUrl", sideEffecting = false, params = { "signature", "echostr",
//            "timestamp", "nonce", "encrypt_type", "msg_signature" }, requireFullOn = { "Campaign" })
//    public String validateServerUrl(String signature, String echostr, String timestamp, String nonce,
//            String encrypt_type, String msg_signature) {
//        LOGGER.info("validateServerUrl BEGIN");
//        String error = "error";
//        if (isEncrypt(encrypt_type)) {
//            WXBizMsgCrypt pc = getWXBizMsgCrypt();
//            if (pc == null) {
//                return error;
//            }
//            error = pc.verifyUrl(msg_signature, timestamp, nonce, echostr);
//            return error;
//        } else {
//            WeChatAccount account = weChatInnerService.getActiveWeChatAccount();
//            if (account == null) {
//                LOGGER.warn("validateServerUrl END. WeChatAccount does not exist, return null");
//                return error;
//            }
//            String token = account.getServerToken();
//            if (validateSignature(token, timestamp, nonce, signature)) {
//                return echostr;
//            } else {
//                return error;
//            }
//
//        }
//
//    }
//
//    public Boolean validateSignature(String token, String timestamp, String nonce, String signature) {
//        String encodedStr = getSignature(token, timestamp, nonce);
//        if (signature.equalsIgnoreCase(encodedStr)) {
//            return true;
//        } else {
//            return false;
//        }
//
//    }
//
//    private String getSHA1(String string) {
//        MessageDigest md;
//        try {
//            md = MessageDigest.getInstance("SHA-1");
//            md.update(string.getBytes());
//            byte[] digest = md.digest();
//
//            StringBuffer hexstr = new StringBuffer();
//            String shaHex = "";
//            for (int i = 0; i < digest.length; i++) {
//                shaHex = Integer.toHexString(digest[i] & 0xFF);
//                if (shaHex.length() < 2) {
//                    hexstr.append(0);
//                }
//                hexstr.append(shaHex);
//            }
//            return hexstr.toString();
//        } catch (Exception e) {
//            LOGGER.warn("getSHA1: Exception happens {}", e);
//            throw new BusinessException("ComputeSignatureError");
//        }
//
//    }
//
//    private String getSignature(String token, String timestamp, String nonce) {
//        String[] array = new String[] { token, timestamp, nonce };
//        StringBuffer sb = new StringBuffer();
//        Arrays.sort(array);
//        for (int i = 0; i < 3; i++) {
//            sb.append(array[i]);
//        }
//        String str = sb.toString();
//        return getSHA1(str);
//    }

    /*
     * private void checkSignature(String signature, String echostr, String
     * timestamp, String nonce) { $signature = $_GET["signature"]; $timestamp =
     * $_GET["timestamp"]; $nonce = $_GET["nonce"];
     *
     * $token = TOKEN; $tmpArr = array($token, $timestamp, $nonce);
     * sort($tmpArr, SORT_STRING); $tmpStr = implode( $tmpArr ); $tmpStr = sha1(
     * $tmpStr );
     *
     * if( $tmpStr == $signature ){ return true; }else{ return false; } }
     */

//    private Boolean isEncrypt(String encryptType) {
//        return (encryptType != null && encryptType.equals("aes"));
//    }
//
//    private WXBizMsgCrypt getWXBizMsgCrypt() {
//        LOGGER.info("getWXBizMsgCrypt BEGIN.");
//        WeChatAccount account = weChatInnerService.getActiveWeChatAccount();
//        if (account == null) {
//            LOGGER.warn("getWXBizMsgCrypt END. WeChatAccount does not exist, return null");
//            return null;
//        }
//        String token = account.getServerToken();
//        String encodingAesKey = account.getServerAESKey();
//        String appId = account.getAppId();
//        WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
//        LOGGER.debug("getWXBizMsgCrypt END.");
//        return pc;
//    }
//
//
//    public Map<String, String> extractRequest(String payload, String timestamp, String nonce, String signature,
//            String msgSignature, String encryptType) {
//        Map<String, String> requestMap;
//        WeChatAccount account = weChatInnerService.getActiveWeChatAccount();
//        if (account == null) {
//            LOGGER.warn("validateServerUrl END. WeChatAccount does not exist, return empty map");
//            return new HashMap<>();
//        }
//        try {
//            requestMap = WeChatUtil.parseXml(payload);
//            if (isEncrypt(encryptType)) {
//                WXBizMsgCrypt pc = getWXBizMsgCrypt();
//                if (pc == null) {
//                    return new HashMap<>();
//                }
//                String decryptedXml = pc.decryptMsg(msgSignature, timestamp, nonce, payload);
//                Map<String, String> requestMap1 = WeChatUtil.parseXml(decryptedXml);
//                requestMap.putAll(requestMap1);
//            } else {
//                validateSignature(account.getServerToken(), timestamp, nonce, signature);
//            }
//        } catch (Exception e) {
//            LOGGER.warn("extractRequest: exception happens {}", e);
//            throw new BusinessException("INVALID_WECHAT_MESSAGE");
//        }
//        return requestMap;
//
//    }

//
//    public String getApiGatewayWeChatServerUrl() {
//        // TODO MSA cmt adopt
//        // Integer tenantId = (int)
//        // this.boFacade.getCurrentUser().getTenantId();
//        // String tenantCode = sldService.getTenantInfo(tenantId).getDBName();
//        // if (tenantCode == null) {
//        // LOGGER.error("getApiGatewayWeChatServerUrl: cannot get tenantCode
//        // from csm");
//        // return null;
//        // }
//        // String apiGatewayUrl =
//        // sldService.getServiceExternalUrlByTypeAndName("APIGATEWAY",
//        // "api-gateway");
//        // if (apiGatewayUrl == null) {
//        // LOGGER.error("getApiGatewayWeChatServerUrl: cannot get api gateway
//        // url from csm");
//        // return null;
//        // }
//        // if (!apiGatewayUrl.endsWith("/") && !apiGatewayUrl.endsWith("\\")) {
//        // apiGatewayUrl = apiGatewayUrl + "/";
//        // }
//        // String weChatUrl = apiGatewayUrl
//        // + String.format(WeChatConstant.callbackURIFormatterString, "wechat",
//        // tenantCode);
//        // weChatUrl.replaceAll(":443", "");
//        return "";// weChatUrl;
//    }
//
//
//    public Integer shakeAroundAccountAuditStatus(Long accountId) {
//        LOGGER.info("shakerAroundDeviceApplyId BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        String accessToken = getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        String result = tryWeChatApi("shakearound/account/auditstatus", "Get", urlParameterMp, null, "json", null, null,
//                accountId);
//        JSONObject jsonObj;
//        try {
//            jsonObj = new JSONObject(result);
//            JSONObject dataObj = jsonObj.optJSONObject("data");
//            Integer auditStatus = dataObj.optInt("audit_status");
//            return auditStatus;
//        } catch (JSONException e) {
//            HttpCommonUtil.handleJsonException(e, result);
//            throw new SystemException(e);
//        }
//    }
//
//
//    public Integer shakeAroundDeviceApplyId(Integer quantity, String applyReason, String comment, Integer poiId,
//            Long accountId) {
//        LOGGER.info("shakerAroundDeviceApplyId BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        parameterMp.put("quantity", quantity);
//        parameterMp.put("apply_reason", applyReason);
//        parameterMp.put("comment", comment);
//        // parameterMp.put("poi_id", poiId);
//        String result = tryWeChatApi("shakearound/device/applyid", "Post", urlParameterMp, parameterMp, "json", null,
//                null, accountId);
//        JSONObject jsonObj;
//        try {
//            jsonObj = new JSONObject(result);
//            Integer errCode = jsonObj.optInt("errcode");
//            if (errCode.equals(0)) {
//                JSONObject dataObj = jsonObj.optJSONObject("data");
//                return dataObj.optInt("apply_id");
//            } else {
//                return -1;
//            }
//        } catch (JSONException e) {
//            HttpCommonUtil.handleJsonException(e, result);
//            throw new SystemException(e);
//        }
//    }

    
//    public Integer shakeAroundDeviceApplyStatus(Integer applyId, Long accountId) {
//        LOGGER.info("shakeAroundDeviceApplyStatus BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        parameterMp.put("apply_id", applyId);
//        String result = tryWeChatApi("shakearound/device/applystatus", "Post", urlParameterMp, parameterMp, "json",
//                null, null, accountId);
//        JSONObject jsonObj;
//        try {
//            jsonObj = new JSONObject(result);
//            JSONObject dataObj = jsonObj.optJSONObject("data");
//            Integer auditStatus = dataObj.optInt("audit_status");
//            return auditStatus;
//        } catch (JSONException e) {
//            HttpCommonUtil.handleJsonException(e, result);
//            throw new SystemException(e);
//        }
//    }
//
//
//    public void shakeAroundDeviceUpdate(Integer deviceId, String uuid, Integer major, Integer minor, String comment,
//            Long accountId) {
//
//        LOGGER.info("shakeAroundDeviceUpdate BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> deviceIdentifierMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        deviceIdentifierMp.put("device_id", deviceId);
//        deviceIdentifierMp.put("uuid", uuid);
//        deviceIdentifierMp.put("major", major);
//        deviceIdentifierMp.put("minor", minor);
//        parameterMp.put("device_identifier", deviceIdentifierMp);
//        parameterMp.put("comment", comment);
//        tryWeChatApi("shakearound/device/update", "Post", urlParameterMp, parameterMp, "json", null, null, accountId);
//    }
//
//
//    public void shakeAroundDeviceBindLocation(Integer deviceId, String uuid, Integer major, Integer minor,
//            Integer poiId, Long accountId) {
//
//        LOGGER.info("shakeAroundDeviceBindLocation BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> deviceIdentifierMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        deviceIdentifierMp.put("device_id", deviceId);
//        deviceIdentifierMp.put("uuid", uuid);
//        deviceIdentifierMp.put("major", major);
//        deviceIdentifierMp.put("minor", minor);
//        parameterMp.put("device_identifier", deviceIdentifierMp);
//        parameterMp.put("poi_id", poiId);
//        tryWeChatApi("shakearound/device/bindlocation", "Post", urlParameterMp, parameterMp, "json", null, null,
//                accountId);
//
//    }
//
//
//    public List<Map<String, Object>> shakeAroundDeviceSearchByDeviceId(Map<String, Object> deviceIdentifiers,
//            Long accountId) {
//        return shakeAroundDeviceSearch(1, deviceIdentifiers, null, null, null, accountId);
//    }
//
//
//    public List<Map<String, Object>> shakeAroundDeviceSearchAll(Integer lastSeen, Integer count, Long accountId) {
//        return shakeAroundDeviceSearch(2, null, null, lastSeen, count, accountId);
//    }
//
//
//    public List<Map<String, Object>> shakeAroundDeviceSearchByApplyId(Integer applyId, Integer lastSeen, Integer count,
//            Long accountId) {
//        return shakeAroundDeviceSearch(3, null, applyId, lastSeen, count, accountId);
//    }
//
//    private List<Map<String, Object>> shakeAroundDeviceSearch(Integer type, Map<String, Object> deviceIdentifiers,
//            Integer applyId, Integer lastSeen, Integer count, Long accountId) {
//
//        LOGGER.info("shakeAroundDeviceSearch BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        if (type.equals(1)) {
//            parameterMp.put("type", type);
//            parameterMp.put("device_identifiers", deviceIdentifiers);
//        } else if (type.equals(2)) {
//            parameterMp.put("type", type);
//            parameterMp.put("last_seen", lastSeen);
//            parameterMp.put("count", count);
//        } else if (type.equals(3)) {
//            parameterMp.put("type", type);
//            parameterMp.put("apply_id", applyId);
//            parameterMp.put("last_seen", lastSeen);
//            parameterMp.put("count", count);
//        }
//        String result = tryWeChatApi("shakearound/device/search", "Post", urlParameterMp, parameterMp, "json", null,
//                null, accountId);
//        JSONObject jsonObj;
//        List<Map<String, Object>> devices = new ArrayList<>();
//        try {
//            jsonObj = new JSONObject(result);
//            JSONObject jsonData = jsonObj.optJSONObject("data");
//            JSONArray jsonDevices = jsonData.optJSONArray("devices");
//            for (int i = 0; i < jsonDevices.length(); i++) {
//                JSONObject device = jsonDevices.optJSONObject(i);
//                Map<String, Object> deviceMap = new HashMap<>();
//                deviceMap.put("comment", device.optString("comment"));
//                deviceMap.put("device_id", device.optInt("device_id"));
//                deviceMap.put("major", device.optInt("major"));
//                deviceMap.put("minor", device.optInt("minor"));
//                deviceMap.put("status", device.optInt("status"));
//                deviceMap.put("last_active_time", device.optLong("last_active_time"));
//                deviceMap.put("poi_id", device.optInt("poi_id"));
//                deviceMap.put("uuid", device.optString("uuid"));
//                devices.add(deviceMap);
//            }
//            Integer totalCount = jsonData.optInt("total_count");
//            LOGGER.debug("shakeAroundDeviceSearch END. devicesCount:({})", totalCount);
//            return devices;
//        } catch (JSONException e) {
//            HttpCommonUtil.handleJsonException(e, result);
//            throw new SystemException(e);
//        }
//    }
//
//
//    public Integer shakeAroundPageAdd(String title, String description, String pageUrl, String comment, String iconUrl,
//            Long accountId) {
//        LOGGER.info("shakeAroundPageAdd BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        parameterMp.put("title", title);
//        parameterMp.put("description", description);
//        parameterMp.put("page_url", pageUrl);
//        parameterMp.put("icon_url", iconUrl);
//        parameterMp.put("comment", comment);
//        String result = tryWeChatApi("shakearound/page/add", "Post", urlParameterMp, parameterMp, "json", null, null,
//                accountId);
//        JSONObject jsonObj;
//        try {
//            jsonObj = new JSONObject(result);
//            JSONObject dataObj = jsonObj.getJSONObject("data");
//            return dataObj.optInt("page_id");
//        } catch (JSONException e) {
//            throw new SystemException(e);
//        }
//    }
//
//
//    public void shakeAroundPageUpdate(Integer pageId, String title, String description, String iconUrl, String pageUrl,
//            String comment, Long accountId) {
//
//        LOGGER.info("shakeAroundPageUpdate BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        parameterMp.put("page_id", pageId);
//        parameterMp.put("title", title);
//        parameterMp.put("description", description);
//        parameterMp.put("icon_url", iconUrl);
//        parameterMp.put("page_url", pageUrl);
//        parameterMp.put("comment", comment);
//        tryWeChatApi("shakearound/page/update", "Post", urlParameterMp, parameterMp, "json", null, null, accountId);
//
//    }
//
//
//    public List<Map<String, Object>> shakeAroundPageSearchByPageIds(List<Integer> pageIds, Long accountId) {
//        return shakeAroundPageSearch(1, pageIds, null, null, accountId);
//    }
//
//
//    public List<Map<String, Object>> shakeAroundPageSearchAll(Integer begin, Integer count, Long accountId) {
//        return shakeAroundPageSearch(2, null, begin, count, accountId);
//    }
//
//    private List<Map<String, Object>> shakeAroundPageSearch(Integer type, List<Integer> pageIds, Integer begin,
//            Integer count, Long accountId) {
//
//        LOGGER.info("shakeAroundPageSearch BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        if (type.equals(1)) {
//            parameterMp.put("type", type);
//            parameterMp.put("page_ids", pageIds);
//        } else if (type.equals(2)) {
//            parameterMp.put("type", type);
//            parameterMp.put("begin", begin);
//            parameterMp.put("count", count);
//        }
//        String result = tryWeChatApi("shakearound/page/search", "Post", urlParameterMp, parameterMp, "json", null, null,
//                accountId);
//        JSONObject jsonObj;
//        List<Map<String, Object>> pages = new ArrayList<>();
//        try {
//            jsonObj = new JSONObject(result);
//            JSONObject jsonData = jsonObj.optJSONObject("data");
//            JSONArray jsonPages = jsonData.optJSONArray("pages");
//
//            for (int i = 0; i < jsonPages.length(); i++) {
//                Map<String, Object> pageMap = new HashMap<>();
//                JSONObject page = jsonPages.getJSONObject(i);
//                pageMap.put("comment", page.optString("comment"));
//                pageMap.put("description", page.optString("description"));
//                pageMap.put("icon_url", page.optString("icon_url"));
//                pageMap.put("page_id", page.optString("page_id"));
//                pageMap.put("page_url", page.optString("page_url"));
//                pageMap.put("title", page.optString("title"));
//                pages.add(pageMap);
//            }
//            return pages;
//        } catch (JSONException e) {
//            HttpCommonUtil.handleJsonException(e, result);
//            throw new SystemException(e);
//        }
//    }
//
//
//    public void shakeAroundPageDelete(Integer pageId, Long accountId) {
//
//        LOGGER.info("shakeAroundPageDelete BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        parameterMp.put("page_id", pageId);
//        tryWeChatApi("shakearound/page/delete", "Post", urlParameterMp, parameterMp, "json", null, null, accountId);
//
//    }
//
//
//    public String shakeAroundMaterialAdd(String type, File media, Long accountId) {
//
//        LOGGER.info("shakeAroundMaterialAdd BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        String accessToken = getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        urlParameterMp.put("type", "icon");
//        parameterMp.put("media", media);
//        String result = tryWeChatApi("shakearound/material/add", "Post", urlParameterMp, parameterMp,
//                HttpUtil.PARAMETER_TYPE_MULTIPART, null, null, accountId);
//        JSONObject jsonObj;
//        try {
//            jsonObj = new JSONObject(result);
//            JSONObject dataObj = jsonObj.getJSONObject("data");
//            return dataObj.optString("pic_url");
//        } catch (JSONException e) {
//            HttpCommonUtil.handleJsonException(e, result);
//            throw new SystemException(e);
//        }
//    }
//
//
//    public void shakeAroundDeviceBindPage(Integer deviceId, List<Integer> pageIds, Long accountId) {
//        LOGGER.info("shakeAroundDeviceBindPage BEGIN");
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        Map<String, Object> parameterMp = new HashMap<>();
//        Map<String, Object> deviceIdentifierMap = new HashMap<>();
//        String accessToken = getAccessTokenByAccount(accountId);
//        urlParameterMp.put("access_token", accessToken);
//        deviceIdentifierMap.put("device_id", deviceId);
//        parameterMp.put("device_identifier", deviceIdentifierMap);
//        parameterMp.put("page_ids", pageIds);
//        tryWeChatApi("shakearound/device/bindpage", "Post", urlParameterMp, parameterMp, "json", null, null, accountId);
//    }

    private static class WeChatErrorHandler implements HttpCommonErrorHandler {

        private static void handleErrorMessage(String errorCode, String errmsg) {
            handleAuthErrorMessage(errorCode, errmsg);
            handleCardErrorMessage(errorCode, errmsg);
            handleCommonErrorMessage(errorCode, errmsg);
        }

        private static void handleAuthErrorMessage(String errorCode, String errmsg) {
            switch (errorCode) {
            case "40014":
            case "42001":
            case "40001":
                throw new BusinessException("ACCESS_TOKEN_EXPIRE");
            case "40013":
                throw new BusinessException("INVALID_APPID_OR_SECRET");
            case "40005":
                throw new BusinessException("INVALID_FILE_FOR_WECHAT");
            case "46004":
                throw new BusinessException("WECHAT_USER_NOT_EXIST");

            }
        }

        private static void handleCardErrorMessage(String errorCode, String errmsg) {
            switch (errorCode) {
            case "40099":
                throw new BusinessException("CARD_ALREADY_CONSUMED");
            case "40127":
                throw new BusinessException("CARD_DELETED_OR_GIVING");
            case "45030":
                throw new BusinessException("UNAUTHORIZED_API_FOR_CARDID");
            }
        }

        private static void handleCommonErrorMessage(String errorCode, String errmsg) {
            switch (errorCode) {
            case "43004":
                throw new BusinessException("REQUIRE_SUBSCRIBE");
            case "40005":
                throw new BusinessException("INVALID_FILE_FOR_WECHAT");
            case "46004":
                throw new BusinessException("WECHAT_USER_NOT_EXIST");
            case "48001":
                throw new BusinessException("API_UNAUTHORIZED");
            case "45009":
                throw new BusinessException("MAX_API_QUOTA_LIMIT_ERROR");
            case "-1":
                throw new BusinessException("WECHAT_SYSTEM_ERROR");
            default:
                throw new BusinessException("WECHAT_API_RETURN_ERROR, errorCode, errmsg");
            }
        }

        
        public void handleError(String result) {
            try {
                LOGGER.debug("wechat return result:{"+result+"}");
                // warn: may have xml api in the future
                JSONObject resultJsonObj = new JSONObject(result);
//                String[] fieldArr = JSONObject.getNames(resultJsonObj);
//                if (ArrayUtils.contains(fieldArr, "errcode")) {
//                    String errorCode = resultJsonObj.getString("errcode");
//                    if (!org.apache.commons.lang.StringUtils.isEmpty(errorCode) && !"0".equals(errorCode)) {
//                        LOGGER.warn("WeChat Server Return Error with result:" + result);
//                        String errmsg = resultJsonObj.getString("errmsg");
//                        handleErrorMessage(errorCode, errmsg);
//                    } else {
//                        return;
//                    }
//                } else {
//                    return;
//                }
            } catch (JSONException e) {
                throw new SystemException(e);
            }
        }

    }

}
