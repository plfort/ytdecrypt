package com.plfort.stringoperation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plfort.stringoperation.operation.StringAlgo;
import com.plfort.stringoperation.test.TestResult;
import com.thoughtworks.selenium.DefaultSelenium;

public class YoutubeSelenium {
	private final static String SEPARATOR = "#|#";
	private final static int TIME_TO_WAIT_FOR_VIDEO = 8000;
	private final static String YOUTUBE_BASE_URL = "http://www.youtube.com";
	private final List<String> youtubeIds = new ArrayList<String>();
	private Logger logger = Logger.getLogger(YoutubeSelenium.class
			.getName());
	final ObjectMapper mapper = new ObjectMapper();
	private DefaultSelenium selenium;
	private ArrayList<StringAlgo> decryptSigAlgoList;

	public void execute() throws Exception {
		DOMConfigurator.configure("log4j.xml");
		java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.WARNING);
				
		decryptSigAlgoList = (ArrayList<StringAlgo>) mapper.readValue(new File(
				"./json/youtube-dl.json"), mapper.getTypeFactory()
				.constructCollectionType(ArrayList.class, StringAlgo.class));

		RemoteControlConfiguration rcc = new RemoteControlConfiguration();
		rcc.setPort(4444);
		rcc.setTrustAllSSLCertificates(true);
		rcc.setFirefoxProfileTemplate(new File("/home/pilou/.mozilla/firefox/jxno61x7.SeleniumYoutube"));
		SeleniumServer srvr = new SeleniumServer(rcc);
		srvr.start();
		selenium = new DefaultSelenium("localhost", 4444, "*firefox",
				YOUTUBE_BASE_URL);
		selenium.start("captureNetworkTraffic=true");
		HashMap<Integer, List<TestResult>> testResultsMap = new HashMap<Integer, List<TestResult>>();
		try {
			for (String youtubeId : youtubeIds) {
				TestResult testResult = testDecrypt(youtubeId);
				if(testResult != null){
					if(testResultsMap.containsKey(testResult.encryptedSigLength)){
						testResultsMap.get(testResult.encryptedSigLength).add(testResult);
					}else{
						ArrayList<TestResult> testResultsArray = new ArrayList<TestResult>();
						testResultsArray.add(testResult);
						testResultsMap.put(testResult.encryptedSigLength, testResultsArray);
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		writeResultAsHtml(testResultsMap);
		selenium.close();
		srvr.stop();
		

	}

	public TestResult testDecrypt(String youtubeId) throws Exception {

		selenium.open("/watch?v=" + youtubeId);

		selenium.waitForPageToLoad("15000");

		Map<String, String> sigs = extractSigFromFmt(selenium.getHtmlSource());
		logger.info("Encrypted sigs size : " + sigs.size());
		if (sigs.size() == 0) {
			System.out.println(String.format(
					"Skip youtubeId %s, no encrypted sig found", youtubeId));
			return null;
		}
		Thread.sleep(TIME_TO_WAIT_FOR_VIDEO);

		String trafficOutput = selenium.captureNetworkTraffic("json");

		Map<String, String> decryptedSig = extractDecryptedSigFromNetworkCapture(trafficOutput);

		// get the encrypted sig from the current played video itag
		// System.out.println(sigs);
		logger.debug(sigs);
		String itag = decryptedSig.get("itag");
		String encryptedSig = sigs.get(itag);
		// get the corresponding decrypted sig
		String decryptedSigString = decryptedSig.get("signature");
		if (encryptedSig != null && decryptedSigString != null) {
			logger.info("Encrypted sig : " + encryptedSig);
			logger.info("Encrypted signature length : " + encryptedSig.length());
			for (StringAlgo dsa : decryptSigAlgoList) {

				if (dsa.stringLengthCondition == encryptedSig.length()) {
					String selfDecryptedSig = dsa.evaluate(encryptedSig);

					logger.info("Youtube decrypted sig : " + decryptedSigString);
					logger.info("Self decrypted sig    : " + selfDecryptedSig);
					if (!decryptedSigString.equals(selfDecryptedSig)) {
						logger.error("Decrypted signature does not match");
					}
					
					return new TestResult(youtubeId,itag,encryptedSig,decryptedSigString,selfDecryptedSig);
				

				}
			}
		} else {

			logger.debug(encryptedSig + " " + decryptedSig);
		}
		return null;
	}

	private Map<String, String> extractDecryptedSigFromNetworkCapture(
			String capture) throws IOException {
		Map<String, String> kvPairs = new HashMap<String, String>();
		Pattern p = Pattern.compile("\"url\":\"(.*?)videoplayback(.*?)\"");
		Matcher m = p.matcher(capture);
		List<String> matches = new ArrayList<String>();
		while (m.find()) {

			matches.add(m.group());
		}
		String urls[] = matches.get(0).split(",");
		for (String ppUrl : urls) {
			String url = URLDecoder.decode(ppUrl, "UTF-8");

			String[] components = url.replaceFirst("\"url\":\"", "").split("&");

			for (String component : components) {
				if (component.contains("=")) {
					String[] keyValueSplit = component.split("=", 2);
					kvPairs.put(keyValueSplit[0], keyValueSplit[1]);
				}
			}

		}
		return kvPairs;
	}

	private Map<String, String> extractSigFromFmt(String html)
			throws IOException {

		html = html.replace("\\u0026", SEPARATOR);
		Pattern p1 = Pattern
				.compile("fmt_stream_map\":( )?\"(.*?)?\"|adaptive_fmts\":( )?\"(.*?)?\"");

		Matcher m1 = p1.matcher(html);
		String concatcMatches = "";
		while (m1.find()) {

			concatcMatches += m1.group() + ",";
		}
		String urls[] = concatcMatches.split(",");
		HashMap<String, String> foundArray = new HashMap<String, String>();

		for (String ppUrl : urls) {

			String url = URLDecoder.decode(ppUrl, "UTF-8");

			String tmpUrl = url.replaceFirst("fmt_stream_map\":( )?\"", "")
					.replaceFirst("adaptive_fmts\":( )?\"", "");
			if ('"' == tmpUrl.charAt(tmpUrl.length() - 1)) {
				tmpUrl = tmpUrl.substring(0, tmpUrl.length() - 1);
			}
			String[] components = tmpUrl.split(SEPARATOR);

			Map<String, String> kvPairs = new HashMap<String, String>();
			for (String component : components) {
				if (component.contains("=")) {
					String[] keyValueSplit = component.split("=", 2);
					kvPairs.put(keyValueSplit[0], keyValueSplit[1]);
				}
			}

			String itag = null;
			itag = kvPairs.get("itag");

			if (itag != null) {
				String s = null;
				s = kvPairs.get("s");
				if (s != null) {
					logger.debug("Sig found in fmt for itag " + itag + " : "
							+ s);

					foundArray.put(itag, s);
				}
			}
		}
		// System.out.print(foundArray);
		return foundArray;
	}

	private void writeResultAsHtml(Map<Integer,List<TestResult>> results){
		StringBuilder sb = new StringBuilder();
		sb.append("<!doctype html><html><head><title>YoutbeDecrypt test results</title>" +
				"<link href=\"css/bootstrap.min.css\" rel=\"stylesheet\">" +
				"<link href=\"css/tests.css\" rel=\"stylesheet\">" +
				"</head><body>");
		sb.append("<h1>YoutbeDecrypt test results</h1>");
		Iterator<Entry<Integer, List<TestResult>>>  it = results.entrySet().iterator();
		while(it.hasNext()){
			Entry<Integer, List<TestResult>> entry = it.next();
			sb.append(String.format("<h2>Signature length : %d</h2>", entry.getKey()));
			sb.append("<table class=\"table\">");
			sb.append("<tr><th>Result</th><th>Youtube Id</th><th>Itag</th><th>Signatures</th></tr>");
			List<TestResult> list = entry.getValue();
			for(TestResult t : list){
				sb.append(t.toHtmlTr());
			}
		
			sb.append("</table>");
			
		}
		sb.append("</body></html>");
		
		 DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		
		BufferedWriter out=null;
		try {
			out = new BufferedWriter(new FileWriter("./testResult/testResult "+dateFormat.format(new Date())+".html"));
			out.write(sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	
	public void addYoutubeId(String youtubeId) {
		youtubeIds.add(youtubeId);
	}
}
