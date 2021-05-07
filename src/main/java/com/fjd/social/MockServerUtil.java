package com.fjd.social;

import org.apache.log4j.Logger;


/**
 * @author I312177
 *
 */
public class MockServerUtil {
    private static final Logger LOGGER = Logger.getLogger(MockServerUtil.class);

    /**
     * 
     * @param isMockServerGlobalSettingKey
     *            like "Global.WeChat.UseMockServer"
     * @return
     */

    public static Boolean isMockServer(String isMockServerGlobalSettingKey) {
        String isMockServer = CmtClient.getInstance().getGlobalSettings(isMockServerGlobalSettingKey);
        LOGGER.info("isMockServer BEGIN {}:{}"+ isMockServerGlobalSettingKey+ isMockServer);
        if ("true".equalsIgnoreCase(isMockServer)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * @param mockServerUrlPath
     *            like "wechat"
     * @return
     */
    public static String getMockServerUrl(String mockServerUrlPath) {
        LOGGER.debug("getMockServerUrl BEGIN, mockServerUrlPath is {}"+ mockServerUrlPath);
        String baseMockHost = CmtClient.getInstance().getGlobalSettings("Global.MockServer.URL");
        String mockUrl = baseMockHost + mockServerUrlPath + "/";
        LOGGER.info("getMockServerUrl END, mockUrl is {}"+ mockUrl);
        return mockUrl;
    }

    /**
     * 
     * @param isMockServerGlobalSettingKey
     * @param mockServerUrlPath
     * @return null if not useMock, else mock server url
     */
    public static String getMockServerUrl(String isMockServerGlobalSettingKey, String mockServerUrlPath) {

        String mockServerUrl = null;
        LOGGER.debug("getMockServerUrl BEGIN, isMockServerGlobalSettingKey is {}, mockServerUrlPath is {}"+
                isMockServerGlobalSettingKey+ mockServerUrlPath);

        if (isMockServer(isMockServerGlobalSettingKey)) {
            mockServerUrl = getMockServerUrl(mockServerUrlPath);
            LOGGER.info("Currently Using Mock Server");
        }
        LOGGER.info("getMockServerUrl END, mockUrl is {}"+ mockServerUrl);
        return mockServerUrl;

    }

}
