package com.fjd.social;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fjd.cli.BusinessException;

import twitter4j.JSONException;
import twitter4j.JSONObject;

/**
 * 
 * provide mock server,
 * error handling,
 * easier writing way,
 * json result convert,
 * default header setting
 * for HttpUtil
 * 
 * @author I312177
 *
 */
public class HttpCommonUtil {
	private static final Logger LOGGER = Logger.getLogger(HttpCommonUtil.class);
    private final String apiBaseUrl;
    private String isMockServerGlobalSettingKey;
    private String mockServerUrlPath;
    private HttpCommonErrorHandler errorHandler;
    private final HttpUtil httpUtil = new HttpUtil();
    private final Map<String, String> defaultHeader = new HashMap<>();

    public HttpCommonUtil(String isMockServerGlobalSettingKey, String mockServerUrlPath, String apiBaseUrl) {

        this.setIsMockServerGlobalSettingKey(isMockServerGlobalSettingKey);
        this.setMockServerUrlPath(mockServerUrlPath);
        this.apiBaseUrl = apiBaseUrl;
    }

    public HttpCommonUtil(String isMockServerGlobalSettingKey, String mockServerUrlPath, String apiBaseUrl,
            HttpCommonErrorHandler errorHandler) {
        this.setIsMockServerGlobalSettingKey(isMockServerGlobalSettingKey);
        this.setMockServerUrlPath(mockServerUrlPath);
        this.apiBaseUrl = apiBaseUrl;
        this.errorHandler = errorHandler;
    }

    public HttpCommonUtil(String isMockServerGlobalSettingKey, String mockServerUrlPath, String apiBaseUrl,
            HttpCommonErrorHandler errorHandler, Map<String, String> defaultHeader) {
        this.setIsMockServerGlobalSettingKey(isMockServerGlobalSettingKey);
        this.setMockServerUrlPath(mockServerUrlPath);
        this.apiBaseUrl = apiBaseUrl;
        this.errorHandler = errorHandler;
        if (defaultHeader != null) {
            this.defaultHeader.putAll(defaultHeader);
        }

    }

    public String sendRequest(String url, String method, Map<String, String> header, Map<String, Object> parameter,
            String parameterType) {
        HttpResponseInfo hri = sendRequestForResponseInfo(url, method, header, parameter, null, parameterType);
        return hri.getBodyStr();
    }

    /**
     * Original HttpUtil sendRequest add error handle
     * 
     * @param url
     * @param method
     * @param parameterJson
     * @return
     */
    public String sendJsonRequest(String url, String method, String parameterJson) {
        HttpResponseInfo hri = sendRequestForResponseInfo(url, method, new HashMap(), null, parameterJson,
                HttpUtil.PARAMETER_TYPE_JSON);
        return hri.getBodyStr();

    }

    /**
     * Original HttpUtil sendRequest add error handle and defaultHeader
     * temporarily public for wechat use
     * 
     * @param url
     * @param method
     * @param header
     * @param parameter
     * @param parameterType
     * @return
     */
    public HttpResponseInfo sendRequestForResponseInfo(String url, String method, Map<String, String> header,
            Map<String, Object> parameter, String jsonParameter, String parameterType) {
        LOGGER.info("sendRequestForResponseInfo: {} {}"+ method+ url);
        header.putAll(defaultHeader);
        try {
            HttpEntity reqEntity = null;
            if (jsonParameter != null) {
                reqEntity = HttpUtil.setUpEntity(jsonParameter);
            } else {
                reqEntity = HttpUtil.setUpEntity(parameter, parameterType);
            }
            HttpResponseInfo hri = httpUtilSendRequest(url, method, header, reqEntity, parameterType);
            String result = hri.getBodyStr();
            if (errorHandler != null) {
                errorHandler.handleError(result);
            }
            return hri;
        } catch (ServerException e) {
            LOGGER.warn("sendRequest server error", e);
            if (errorHandler != null && StringUtils.isNotEmpty(e.getDescription())) {
                errorHandler.handleError(e.getDescription());
            }
            throw new BusinessException("SocialCommonErrorCode.SERVER_EXCEPTION_UNKNOWN"+
                    e.getDescription());
        } catch (Exception e) {
            LOGGER.error("sendRequest other error", e);
            throw new SystemException(e);

        }

    }

    /**
     * Only public for testing, please don't use it
     * 
     * @param url
     * @param method
     * @param header
     * @param reqEntity
     * @param parameterType
     * @return
     */

