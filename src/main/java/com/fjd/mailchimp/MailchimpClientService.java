package com.fjd.mailchimp;

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



import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;



/**
 * @author I312177
 *
 */
@Service
public class MailchimpClientService {
    private static final Logger LOGGER = Logger.getLogger(MailchimpClientService.class);
    private static final String MAILCHIMP_AUTH_BASE_URL = "https://login.mailchimp.com/";
    private static final String MAILCHIMP_API_VERSION = "3.0/";
    private static final String MAILCHIMP_BASE_URL_FORMAT = "https://%s.api.mailchimp.com/%s";
    public static final String MEMBER_STATUS_PENDING = "pending";
    public static final String MEMBER_STATUS_SUBSCRIBED = "subscribed";
    public static final String MEMBER_STATUS_UNSUBSCRIBED = "unsubscribed";
    public static final String MEMBER_STATUS_CLEANED = "cleaned";
    public static final String MEMBER_STATUS_TRANSACTIONAL = "transactional";
    private static final HttpCommonErrorHandler ERROR_HANDLER = new MailchimpErrorHandler();

    private HttpCommonUtil getHttpCommonParam(String dc, String accessToken) {
        LOGGER.info("initHttpCommonUtil BEGIN,dc is {"+dc+"},accessToken is {"+accessToken+"}");
        if (StringUtils.isEmpty(dc) || StringUtils.isEmpty(accessToken)) {
            throw new BusinessException("AUTH_INVALID");
        }
        Map<String, String> header = getEspRequestHeader();
        String basicAuth = "anystring:" + accessToken;
        String base64EncodedAuth = new String(Base64.encodeBase64(basicAuth.getBytes()));

        header.put("Authorization", "Basic " + base64EncodedAuth);
        LOGGER.info("initHttpCommonUtil END");
        return new HttpCommonUtil("Global.Esp.UseMockServer", "mailchimp", getApiBaseUrl(dc), ERROR_HANDLER,
                header);

    }

    private static String getApiBaseUrl(String dc) {
        return String.format(MAILCHIMP_BASE_URL_FORMAT, dc, MAILCHIMP_API_VERSION);
    }

    public String getAuthorizationUrl(){
        String appId = CmtClient.getInstance().getGlobalSettings("Global.MailChimp.ClientId");
        String redirectUrl = CmtClient.getInstance().getGlobalSettings("Global.MailChimp.RedirectUri");
        Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
        urlParameterMp.put("response_type", "code");
        urlParameterMp.put("redirect_uri", redirectUrl);
        urlParameterMp.put("client_id", appId);

        String url = HttpUtil.buildEncodedUrl("https://login.mailchimp.com/oauth2/authorize", urlParameterMp);
        return url;
    }
    public String getToken(String code) {
        LOGGER.debug("getToken BEGIN");
        Map<String, Object> token = null;
        HttpCommonUtil httpAuthUtil = new HttpCommonUtil("Global.Esp.UseMockServer", "mailchimp",
                MAILCHIMP_AUTH_BASE_URL, null, getEspRequestHeader());
        String url = httpAuthUtil.getUrl( "oauth2/token", new HashMap<>());
        Map<String, String> header = new HashMap<>();
        header.put("Host", "login.mailchimp.com");
        Map<String, Object> parameterMp = new HashMap<>();
        parameterMp.put("code", code);
        parameterMp.put("client_id", CmtClient.getInstance().getGlobalSettings("Global.MailChimp.ClientId"));
        parameterMp.put("client_secret", CmtClient.getInstance().getGlobalSettings("Global.MailChimp.ClientSecret"));
        parameterMp.put("redirect_uri", CmtClient.getInstance().getGlobalSettings("Global.MailChimp.RedirectUri"));
        parameterMp.put("grant_type", "authorization_code");
        // return like
        // {"access_token":"5c6ccc561059aa386da9d112215bae55","expires_in":0,"scope":null}
        String tokenJson = httpAuthUtil.sendRequest( url, "Post", header, parameterMp,
                HttpUtil.PARAMETER_TYPE_URLENCODED);
        token = HttpCommonUtil.convertToObject(tokenJson, Map.class);
        LOGGER.info("getToken END");
        return (String) token.get("access_token");
    }

