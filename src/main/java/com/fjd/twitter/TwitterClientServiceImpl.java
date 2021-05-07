package com.fjd.twitter;

import java.io.File;
import java.util.*;

import com.fjd.social.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UploadedMedia;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fjd.cli.BusinessException;

import twitter4j.JSONException;
import twitter4j.JSONObject;


public class TwitterClientServiceImpl {
    private static final Logger LOGGER = Logger.getLogger(TwitterClientServiceImpl.class);

    private final Map<String, Twitter> twitterMap = new HashMap<>();

    private static Map<String, Twitter> twitters = new HashMap();
    private static final String TWITTER = "Twitter";

    public String getAuthorizationUrl() {
        LOGGER.debug("getAuthorizationUrl BEGIN");
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(CmtClient.getInstance().getGlobalSettings("Global.Twitter.ConsumerKey"))
                .setOAuthConsumerSecret(CmtClient.getInstance().getGlobalSettings("Global.Twitter.ConsumerSecret"));
        if (isMockServer()) {
            cb.setOAuthRequestTokenURL(getMockServerUrl() + "oauth/request_token");
            cb.setOAuthAccessTokenURL(getMockServerUrl() + "oauth/access_token");
        } else {
            String proxyHost = CmtClient.getInstance().getGlobalSettings("Global.Proxy.Host");
            String proxyPort = CmtClient.getInstance().getGlobalSettings("Global.Proxy.Port");
            if (!StringUtils.isEmpty(proxyHost) && !StringUtils.isEmpty(proxyPort)) {
                cb.setHttpProxyHost(proxyHost).setHttpProxyPort(Integer.parseInt(proxyPort));
            }
        }
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        RequestToken requestToken;
        String authorizationUrl = "";
        try {
            requestToken = twitter.getOAuthRequestToken();
            twitters.put(requestToken.getToken(), twitter);
            LOGGER.info("getAuthorizationUrl added twitter: {"+requestToken.getToken()+"}, {"+twitter+"}");
            authorizationUrl = requestToken.getAuthorizationURL();
        } catch (TwitterException e) {
            LOGGER.warn("Encounter error when getting request token", e);
            judgeExceptionType(e);
        }
        return authorizationUrl;
    }

    
    public Map<String, Object> getAccessToken(String oauth_verifier, String oauth_token) {
        LOGGER.info("getAccessToken BEGIN, oauth_verifier is {"+oauth_verifier+"}, oauth_token is {"+oauth_token+"}");
        Map<String, Object> token = new HashMap();
        AccessToken accessToken = null;
        Twitter twitter = twitters.get(oauth_token);
        if (twitter == null) {
            LOGGER.error("getAccessToken no such twitter with oauth_token {"+oauth_token+"} cached, twitter cache records: {"+ twitters.size()+"}");
            throw new BusinessException("NO_CREDENTIAL");
        }
        twitters.clear();
        try {
            accessToken = twitter.getOAuthAccessToken(oauth_verifier);
            token.put("user_id", accessToken.getUserId());
            // token.put("user_image", twitter.showUser(accessToken.getUserId()).getProfileImageURL());
            token.put("account", accessToken.getScreenName());
            token.put("token", accessToken.getToken());
            token.put("token_secret", accessToken.getTokenSecret());
        } catch (TwitterException e) {
            LOGGER.warn("Encounter error when getting access token", e);
            judgeExceptionType(e);
        }
        return token;
    }

    /**
     * @return the twitters
     */
    public static Map<String, Twitter> getTwitters() {
        return twitters;
    }

    private Boolean isMockServer() {
        return MockServerUtil.isMockServer("Global.Twitter.UseMockServer");
    }

