package com.fjd.cli;

import java.io.IOException;

public interface SocialCmd {
	String validateAccessToken(String accessToken) throws IOException;
	String getAccessToken(String code);
	void getCode();
	
}