    public String getDc(String accessToken) {
        LOGGER.debug("getDc BEGIN");
        HttpCommonUtil httpAuthUtil = new HttpCommonUtil("Global.Esp.UseMockServer", "mailchimp",
                MAILCHIMP_AUTH_BASE_URL, null, getEspRequestHeader());
        String url = httpAuthUtil.getUrl( "oauth2/metadata", new HashMap());
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + accessToken);
        // will return like
        // {"dc":"us1","login_url":"https:\/\/login.mailchimp.com","api_endpoint":"https:\/\/us1.api.mailchimp.com"}
        String paramsJson = httpAuthUtil.sendRequest( url, "Post", header, new HashMap<String, Object>(),
                HttpUtil.PARAMETER_TYPE_URLENCODED);
        Map<String, Object> params = HttpCommonUtil.convertToObject(paramsJson, Map.class);

        LOGGER.debug("getDc END");
        return (String) params.get("dc");
    }

//    public void campaignContentUpdate(String dc, String accessToken, String campaignId) {
//        LOGGER.info("campaignContentUpdate BEGIN, dc is {}, accessToken is {}, campaignId is {}", dc, accessToken,
//                campaignId);
//        if (StringUtils.isEmpty(campaignId)) {
//            throw new BusinessException("Invalid espCampaign Id");// TODO
//        }
//        HttpCommonParamInfo hcpi = this.getHttpCommonParam(dc, accessToken);
//        Map<String, Object> parameterMp = new HashMap<String, Object>();
//        parameterMp.put("html", Constants.DEFAULT_HTML_CONTENT);
//        parameterMp.put("plain_text", Constants.DEFAULT_TEXT_CONTENT);
//        HttpCommonUtil.doPutForObject(hcpi, "campaigns/" + campaignId + "/content", new HashMap<>(), parameterMp,
//                Map.class);
//        LOGGER.debug("campaignContentUpdate END");
//    }
//
//    public EmailCampaign campaignCreate(String dc, String accessToken, String listId, String title, String subject,
//            String fromName, String replyToEmail) {
//        LOGGER.info("create BEGIN");
//        Map<String, Object> parameterMp = new HashMap<>();
//        parameterMp.put("type", CampaignType.REGULAR.getValue());
//        Map<String, Object> settingsMp = new HashMap<>();
//        Map<String, Object> recipientsMp = new HashMap<>();
//        // EmailCampaign options = new EmailCampaign();
//        recipientsMp.put("list_id", listId);
//        parameterMp.put("recipients", recipientsMp);
//        settingsMp.put("title", title);
//        settingsMp.put("subject_line", subject);
//        settingsMp.put("reply_to", replyToEmail);
//        settingsMp.put("from_name", fromName);
//        settingsMp.put("to_name", Constants.TO_NAME);
//        Map<String, Object> trackingMp = new HashMap<>();
//        trackingMp.put("ecomm360", Boolean.TRUE);
//        parameterMp.put("settings", settingsMp);
//        parameterMp.put("tracking", trackingMp);
//        EmailCampaign emailCampaign = sendRequestCreateCampaign(dc, accessToken, parameterMp);
//
//        LOGGER.debug("create END");
//        return emailCampaign;
//
//    }
//
//    private EmailCampaign sendRequestCreateCampaign(String dc, String accessToken, Map<String, Object> parameter) {
//        LOGGER.info("sendRequestCreateCampaign BEGIN");
//        HttpCommonParamInfo hcpi = getHttpCommonParam(dc, accessToken);
//        String url = HttpCommonUtil.getUrl(hcpi, "campaigns", new LinkedHashMap<>());
//        HttpResponseInfo hri = HttpCommonUtil.sendRequestForResponseInfo(hcpi, url, "Post", new HashMap<>(), parameter,
//                null, HttpUtil.PARAMETER_TYPE_JSON);
//        String result = hri.getBodyStr();
//        EmailCampaign ec = HttpCommonUtil.convertToObject(result, EmailCampaign.class);
//        Map<String, String> headers = hri.getHeaders();
//        if (headers.get("Link") != null) {
//            String value = headers.get("Link");
//            String webId = value.substring(value.indexOf("id=") + 3, value.indexOf(">; rel=\"dashboard\""));
//            ec.setWebId(webId);
//        }
//        LOGGER.info("sendRequestCreateCampaign END");
//        return ec;
//
//    }
//
//    public void campaignUpdate(String dc, String accessToken, String espCampaignId, String title, String subject,
//            String listId, Integer segmentId) {
//        LOGGER.debug("campaignUpdate BEGIN");
//
//        Map<String, Object> parameterMp = new HashMap<>();
//
//        Map<String, Object> settingsMp = new HashMap<>();
//        settingsMp.put("title", title);
//        settingsMp.put("subject_line", subject);
//        parameterMp.put("settings", settingsMp);
//        if (listId != null && segmentId != null) {
//            Map<String, Object> recipientsMp = new HashMap<>();
//            recipientsMp.put("list_id", listId);
//            Map<String, Object> segmentOpts = new HashMap<>();
//            segmentOpts.put("saved_segment_id", segmentId);
//            segmentOpts.put("match", "all");
//            // List<Condition> conditions = new ArrayList<Condition>();
//            // conditions.add(new Condition("static_segment", "eq", segmentId +
//            // ""));
//            // segmentOpts.setConditions(conditions);
//            recipientsMp.put("segment_opts", segmentOpts);
//            parameterMp.put("recipients", recipientsMp);
//        }
//        HttpCommonParamInfo hcpi = getHttpCommonParam(dc, accessToken);
//        HttpCommonUtil.doPatchForObject(hcpi, "campaigns/" + espCampaignId, new LinkedHashMap<>(), parameterMp,
//                Map.class);
//        LOGGER.info("campaignUpdate END");
//    }
//
//    public void campaignDelete(String dc, String accessToken, String espCampaignId) {
//        LOGGER.debug("campaignDelete BEGIN");
//        HttpCommonParamInfo hcpi = getHttpCommonParam(dc, accessToken);
//        HttpCommonUtil.doDeleteForObject(hcpi, "campaigns/" + espCampaignId, new HashMap<>(), Map.class);
//        LOGGER.info("campaignDelete END");
//    }
//
//    public EmailCampaign campaignReadById(String dc, String accessToken, String espCampaignId) {
//        LOGGER.debug("campaignReadById BEGIN");
//        HttpCommonParamInfo hcpi = getHttpCommonParam(dc, accessToken);
//        EmailCampaign emailCampaign = HttpCommonUtil.doGetForObject(hcpi, "campaigns/" + espCampaignId, new HashMap<>(),
//                EmailCampaign.class);
//        LOGGER.info("campaignReadById END");
//        return emailCampaign;
//    }
//
//    public String campaignContentReadById(String dc, String accessToken, String espCampaignId) {
//        LOGGER.debug("campaignContentReadById BEGIN");
//        HttpCommonParamInfo hcpi = getHttpCommonParam(dc, accessToken);
//        Map<String, Object> campaignContent = HttpCommonUtil.doGetForObject(hcpi,
//                "campaigns/" + espCampaignId + "/content", new LinkedHashMap<>(), Map.class);
//        LOGGER.info("campaignContentReadById END");
//        return (String) campaignContent.get("html");
//    }
//
//    public Map<String, Object> reportReadByCampaignId(String dc, String accessToken, String espCampaignId) {
//        LOGGER.debug("campaignContentReadById BEGIN");
//        HttpCommonParamInfo hcpi = getHttpCommonParam(dc, accessToken);
//        Map<String, Object> result = HttpCommonUtil.doGetForObject(hcpi, "reports/" + espCampaignId, new HashMap<>(),
//                Map.class);
//        LOGGER.info("campaignContentReadById END");
//        return result;
//    }

    public String listsRead(String dc, String accessToken) {
        LOGGER.debug("listsRead BEGIN dc is {"+dc+"}, accessToken is {"+accessToken+"}");

        int offset = 0;
        int total = 0;
        int apiTotal = 0;
        Map<String, String> urlParameterMp = new LinkedHashMap<>();
        urlParameterMp.put("count", "20");
        String result;
        HttpCommonUtil httpCommonUtil = getHttpCommonParam(dc, accessToken);
            urlParameterMp.put("offset", offset + "");
            result = httpCommonUtil.doGet( "lists", urlParameterMp);
        LOGGER.info("listsRead END ");
        return result;

    }