    private String getMockServerUrl() {
        return MockServerUtil.getMockServerUrl("twitter");
    }

    
    public Map<String, Object> getUserInfo(Long userId, String accessToken, String accessTokenSecret) {
        LOGGER.info("getUserInfo BEGIN,userId:{"+userId+"},accessToken:{"+accessToken+"},accessTokenSecret:{"+accessTokenSecret+"}");
        if (userId == null) {
            return new HashMap();
        }
        Twitter twitter = getTwitterInstance(accessToken, accessTokenSecret);
        Map<String, Object> userInfo = new HashMap();
        try {
            User user = twitter.showUser(userId);
            userInfo.put("image_url", user.getProfileImageURL());
            userInfo.put("favourite_count", user.getFavouritesCount());
            userInfo.put("followers_count", user.getFollowersCount());
            userInfo.put("friends_count", user.getFriendsCount());
            userInfo.put("status_count", user.getStatusesCount());
        } catch (TwitterException e) {
            judgeExceptionType(e);
        }
        LOGGER.debug("getUserInfo END");
        return userInfo;
    }

    
//    public Long postToTwitter(String content, String accessToken, String accessTokenSecret) {
//        LOGGER.info("postToTwitter BEGIN,content:{},accessToken:{},accessTokenSecret:{}", content, accessToken,
//                accessTokenSecret);
//        if (StringUtils.isEmpty(content)) {
//            throw new BusinessException(SocialCommonErrorCode.CONTENT_NOT_EXIST);
//        }
//        Twitter twitter = getTwitterInstance(accessToken, accessTokenSecret);
//        Status postedStatus = null;
//        try {
//            postedStatus = twitter.updateStatus(content);
//        } catch (TwitterException te) {
//            judgeExceptionType(te);
//        }
//        if (postedStatus != null) {
//            return Long.valueOf(postedStatus.getId());
//        } else {
//            return null;
//        }
//    }

