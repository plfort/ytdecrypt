package com.plfort.stringoperation.test;

public class TestResult {

	public String youtubeId;
	public String itag;
	public int encryptedSigLength;
	public String encryptedSig;
	public String youtubeDecryptedSig;
	public String selfDecryptedSig;
	public boolean success;
	
	public TestResult(String youtubeId,String itag, String eSig, String yDSig, String sDSig){
		this.youtubeId = youtubeId;
		this.itag = itag;
		this.encryptedSig = eSig;
		this.encryptedSigLength = eSig.length();
		this.youtubeDecryptedSig = yDSig;
		this.selfDecryptedSig = sDSig;
		this.success = youtubeDecryptedSig.equals(selfDecryptedSig);
	}
	
	public String toHtmlTr(){
		return String.format("<tr><td  class=\"result %s\">%s</td><td >%s</td><td>%s</td><td>" +
				"Encrypted : %s<br/>" +
				"Decrypted Youtube : %s<br/>" +
				"Decrypted by algo : %s<br/>" +
				"</td></tr>",
				success == true ?"success":"error",success == true ?"OK":"NOK",youtubeId,itag,encryptedSig,youtubeDecryptedSig,selfDecryptedSig);
	
	}
	
}