    private HttpResponseInfo httpUtilSendRequest(String url, String method, Map<String, String> header,
            HttpEntity reqEntity, String parameterType) {
        HttpResponseInfo result = httpUtil.sendRequestForResponseInfo(url, method, header, reqEntity, parameterType);
        return result;
    }

    /**
     *
     * @param jsonString
     *            original json String
     * @param clazz
     *            Class to convert to
     * @return List of Object of Class Type
     * @throws SystemException
     *             if invalid json
     */
    public static <T> List<T> convertToList(String jsonString, final Class<T> clazz) {
        LOGGER.info("convertToList BEGIN jsonString is {}"+jsonString);
        ObjectMapper mapper = new ObjectMapper();
        ObjectMapperUtil.initObjectMapper(mapper);
        List<T> list = new ArrayList<T>();

        if (StringUtils.isNotBlank(jsonString)) {
            CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
            try {
                list = mapper.readValue(jsonString, javaType);
            } catch (IOException e) {
                LOGGER.error("convertToList: Covert from JSONString To Obj Error with result:" + jsonString, e);
                throw new SystemException(e);
            }
        }
        return list;
    }

    /**
     *
     * @param jsonString
     *            original json String
     * @param clazz
     *            Class to convert to
     * @return Object of Class Type
     * @throws SystemException
     *             if invalid json
     */
    public static <T> T convertToObject(String jsonString, final Class<T> clazz) {
        LOGGER.info("convertToObject BEGIN jsonString is {}"+jsonString);
        ObjectMapper mapper = new ObjectMapper();
        ObjectMapperUtil.initObjectMapper(mapper);
        T obj = null;
        if (StringUtils.isNotBlank(jsonString)) {
            try {
                obj = mapper.readValue(jsonString, clazz);
            } catch (IOException e) {
                LOGGER.error("convertToObject: Covert from JSONString To Obj Error with result:" + jsonString, e);
                throw new SystemException(e);
            }
        }
        return obj;
    }

    /**
     * get specific field value from json object.
     * 
     * @param json
     * @param fieldList
     */
    public static String getJSONFieldValue(JSONObject json, List<String> fieldList, boolean isNullNeeded) {
        LOGGER.debug("getJSONFieldValue BEGIN, json is {},fieldList is {}"+ json+ fieldList);
        String fieldVal = null;
        if (fieldList == null) {
            fieldVal = isNullNeeded ? null : "";
            return fieldVal;
        }
        try {
            for (int i = 0; i < fieldList.size(); ++i) {
                // check whether this field exists or not, if yes, continue, otherwise assign a default value and
                // return.
                String fieldLayerI = fieldList.get(i);
                LOGGER.debug("getJSONFieldValue under layer {}, field:{},json:{}"+ i+ fieldLayerI+ json);
                if (json.has(fieldLayerI)) {
                    fieldVal = json.getString(fieldLayerI);
                    if (i == fieldList.size() - 1) {
                        break;
                    }
                    if (fieldVal != null && fieldVal.startsWith("{")) {
                        json = new JSONObject(fieldVal);
                    } else {
                        fieldVal = isNullNeeded ? null : "";
                        break;
                    }

                } else {
                    fieldVal = isNullNeeded ? null : "";
                    break;
                }
            }
            LOGGER.debug("getJSONFieldValue END , fieldValue is {}"+ fieldVal);
            return fieldVal;

        } catch (JSONException e) {
            LOGGER.error("getJSONFieldValue Exception json is {},fieldVal is {}: {}"+ json+ fieldVal, e);
            throw new SystemException(e);
        }

    }

    public String doGet(String apiPath, Map<String, String> urlParameterMp) {
        String url = getUrl(apiPath, urlParameterMp);
        return doGet(url);
    }

    private String doGet(String url) {
        return sendRequest(url, "Get", new HashMap<String, String>(), new HashMap<String, Object>(),
                HttpUtil.PARAMETER_TYPE_URLENCODED);
    }

    /**
     * post with no payload
     * 
     * @param apiPath
     * @param urlParameterMp
     * @return
     */
    public String doPost(String apiPath, Map<String, String> urlParameterMp) {
        String url = getUrl(apiPath, urlParameterMp);
        return doPost(url);
    }

    /**
     * post with json payload
     * 
     * @param apiPath
     * @param urlParameterMp
     * @return
     */
    public String doPost(String apiPath, Map<String, String> urlParameterMp, Map<String, Object> parameterMp) {
        String url = getUrl(apiPath, urlParameterMp);
        String result = sendRequest(url, "Post", new HashMap<String, String>(), parameterMp,
                HttpUtil.PARAMETER_TYPE_JSON);
        return result;
    }

