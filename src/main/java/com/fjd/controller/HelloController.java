package com.fjd.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fjd.facebook.FacebookClientServiceImpl;
import com.fjd.facebook.FacebookOauthServiceImpl;

@Controller
public class HelloController {
    /**
     * Be sure to specify the name of your application. If the application name is {@code null} or
     * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
     */

	 private static String appId = "";
	    private static String appSecret = "";
	    private static String redirectUri = "";
	    private static String proxyHost = "";
	    private static String proxyPort = "";



    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String printWelcome(ModelMap model) {

        model.addAttribute("message", "Spring 3 MVC Hello World");
        return "hello";

    }

    @RequestMapping(value = "/hello/{name:.+}", method = RequestMethod.GET)
    public ModelAndView hello(@PathVariable("name") String name) {
        ModelAndView model = new ModelAndView();
        model.setViewName("hello");
        model.addObject("msg", name);
        return model;
    }

    @RequestMapping(value = "/gmail", method = RequestMethod.GET)
    public ModelAndView gmail(String name, HttpServletResponse response) throws Exception {
        /*
         * GoogleOAuth oauth = new GoogleOAuth();
         * oauth.accessTokenRequest(response);
         */

FacebookOauthServiceImpl fbos=new FacebookOauthServiceImpl();

        String url =fbos.getAuthorizationUrl(appId,appSecret,redirectUri);
        response.sendRedirect(url);
        ModelAndView model = new ModelAndView();
        model.setViewName("facebook");
        return model;
    }

    // @RequestMapping(value = "/receive", method = RequestMethod.POST)
    // public ModelAndView oauth2callback(HttpServletRequest request, HttpServletResponse response) throws Exception {
    // append("./messagesPPPPPPPPPPPPPPPPPPPPPPPPPPPPP.txt", "messagereceived\n");
    // ServletInputStream reader = request.getInputStream();
    // // Parse the JSON message to the POJO model class.
    // JsonParser parser = JacksonFactory.getDefaultInstance().createJsonParser(reader);
    // parser.skipToKey("message");
    // PubsubMessage message = parser.parseAndClose(PubsubMessage.class);
    // // Base64-decode the data and work with it.
    // String data = new String(message.decodeData(), "UTF-8");
    // append("./messagesPPPPPPPPPPPPPPPPPPPPPPPPPPPPP.txt", "messageid:" + message.getMessageId() + "\n");
    // // Work with your message
    // // Respond with a 20X to acknowledge receipt of the message.
    // response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    //
    // System.out.println(message.getMessageId());
    // response.getWriter().write(message.getMessageId());
    // // response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    //
    // response.getWriter().close();
    // ModelAndView model = new ModelAndView();
    // model.addObject("message", "messageid:" + message.getMessageId());
    // return model;
    // }