    /**
     * @param e
     */
    private void judgeExceptionType(TwitterException e) {
        LOGGER.warn("judgeExceptionType {}", e);
        int errorCode = e.getErrorCode();
        String errorMessage = e.getErrorMessage();
//        LOGGER.warn("judgeExceptionType errorCode: {}, errorMessage: {}", errorCode, errorMessage);
//        switch (errorCode) {
//        case -1:
//            throw new BusinessException(TwitterErrorCode.NOT_SURPORTED);
//        case 144:
//            throw new BusinessException(SocialCommonErrorCode.INVALID_MESSAGE_ID, TWITTER, "");//last param should be statusId
//        case 185:
//            throw new BusinessException(TwitterErrorCode.PUBLISH_LIMIT_EXCEEDED);
//        case 187:
//            throw new BusinessException(TwitterErrorCode.CONTENT_DUPLICATED);
//        case 186:
//            throw new BusinessException(TwitterErrorCode.CHARACTER_LENGTH_EXCEEDED);
//        case 32:
//            throw new BusinessException(SocialCommonErrorCode.NO_CREDENTIAL);
//        case 93: {
//            Long syncCount = getTodayMessageSyncCount();
//            LOGGER.info("Today, {} twitter messages have been sync.", syncCount);
//            throw new BusinessException(TwitterErrorCode.UPDATE_LIMITS);
//        }
//        case 220:
//            throw new BusinessException(TwitterErrorCode.AUTH_EXPIRE);
//        case 324:
//            throw new BusinessException(TwitterErrorCode.TOO_MANY_MEDIAS);
//        default:
//            throw new BusinessException(SocialCommonErrorCode.SOCIAL_API_RETURN_ERROR,
//                    boFacade.getMessageBundle().getString("SocialPlatformEnum.TWITTER"), errorCode, errorMessage);
//            // throw new BusinessException(TwitterErrorCode.FAIL_TO_POST_CONTENT);
//        }

    }

//
//    public Long replyToTwitter(String content, Long inReplyToStatusId, String accessToken, String accessTokenSecret) {
//        LOGGER.info("replyToTwitter BEGIN,content:{},inReplyToStatusId:{},accessToken:{},accessTokenSecret:{}", content,
//                inReplyToStatusId, accessToken, accessTokenSecret);
//        if (StringUtils.isEmpty(content)) {
//            throw new BusinessException(SocialCommonErrorCode.CONTENT_NOT_EXIST);
//        }
//
//        Twitter twitter = getTwitterInstance(accessToken, accessTokenSecret);
//        Status repliedStatus = null;
//        try {
//            StatusUpdate statusUpdate = new StatusUpdate(content);
//            statusUpdate.setInReplyToStatusId(inReplyToStatusId);
//            repliedStatus = twitter.updateStatus(statusUpdate);
//        } catch (TwitterException te) {
//            LOGGER.warn("Failed to reply to tweet: {} ", te);
//            judgeExceptionType(te);
//        }
//        if (repliedStatus != null) {
//            return Long.valueOf(repliedStatus.getId());
//        } else {
//            return null;
//        }
//    }
//
//
//    public Long postToTwitterWithImage(String content, List<File> imageFiles, String accessToken,
//            String accessTokenSecret) {
//        return toTwitterWithImage(content, null, imageFiles, accessToken, accessTokenSecret);
//    }
//
//
//    public Long replyToTwitterWithImage(String content, Long inReplyToStatusId, List<File> imageFiles,
//            String accessToken, String accessTokenSecret) {
//        return toTwitterWithImage(content, inReplyToStatusId, imageFiles, accessToken, accessTokenSecret);
//    }
//
//    private Long toTwitterWithImage(String content, Long inReplyToStatusId, List<File> imageFiles, String accessToken,
//            String accessTokenSecret) {
//        LOGGER.info("toTwitterWithImage BEGIN,content:{},inReplyToStatusId:{},accessToken:{},accessTokenSecret:{}",
//                content, inReplyToStatusId, accessToken, accessTokenSecret);
//        if (StringUtils.isEmpty(content)) {
//            throw new BusinessException(SocialCommonErrorCode.CONTENT_NOT_EXIST);
//        }
//
//        Twitter twitter = getTwitterInstance(accessToken, accessTokenSecret);
//        StatusUpdate tweetWithImages = new StatusUpdate(content);
//        List<Long> mediaIds = Lists.newArrayList();
//        try {
//            for (File imageFile : imageFiles) {
//                UploadedMedia result;
//                result = twitter.uploadMedia(imageFile);
//                mediaIds.add(result.getMediaId());
//            }
//        } catch (TwitterException te) {
//            LOGGER.warn("Failed to upload image: {} ", te);
//            throw new BusinessException(TwitterErrorCode.FAIL_TO_POST_CONTENT);
//        }
//        tweetWithImages.setMediaIds(Longs.toArray(mediaIds));
//        if (inReplyToStatusId != null) {
//            tweetWithImages.setInReplyToStatusId(inReplyToStatusId);
//        }
//        Status postedStatus = null;
//        try {
//            postedStatus = twitter.updateStatus(tweetWithImages);
//        } catch (TwitterException te) {
//            if (inReplyToStatusId != null) {
//                LOGGER.warn("Failed to reply to tweet with image files: {} ", te);
//            } else {
//                LOGGER.warn("Failed to post tweet with image files: {} ", te);
//            }
//            judgeExceptionType(te);
//        }
//        if (postedStatus != null) {
//            return Long.valueOf(postedStatus.getId());
//        } else {
//            return null;
//        }
//    }
//
//
//    public List<TwitterMessageInfo> getMessages(Long sinceId, String accessToken, String accessTokenSecret) {
//        LOGGER.info("getMessages BEGIN,sinceId is {}", sinceId);
//        Twitter twitter = getTwitterInstance(accessToken, accessTokenSecret);
//        List<TwitterMessageInfo> infos = Lists.newArrayList();
//        List<Status> allMentions = Lists.newArrayList();
//        // List<DirectMessage> directMessages = Lists.newArrayList();
//        try {
//            Paging paging = new Paging();
//            paging.setCount(200);
//            paging.setSinceId(sinceId);
//            allMentions = twitter.getMentionsTimeline(paging);
//            // directMessages = twitter.getDirectMessages(paging);
//        } catch (TwitterException te) {
//            LOGGER.warn("Failed to get twitter messages: {} ", te);
//            judgeExceptionType(te);
//        }
//
//        for (int size = 0; size < allMentions.size(); size++) {
//            Status mention = allMentions.get(size);
//            TwitterMessageInfo info = new TwitterMessageInfo();
//            if (mention.getInReplyToStatusId() != -1) {
//                info.setType("REPLY");
//                info.setSentMessageId(mention.getInReplyToStatusId());
//
//            } else {
//                info.setType("DIRECT_TWEET");
//                info.setSentMessageId(-1L);
//            }
//            info.setReplyMessageId(mention.getId());
//            info.setUserId(mention.getUser().getId());
//            info.setUserName(mention.getUser().getScreenName());
//            info.setUserImageUrl(mention.getUser().getProfileImageURL());
//            info.setCreatedTime(new DateTime(mention.getCreatedAt()));
//            info.setMessage(mention.getText());
//            List<String> imageUrls = Lists.newArrayList();
//            if (mention.getExtendedMediaEntities().length > 0) {
//                for (int i = 0; i < mention.getExtendedMediaEntities().length; i++) {
//                    imageUrls.add(mention.getExtendedMediaEntities()[i].getMediaURL());
//                }
//                info.setReplyImageUrls(imageUrls);
//            }
//            infos.add(info);
//        }
//
//        // for (int size = 0; size < directMessages.size(); size++) {
//        // DirectMessage directMessage = directMessages.get(size);
//        // TwitterMessageInfo info = new TwitterMessageInfo();
//        // info.setType("DIRECT_MESSAGE");
//        // info.setReplyMessageId(directMessage.getId());
//        // info.setUserId(directMessage.getSender().getId());
//        // info.setUserName(directMessage.getSender().getScreenName());
//        // info.setUserImageUrl(directMessage.getSender().getProfileImageURL());
//        // info.setCreatedTime(new DateTime(directMessage.getCreatedAt()));
//        // info.setMessage(directMessage.getText());
//        // info.setSentMessageId(-1L);
//        // List<String> imageUrls = Lists.newArrayList();
//        // if (directMessage.getMediaEntities().length > 0) {
//        // for (int i = 0; i < directMessage.getMediaEntities().length; i++) {
//        // imageUrls.add(directMessage.getMediaEntities()[i].getExpandedURL());
//        // }
//        // info.setReplyImageUrls(imageUrls);
//        // }
//        // infos.add(info);
//        // }
//        LOGGER.info("getMessages END,sinceId is {},{} messages retrieved", sinceId, infos.size());
//        return infos;
//    }