    /**
     * post with no payload
     * 
     * @param url
     * @return
     */
    private String doPost(String url) {
        String result = sendRequest(url, "Post", new HashMap<String, String>(), new HashMap<String, Object>(),
                HttpUtil.PARAMETER_TYPE_URLENCODED);
        return result;
    }

    /**
     * post multipart entity
     * 
     * @param apiPath
     * @param urlParameterMp
     * @param parameterMp
     * @return
     */
    public String doPostMultipart(String apiPath, Map<String, String> urlParameterMp, Map<String, Object> parameterMp) {
        String url = getUrl(apiPath, urlParameterMp);
        String result = sendRequest(url, "Post", new HashMap<String, String>(), parameterMp,
                HttpUtil.PARAMETER_TYPE_MULTIPART);
        return result;
    }

    /**
     * 
     * @param apiPath
     *            like "v1/users/search/"
     * @param urlParameterMp
     * @return
     */
    public String getUrl(String apiPath, Map<String, String> urlParameterMp) {
        String baseUrl;
        if (MockServerUtil.isMockServer(isMockServerGlobalSettingKey)) {
            baseUrl = MockServerUtil.getMockServerUrl(mockServerUrlPath);
        } else {
            baseUrl = apiBaseUrl;
        }

        String url = HttpUtil.buildEncodedUrl(baseUrl + apiPath, urlParameterMp);
        LOGGER.info("getUrl send to url:" + url);
        return url;
    }

    /**
     * @return the isMockServerGlobalSettingKey
     */
    public String getIsMockServerGlobalSettingKey() {
        return isMockServerGlobalSettingKey;
    }

    /**
     * @param isMockServerGlobalSettingKey
     *            the isMockServerGlobalSettingKey to set
     */
    public void setIsMockServerGlobalSettingKey(String isMockServerGlobalSettingKey) {
        this.isMockServerGlobalSettingKey = isMockServerGlobalSettingKey;
    }

    /**
     * @return the mockServerUrlPath
     */
    public String getMockServerUrlPath() {
        return mockServerUrlPath;
    }

    /**
     * @param mockServerUrlPath
     *            the mockServerUrlPath to set
     */
    public void setMockServerUrlPath(String mockServerUrlPath) {
        this.mockServerUrlPath = mockServerUrlPath;
    }

    public <T> T doGetForObject(String apiPath, Map<String, String> urlParameterMp, final Class<T> clazz) {
        String result = this.doGet(apiPath, urlParameterMp);
        return convertToObject(result, clazz);
    }

    public <T> List<T> doGetForList(String apiPath, Map<String, String> urlParameterMp, final Class<T> clazz) {
        String result = this.doGet(apiPath, urlParameterMp);
        return convertToList(result, clazz);
    }

    public <T> T doPostForObject(String apiPath, Map<String, String> urlParameterMp, Map<String, Object> parameterMp,
            final Class<T> clazz) {
        String result = this.doPost(apiPath, urlParameterMp, parameterMp);
        return convertToObject(result, clazz);
    }

    public <T> T doPatchForObject(String apiPath, Map<String, String> urlParameterMp, Map<String, Object> parameterMp,
            final Class<T> clazz) {
        String url = getUrl(apiPath, urlParameterMp);
        String result = sendRequest(url, "Patch", new HashMap<String, String>(), parameterMp,
                HttpUtil.PARAMETER_TYPE_JSON);
        return convertToObject(result, clazz);
    }

    public <T> T doPutForObject(String apiPath, Map<String, String> urlParameterMp, Map<String, Object> parameterMp,
            final Class<T> clazz) {
        String url = getUrl(apiPath, urlParameterMp);
        String result = sendRequest(url, "Put", new HashMap<String, String>(), parameterMp,
                HttpUtil.PARAMETER_TYPE_JSON);
        return convertToObject(result, clazz);
    }

    public <T> T doDeleteForObject(String apiPath, Map<String, String> urlParameterMp, final Class<T> clazz) {

        String url = getUrl(apiPath, urlParameterMp);
        String result = sendRequest(url, "Delete", new HashMap<String, String>(), new HashMap<String, Object>(),
                HttpUtil.PARAMETER_TYPE_URLENCODED);
        return convertToObject(result, clazz);
    }

}