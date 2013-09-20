package com.plfort.stringoperation;
public class TestYoutubeSelenium{
	

	public static void main(String[] args){
		
		YoutubeSelenium youtubeTest = new YoutubeSelenium();
		youtubeTest.addYoutubeId("5NV6Rdv1a3I");
		youtubeTest.addYoutubeId("_8Vus3F971I");
		try {
			youtubeTest.execute();
		} catch (Exception e) {
		
			e.printStackTrace();
		}

		
	}
}