    private void checkAccessToken(String accessToken, String accessTokenSecret) {
        if (StringUtils.isEmpty(accessToken) || StringUtils.isEmpty(accessTokenSecret)) {
            throw new BusinessException("AUTH_EXPIRE");
        }
    }

    private Twitter getTwitterInstance(String accessToken, String accessTokenSecret) {
        LOGGER.info("getTwitterInstance BEGIN, accessToken is {"+accessToken+"},accessTokenSecret is {"+accessTokenSecret+"}");
        checkAccessToken(accessToken, accessTokenSecret);
        String key = accessToken + accessTokenSecret;
        if (twitterMap.get(key) == null) {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(CmtClient.getInstance().getGlobalSettings("Global.Twitter.ConsumerKey"))
                    .setOAuthConsumerSecret(CmtClient.getInstance().getGlobalSettings("Global.Twitter.ConsumerSecret"))
                    .setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessTokenSecret);

            if (MockServerUtil.isMockServer("Global.Twitter.UseMockServer")) {
                cb.setRestBaseURL(MockServerUtil.getMockServerUrl("twitter"));
            } else {
                String proxyHost = CmtClient.getInstance().getGlobalSettings("Global.Proxy.Host");
                String proxyPort = CmtClient.getInstance().getGlobalSettings("Global.Proxy.Port");
                if (!StringUtils.isEmpty(proxyHost) && !StringUtils.isEmpty(proxyPort)) {
                    cb.setHttpProxyHost(proxyHost).setHttpProxyPort(Integer.parseInt(proxyPort));
                }
            }
            TwitterFactory tf = new TwitterFactory(cb.build());
            twitterMap.put(key, tf.getInstance());
        }
        return twitterMap.get(key);
    }

//
//    public Integer[] getStatistics(Long statusId, String accessToken, String accessTokenSecret) {
//        LOGGER.info("getStatistics BEGIN,statusId is {}", statusId);
//        Twitter twitter = getTwitterInstance(accessToken, accessTokenSecret);
//        Status status = null;
//        List<Status> retweets = null;
//        int impressionsCount = 0;
//        Integer[] res = new Integer[3];
//        try {
//            status = twitter.showStatus(statusId);
//            retweets = twitter.getRetweets(statusId);
//            impressionsCount = status.getUser().getFollowersCount();
//            if (retweets != null) {
//                for (Status retweet : retweets) {
//                    impressionsCount += retweet.getUser().getFollowersCount();
//                }
//            }
//            res[0] = Integer.valueOf(status.getRetweetCount());
//            res[1] = Integer.valueOf(status.getFavoriteCount());
//            res[2] = Integer.valueOf(impressionsCount);
//        } catch (TwitterException e) {
//            LOGGER.warn("Encounter error when showing status", e);
//            judgeExceptionType(e);
//        }
//        LOGGER.info("getStatistics END,statusId is {}", statusId);
//        return res;
//    }
//
//    /**
//     * The query is according to user identifier
//     *
//     * @param screenNames
//     *            List of screen names
//     * @return
//     */
//
//    public List<SocialUserInfo> lookupUsers(String[] screenNames, String accessToken, String accessTokenSecret) {
//        LOGGER.info("lookupUsers BEGIN, screenNames is {}, ({},{})",screenNames,accessToken,accessTokenSecret);
//        Twitter twitter = getTwitterInstance(accessToken, accessTokenSecret);
//
//        List<SocialUserInfo> userInfos = Lists.newArrayList();
//        List<User> searchResults = Lists.newArrayList();
//
//        try {
//            searchResults = twitter.lookupUsers(screenNames);
//        } catch (TwitterException e) {
//            // no expired access token exception for twitter
//            LOGGER.warn("Encounter error when lookupUsers '{}' in twitter.", screenNames, e);
//            judgeExceptionType(e);
//        }
//
//        for (int i = 0; i < searchResults.size(); i++) {
//            User user = searchResults.get(i);
//            SocialUserInfo userInfo = getSocialUserInfoByTwitterUser(user);
//            userInfos.add(userInfo);
//        }
//        LOGGER.info("lookupUsers END");
//        return userInfos;
//    }

    /**
     * the query is free style
     * 
     * @param query
     * @param page
     * @return
     */
    
    public String searchUsers(String query, int page, String accessToken, String accessTokenSecret) {
        LOGGER.info("searchUsers BEGIN,query:{},page:{},({},{})");
        Twitter twitter = getTwitterInstance(accessToken, accessTokenSecret);
        //List<SocialUserInfo> userInfos = Lists.newArrayList();
        List<User> searchResults = new ArrayList();
        String result="";
        try {
            searchResults = twitter.searchUsers(query, page);
        } catch (TwitterException e) {
            LOGGER.warn("searchUsers TwitterException {} when searching" + query, e);
            judgeExceptionType(e);
        }

        for (int i = 0; i < searchResults.size(); i++) {
            User user = searchResults.get(i);
            //SocialUserInfo userInfo = getSocialUserInfoByTwitterUser(user);
            //userInfos.add(userInfo);
            result+=(","+user.getScreenName());
        }
        LOGGER.info("searchUsers END");
        return result;
    }