//    public List<String> listAddMembers(String dc, String accessToken, String listId, List<Recipient> members) {
//        LOGGER.debug("listAddMembers BEGIN");
//        if (CollectionUtils.isEmpty(members)) {
//            LOGGER.info("listAddMembers list is empty, do nothing");
//            return new ArrayList<>();
//        }
//
//        Map<String, Object> parameterMp = new HashMap<>();
//        // param.put("double_optin", Boolean.FALSE);
//        parameterMp.put("update_existing", Boolean.TRUE);
//        // param.put("replace_interests", Boolean.FALSE);
//        parameterMp.put("members", members);
//        HttpCommonParamInfo hcpi = getHttpCommonParam(dc, accessToken);
//        String resultStr = HttpCommonUtil.doPost(hcpi, "lists/" + listId, new HashMap<>(), parameterMp);
//        List<String> newOrUpdatedMembersList = new ArrayList<>();
//        List<String> newMembersList = new ArrayList<>();
//        List<String> updatedMembersList = new ArrayList<>();
//        try {
//            JSONObject resultJson = new JSONObject(resultStr);
//            JSONArray newMembersJson = resultJson.getJSONArray("new_members");
//            for (int i = 0; i < newMembersJson.length(); i++) {
//                JSONObject newMember = newMembersJson.getJSONObject(i);
//                String email = newMember.getString("email_address");
//                newMembersList.add(email);
//            }
//            JSONArray updatedMembersJson = resultJson.getJSONArray("updated_members");
//            for (int j = 0; j < updatedMembersJson.length(); j++) {
//                JSONObject updatedMember = updatedMembersJson.getJSONObject(j);
//                String email = updatedMember.getString("email_address");
//                updatedMembersList.add(email);
//            }
//            newOrUpdatedMembersList.addAll(newMembersList);
//            newOrUpdatedMembersList.addAll(updatedMembersList);
//            LOGGER.info("listAddMembers END, {} members new and {} members updated", newMembersList.size(),
//                    updatedMembersList.size());
//            return newOrUpdatedMembersList;
//        } catch (JSONException e) {
//            HttpCommonUtil.handleJsonException(e, resultStr);
//            throw new SystemException(e);
//        }
//
//    }

