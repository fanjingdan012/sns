package com.fjd.cli;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import com.fjd.instagram.InstagramCmd;
import com.fjd.mailchimp.MailchimpCmd;
import com.fjd.twitter.TwitterCmd;
import com.fjd.weibo.WeiboCmd;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.StringUtils;

import com.fjd.facebook.FacebookCmd;
import com.fjd.pinterest.PinterestCmd;
import com.fjd.social.CmtClient;

public class SocialTokenTool {
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args)  {

		cli(args);
	}

	public static void cli(String[] args){
		ResourceBundle msgBundle = ResourceBundle.getBundle("globalization/message", Locale.getDefault());

		final Options options = new Options();
		options.addOption("h", false, msgBundle.getString("HELP"));
		// social channel
		options.addOption("c", true, "channel: twitter, facebook, instagram, pinterest, weibo, mailchimp");
		// action
		options.addOption("v", true, "validate access token,following with access token to validate");
		options.addOption("auth", false, "oauth, please copy code and use -g to exchange access token");
		options.addOption("g", true, "get access token, following with code");
		final CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			if (cmd.getOptions().length < 0 || cmd.hasOption('h')) {
				HelpFormatter hf = new HelpFormatter();
				hf.printHelp("Options", options);
			} else {
				String channel = cmd.getOptionValue("c");
				SocialCmd sc = null;
				if (StringUtils.isEmpty(channel)) {
					throw new BusinessException(
							"channel is empty, please choose a channel. twitter, facebook, instagram, pinterest, mailchimp.");
				} else {
					if ("facebook".equals(channel)) {
						sc = new FacebookCmd(CmtClient.getInstance());
					} else if ("twitter".equals(channel)) {
						sc = new TwitterCmd(CmtClient.getInstance());
					} else if ("pinterest".equals(channel)) {
						sc = new PinterestCmd(CmtClient.getInstance());
					} else if ("instagram".equals(channel)) {
						sc = new InstagramCmd(CmtClient.getInstance());
					} else if ("weibo".equals(channel)) {
						sc = new WeiboCmd(CmtClient.getInstance());
					} else if ("mailchimp".equals(channel)) {
						sc = new MailchimpCmd(CmtClient.getInstance());
					} else {
						throw new BusinessException(
								"channel is not supported, please choose a channel. twitter, facebook, instagram, pinterest, mailchimp.");
					}
				}
				if (cmd.hasOption('v')) {
					String accessToken = cmd.getOptionValue("v");
					if (StringUtils.isEmpty(accessToken)) {
						throw new BusinessException("accessToken is empty.");
					}
					sc.validateAccessToken(accessToken);
				} else if (cmd.hasOption('g')) {
					String code = cmd.getOptionValue("g");
					if (StringUtils.isEmpty(code)) {
						throw new BusinessException("code is empty.");
					}
					sc.getAccessToken(code);
				} else if (cmd.hasOption("auth")) {
					sc.getCode();
				}else {
					throw new BusinessException("you must choose an action either -v or -g or -auth");
				}

			}
		} catch (final ParseException e) {
			System.err.println("parser command line error! " + e);
			System.exit(1);
		} catch (BusinessException e) {
			System.err.println(e.getDescription()+e);
			System.exit(1);
		}catch(Exception e){
			System.err.println(e.getMessage()+e);
			System.exit(1);
		}

	}
}