    public static void append(String fileName, String content) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {

            // if file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // @RequestMapping(value = "/", method = RequestMethod.GET)
    // public ModelAndView receive(String name, HttpServletResponse response) throws Exception {
    // /*
    // * GoogleOAuth oauth = new GoogleOAuth();
    // * oauth.accessTokenRequest(response);
    // */
    //
    // GoogleAuthorizationCodeFlow flow = AuthUtil.getFlow();
    //
    // String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
    // response.sendRedirect(url);
    // ModelAndView model = new ModelAndView();
    // model.setViewName("gmail");
    // return model;
    // }



    // @RequestMapping(value = "/oauth2callback", method = RequestMethod.GET)
    // public ModelAndView oauth2callback(HttpServletRequest request, HttpServletResponse response) throws Exception {
    //
    // System.out.println("oauth2callback");
    //
    // String code = request.getParameter("code");
    // requestCode = code;
    //
    // // String token = GoogleOAuth.getAccessToken(code);
    // GoogleCredential credential = AuthUtil.exchangeCode(code);
    //
    // Gmail service = EmailSender.createGmailService(credential);
    // /*
    // * MimeMessage email = EmailSender.createEmail("shaoqin.chen@sap.com", "chenshao0594@126.com", "test",
    // * "work good");
    // * EmailSender.sendMessage(service, "chenshao0594@gmail.com", email);
    // */
    //
    // String q = "in:inbox";
    // // ListMessagesResponse messageResp = service.users().messages().list(userGmailAddress).setQ(q).execute();
    //
    // List<Message> messages = new ArrayList<Message>();
    // if (startHistoryId == null) {
    // messages.addAll(listMessagesMatchingQuery(service, userGmailAddress, q));
    // } else {
    // messages.addAll(listHistory(service, userGmailAddress, startHistoryId));
    // }
    // // messageResp.getMessages());//
    // /*
    // * while (messageResp.getMessages() != null) {
    // * messages.addAll(messageResp.getMessages());
    // * if (messageResp.getNextPageToken() != null) {
    // * String pageToken = messageResp.getNextPageToken();
    // * messageResp = service.users().messages().list(userGmailAddress).setQ(null)
    // * .setPageToken(pageToken).execute();
    // * } else {
    // * break;
    // * }
    // * }
    // */
    //
    // System.out.println(messages.size());
    // Message m;
    // if (messages.isEmpty()) {
    // m = this.lastMessage;
    // } else {
    // m = messages.get(0);
    // }
    // if (m == null) {
    // ModelAndView model = new ModelAndView();
    // model.addObject("subject", "");
    // model.addObject("from", "");
    // model.addObject("recipient", "");
    // model.addObject("content", "");
    // model.addObject("messageid", "");
    // model.addObject("number", messages.size());
    // model.setViewName("gmail");
    // return model;
    // }
    // lastMessage = m;
    // Message message = service.users().messages().get(userGmailAddress, m.getId()).setFormat("raw").execute();
    //
    // System.out.println(message.toPrettyString());
    //
    // byte[] emailBytes = Base64.decodeBase64(message.getRaw());
    //
    // Properties props = new Properties();
    // Session session = Session.getDefaultInstance(props, null);
    //
    // MimeMessage mime = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
    //
    // System.out.println(mime.getSubject());
    // System.out.println(mime.getFrom().toString());
    // System.out.println("------------------------");
    // String recipient = "";
    // for (Address each : mime.getRecipients(RecipientType.TO)) {
    // recipient = each.toString();
    // }
    // System.out.println("------------------------");
    //
    // System.out.println(mime.getContent().toString());
    // startHistoryId = message.getHistoryId();
    // ModelAndView model = new ModelAndView();
    // model.addObject("subject", mime.getSubject());
    // model.addObject("from", ((InternetAddress) mime.getFrom()[0]).getAddress());
    // model.addObject("recipient", recipient);
    // model.addObject("content", mime.getContent().toString());
    // model.addObject("messageid", mime.getMessageID());
    // model.addObject("number", messages.size());
    // model.setViewName("gmail");
    // return model;
    //
    // }
    //
    // public static List<Message> listHistory(Gmail service, String userId, BigInteger startHistoryId) throws
    // IOException {
    // List<History> histories = new ArrayList<History>();
    // ListHistoryResponse response = service.users().history().list(userId).setStartHistoryId(startHistoryId)
    // .execute();
    // while (response.getHistory() != null) {
    // histories.addAll(response.getHistory());
    // if (response.getNextPageToken() != null) {
    // String pageToken = response.getNextPageToken();
    // response = service.users().history().list(userId).setPageToken(pageToken)
    // .setStartHistoryId(startHistoryId).execute();
    // } else {
    // break;
    // }
    // }
    // List<Message> messages = new ArrayList<Message>();
    // for (int i = histories.size() - 1; i >= 0; i--) {
    // History history = histories.get(i);
    // List<HistoryMessageAdded> hmaList = new ArrayList();
    // if (history.getMessagesAdded() != null)
    // hmaList.addAll(history.getMessagesAdded());
    //
    // for (int j = hmaList.size() - 1; j >= 0; j--) {
    // HistoryMessageAdded hma = hmaList.get(j);
    // messages.add(hma.getMessage());
    // }
    // System.out.println(history.toPrettyString());
    // }
    // return messages;
    // }

    @RequestMapping(value = "/oauth2callback", method = RequestMethod.GET)
    public ModelAndView oauth2fetchnew(HttpServletRequest request, HttpServletResponse response) throws Exception {

        System.out.println("oauth2fetchnew");

        String code = request.getParameter("code");
        FacebookOauthServiceImpl fbos = new FacebookOauthServiceImpl();
        Map<String,Object> token = fbos.getAccessToken(appId,appSecret,proxyHost,proxyPort,redirectUri,code);
        // String token = GoogleOAuth.getAccessToken(code);
        /*
         * MimeMessage email = EmailSender.createEmail("shaoqin.chen@sap.com", "chenshao0594@126.com", "test",
         * "work good");
         * EmailSender.sendMessage(service, "chenshao0594@gmail.com", email);
         */
        FacebookClientServiceImpl fbcs = new FacebookClientServiceImpl();
        Facebook facebook = new FacebookTemplate((String)(token.get("access_token")));
      FacebookClientServiceImpl.addProxy("proxy.sin.sap.corp","8080",null);
      String result = fbcs.searchUsersWork(facebook, "a", 0);
      System.out.println("is working:"+result);
        System.out.println("------------------------");

        ModelAndView model = new ModelAndView();
        // model.addObject("subject", mime.getSubject());
        // model.addObject("from", ((InternetAddress) mime.getFrom()[0]).getAddress());
        // model.addObject("recipient", recipient);
        // model.addObject("content", mime.getContent().toString());
        // model.addObject("messageid", mime.getMessageID());
        model.addObject("subject", "");
        model.addObject("from", "");
        model.addObject("recipient", "");
        model.addObject("content", "");
        model.addObject("messageid",result);
        model.setViewName("gmail");
        return model;

    }
}