//    /**
//     *
//     * @param status
//     *            valid values: subscribed unsubscribed cleaned pending
//     *            transactional
//     * @param sinceLastChanged
//     *            Restrict results to subscribers whose information changed
//     *            after the set timeframe.
//     */
//    public List<MemberInfo> listMembersRead(String dc, String accessToken, String listId, String status,
//            DateTime sinceLastChanged) {
//        LOGGER.debug("listMembersRead BEGIN, query for listId:{}, status:{}, sinceLastChanged:{}", listId, status,
//                sinceLastChanged);
//
//        List<MemberInfo> results = new ArrayList<>();
//
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//
//        urlParameterMp.put("count", Constants.DEFAULT_LIMIT + "");
//        if (StringUtils.isNotEmpty(status)) {
//            urlParameterMp.put("status", status);
//        }
//
//        if (sinceLastChanged != null) {
//            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");// like
//            // 2017-07-09T15:41:36+00:00
//            urlParameterMp.put("since_last_changed", formatter.print(sinceLastChanged));
//        }
//
//        ListMemberResult listMemberResult;
//        int total = 0;
//        int offset = 0;
//        int apiTotal = 0;
//        HttpCommonParamInfo hcpi = getHttpCommonParam(dc, accessToken);
//        do {
//            urlParameterMp.put("offset", offset + "");
//            listMemberResult = HttpCommonUtil.doGetForObject(hcpi, "lists/" + listId + "/members", urlParameterMp,
//                    ListMemberResult.class);
//
//            results.addAll(listMemberResult.getData());
//            offset += Constants.DEFAULT_LIMIT;
//            total += listMemberResult.getData().size();
//            apiTotal = listMemberResult.getTotal();
//        } while (apiTotal > total);
//        LOGGER.info("listMembersRead END, query for listId:{}, status:{}, sinceLastChanged:{}, {}/{} records got.",
//                listId, status, results.size(), apiTotal);
//        return results;
//    }

