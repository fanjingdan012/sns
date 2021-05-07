package com.fjd.cli;

import java.io.IOException;

import org.junit.Test;

import com.fjd.cli.SocialTokenTool;

public class SocialTokenToolTest {
	//@Test
	 public void testHelp()  { 
		 String args[]={"-h"}; 
		 SocialTokenTool.cli(args);
		
	 } 

	//@Test
	 public void testNoArgs() { 
		 String args[] = new String[0]; 
		 SocialTokenTool.cli(args);
		 //assertEquals(1, RMDataSource.simpleTest(args)); 
	 } 

	 //@Test
	 public void testAuth()  { 
		 String args[] = new String[]{"-c","mailchimp","-auth"};
		 SocialTokenTool.cli(args);
		// assertEquals(0, RMDataSource.simpleTest(args)); 
	 }
	 //@Test
	 public void testGetAccessToken()  { 
		 String args[] = new String[]{"-c","mailchimp","-g","61782c6021293c24bf9dbff5942bcded"};
		 SocialTokenTool.cli(args);
		// assertEquals(0, RMDataSource.simpleTest(args)); 
	 }
	@Test
	 public void testValidateAccessToken()  { 
		 String args[] = new String[]{"-c","mailchimp","-v","2ce7dc8651d875b312ef629b44b76977"};
		 SocialTokenTool.cli(args);
		// assertEquals(0, RMDataSource.simpleTest(args)); 
	 }
	 
}