//    private SocialUserInfo getSocialUserInfoByTwitterUser(User user) {
//        SocialUserInfo userInfo = new SocialUserInfo();
//        userInfo.setName(user.getName());
//        userInfo.setScreenName(user.getScreenName());
//        userInfo.setDescription(user.getDescription());
//        userInfo.setImageUrl(user.getOriginalProfileImageURLHttps());
//        userInfo.setHomepageUrl("https://twitter.com/" + user.getScreenName());
//        userInfo.setWebsite(user.getURLEntity().getDisplayURL());
//        return userInfo;
//    }
//
//
//    public List<CustomerSocialMessageInfo> getCustomerFeeds(String screenName, Long sinceId, String accessToken, String accessTokenSecret) {
//        LOGGER.info("getCustomerFeeds BEGIN,screenName:{},sinceId:{},({},{})", screenName, sinceId,accessToken,accessTokenSecret);
//        Twitter twitter = getTwitterInstance(accessToken, accessTokenSecret);
//        List<CustomerSocialMessageInfo> res = Lists.newArrayList();
//        List<Status> customerTweets = Lists.newArrayList();
//        try {
//            Paging paging = new Paging();
//            paging.setCount(200);
//            paging.setSinceId(sinceId);
//            customerTweets = twitter.getUserTimeline(screenName, paging);
//        } catch (TwitterException e) {
//            LOGGER.warn("Encounter error {} when searching customer tweets with account" + screenName, e);
//            judgeExceptionType(e);
//        }
//        for (int i = 0; i < customerTweets.size(); i++) {
//            Status customerTweet = customerTweets.get(i);
//            CustomerSocialMessageInfo tweet = new CustomerSocialMessageInfo();
//            tweet.setId(String.valueOf(customerTweet.getId()));
//            tweet.setText(customerTweet.getText());
//            tweet.setCreatedAt(new DateTime(customerTweet.getCreatedAt()));
//            res.add(tweet);
//        }
//        LOGGER.info("getCustomerFeeds END");
//        return res;
//    }
//
//
//    public Object[] getCustomerTwitterAccountInfo(String screenName, String accessToken, String accessTokenSecret) {
//        LOGGER.info("getCustomerTwitterAccountInfo BEGIN,screenName:{},({},{})", screenName,accessToken,accessTokenSecret);
//        Twitter twitter = getTwitterInstance(accessToken, accessTokenSecret);
//        Object[] res = new Object[4];
//        try {
//            User user = twitter.showUser(screenName);
//            res[0] = user.getProfileImageURL();
//            res[1] = user.getDescription();
//            res[2] = user.getFollowersCount();
//            res[3] = user.getFriendsCount();
//        } catch (TwitterException e) {
//            LOGGER.warn("Encounter error {} when searching customer tweets with account" + screenName, e);
//            judgeExceptionType(e);
//        }
//        LOGGER.info("getCustomerTwitterAccountInfo END");
//        return res;
//    }
//


}