//    public void listSegmentDelete(String dc, String accessToken, String listId, String segmentId) {
//        LOGGER.debug("listSegmentDelete BEGIN");
//        HttpCommonParamInfo hcpi = getHttpCommonParam(dc, accessToken);
//        Map<String, Object> params = new HashMap<>();
//        params.put("id", listId);
//        params.put("seg_id", segmentId);
//        HttpCommonUtil.doDeleteForObject(hcpi, "lists/" + listId + "/segments/" + segmentId, new HashMap<>(),
//                Map.class);
//        LOGGER.info("listSegmentDelete END");
//    }
//
//    /**
//     *
//     * @param dc
//     * @param accessToken
//     * @param listId
//     * @param segmentId
//     * @param members
//     *            list of email of members like ["a@b.com","c@d.com"]
//     */
//    public void listSegmentMemberAdd(String dc, String accessToken, String listId, String segmentId,
//            List<String> members) {
//        LOGGER.debug("listSegmentMemberAdd BEGIN");
//        Map<String, Object> parameterMp = new HashMap<>();
//        parameterMp.put("members_to_add", members);
//        HttpCommonParamInfo hcpi = getHttpCommonParam(dc, accessToken);
//        HttpCommonUtil.doPost(hcpi, "lists/" + listId + "/segments/" + segmentId, new LinkedHashMap<>(), parameterMp);
//        LOGGER.info("listSegmentMemberAdd BEGIN");
//    }
//
//    public Map<String, Object> listSegmentCreate(String dc, String accessToken, String listId, String name,
//            List<String> members) {
//        LOGGER.debug("listSegmentCreate BEGIN");
//        Map<String, Object> parameterMp = new HashMap<>();
//        parameterMp.put("name", name);
//        HttpCommonParamInfo hcpi = getHttpCommonParam(dc, accessToken);
//        if (!CollectionUtils.isEmpty(members)) {
//            parameterMp.put("static_segment", members);
//            return HttpCommonUtil.doPostForObject(hcpi, "lists/" + listId + "/segments/", new LinkedHashMap<>(),
//                    parameterMp, Map.class);
//        }
//        LOGGER.warn("empty email list, cannot create a segment.");
//        return null;
//    }

    public Map getAccountInfo(String dc, String accessToken) {
        LOGGER.debug("getAccountInfo BEGIN");
        HttpCommonUtil httpCommonUtil = getHttpCommonParam(dc, accessToken);
        Map accountInfo = httpCommonUtil.doGetForObject( "", new HashMap<>(), Map.class);

        LOGGER.info("getAccountInfo END, accountInfo is {"+accountInfo+"}");
        return accountInfo;
    }

//    public UnsubscribeResult reportsUnsubscribedReadByCampaignId(String dc, String accessToken, String campaignId,
//            int offset, int count) {
//        LOGGER.debug("reportsUnsubscribedReadByCampaignId BEGIN");
//        HttpCommonParamInfo hcpi = getHttpCommonParam(dc, accessToken);
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        urlParameterMp.put("offset", offset + "");
//        urlParameterMp.put("count", count + "");
//
//        UnsubscribeResult unsubscribeResult = HttpCommonUtil.doGetForObject(hcpi,
//                "reports/" + campaignId + "/unsubscribed", urlParameterMp, UnsubscribeResult.class);
//        LOGGER.info("reportsUnsubscribedReadByCampaignId END");
//        return unsubscribeResult;
//    }
//
//    public SendResult reportsSendToReadByCampaignId(String dc, String accessToken, String espCampaignId, int offset,
//            int count) {
//        LOGGER.debug("reportsSendToReadByCampaignId BEGIN");
//        HttpCommonParamInfo hcpi = getHttpCommonParam(dc, accessToken);
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        urlParameterMp.put("count", count + "");
//        urlParameterMp.put("offset", offset + "");
//        SendResult sendResult = HttpCommonUtil.doGetForObject(hcpi, "reports/" + espCampaignId + "/sent-to",
//                urlParameterMp, SendResult.class);
//        LOGGER.info("reportsSendToReadByCampaignId END");
//        return sendResult;
//    }

//    /**
//     *
//     * @param dc
//     * @param accessToken
//     * @param espCampaignId
//     * @param offset
//     * @param count
//     * @return see
//     *         http://developer.mailchimp.com/documentation/mailchimp/reference/reports/email-activity/#read-
//     *         get_reports_campaign_id_email_activity
//     */
//    public EmailActivityResult reportsEmailActivityReadByCampaignId(String dc, String accessToken, String espCampaignId,
//            Integer offset, Integer count) {
//        LOGGER.debug("reportsEmailActivityReadByCampaignId BEGIN");
//        HttpCommonParamInfo hcpi = getHttpCommonParam(dc, accessToken);
//        Map<String, String> urlParameterMp = new LinkedHashMap<>();
//        if (offset != null && count != null) {
//            urlParameterMp.put("count", count + "");
//            urlParameterMp.put("offset", offset + "");
//        }
//
//        EmailActivityResult result = HttpCommonUtil.doGetForObject(hcpi, "reports/" + espCampaignId + "/email-activity",
//                urlParameterMp, EmailActivityResult.class);
//        LOGGER.info("reportsEmailActivityReadByCampaignId END");
//        return result;
//    }
    // Currently not used
    // private EmailActivityRecord reportsEmailActivityReadByEmail(String dc,
    // String accessToken, String espCampaignId,
    // int offset, int count, String email) {
    // HttpCommonParamInfo hcpi = getHttpCommonParam(dc, accessToken);
    // Map<String, String> urlParameterMp = new LinkedHashMap<String, String>();
    // urlParameterMp.put("count", count + "");
    // urlParameterMp.put("offset", offset + "");
    // EmailActivityRecord result = httpCommonUtil.doGetForObject(
    // "reports/" + espCampaignId + "/email-activity/" + getMD5(email),
    // urlParameterMp,
    // EmailActivityRecord.class);
    // return result;
    // }

    // private static String getMD5(String str) {
    // try {
    //
    // MessageDigest md = MessageDigest.getInstance("MD5");
    //
    // md.update(str.getBytes());
    //
    // return new BigInteger(1, md.digest()).toString(16);
    // } catch (Exception e) {
    // throw new BusinessException("MD5 hash error");
    // }
    // }

    private static Map<String, String> getEspRequestHeader() {
        Map<String, String> headerMp = new HashMap<>();
        headerMp.put("Accept", "application/json");
        headerMp.put("Pragma", "no-cache");
        return headerMp;
    }

    private static class MailchimpErrorHandler implements HttpCommonErrorHandler {
        // http://developer.mailchimp.com/documentation/mailchimp/guides/error-glossary/
        private static final Logger LOGGER = Logger.getLogger(MailchimpErrorHandler.class);
        // private final BusinessObjectFacade boFacade;
        //
        // public MailchimpErrorHandler(BusinessObjectFacade boFacade) {
        // this.boFacade = boFacade;
        // }

        private void handleErrorMessage(Integer status, String type, String title, String detail,
                JSONObject resultJsonObj) {
//            switch (detail) {
//            case "The resource submitted could not be validated. For field-specific details, see the 'errors' array.":
//                JSONArray errors = resultJsonObj.optJSONArray("errors");
//                for (int i = 0; i < errors.length(); i++) {
//                    JSONObject error;
//                    try {
//                        error = errors.getJSONObject(i);
//                        String field = error.optString("field");
//                        String message = error.optString("message");
//                        if (field.contains("list_id")) {
//                            throw new BusinessException("LIST_DOES_NOT_EXIST");
//                        }
//                    } catch (JSONException e) {
//                        throw new SystemException(e);
//                    }
//
//                }
//                throw new BusinessException("SOCIAL_API_RETURN_ERROR"+ status+
//                        title + ", " + detail + ", " + errors.toString());
//            default:
//                throw new BusinessException("SOCIAL_API_RETURN_ERROR"+ status+
//                        title + ", " + detail);
//            }

        }

        /**
         * result like
         * {"type":"http://kb.mailchimp.com/api/error-docs/405-method-not-allowed","title":"Method
         * Not Allowed" ,"status":405,"detail": "The requested method and
         * resource are not compatible. See the Allow header for this resource's
         * available methods." ,"instance":""}
         */
        @Override
        public void handleError(String result) {
            // see
            // http://developer.mailchimp.com/documentation/mailchimp/guides/get-started-with-mailchimp-api-3/#errors

            try {
                // warn: may have xml api in the future
                if (!result.startsWith("{")) {
                    return;
                }
                JSONObject resultJsonObj = new JSONObject(result);
//                String[] fieldArr = JSONObject.getNames(resultJsonObj);
//                if (ArrayUtils.contains(fieldArr, "instance") && ArrayUtils.contains(fieldArr, "type")) {
//                    String errorCode = resultJsonObj.getString("type");
//                    if (!StringUtils.isEmpty(errorCode)) {
//                        LOGGER.warn("Mailchimp Server Return Error with result:" + result);
//                        String detail = resultJsonObj.getString("detail");
//                        String type = resultJsonObj.getString("type");
//                        String title = resultJsonObj.getString("title");
//                        Integer status = resultJsonObj.optInt("status");
//                        handleErrorMessage(status, type, title, detail, resultJsonObj);
//                    }
//                }
                return;
            } catch (JSONException e) {
                throw new SystemException(e);
            }

        }

    }

}
