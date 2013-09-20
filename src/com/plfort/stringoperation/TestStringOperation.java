package com.plfort.stringoperation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plfort.stringoperation.operation.StringAlgo;

public class TestStringOperation {

	private final static HashMap<String, String> testSignature = new HashMap<String, String>();

	private static void initSigMap() {
		// 93
		testSignature
				.put("qwertyuioplkjhgfdsazxcvbnm1234567890QWERTYUIOPLKJHGFDSAZXCVBNM!@#$%^&*()_-+={[]}|:;?/>.<'`~\"€",
						".>/?;:|}][{=+-_)(*&^%$#@!MNBVCXZASDFGHJKLPOIUYTREWQ098765'321mnbvcxzasdfghjklpoiu");
		// 92
		testSignature
				.put("qwertyuioplkjhgfdsazxcvbnm1234567890QWERTYUIOPLKJHGFDSAZXCVBNM!@#$%^&*()_-+={[]}|:;?/>.<'`~\"",
						"mrtyuioplkjhgfdsazxcvbnq1234567890QWERTY}IOPLKJHGFDSAZXCVBNM!@#$%^&*()_-+={[]\"|:;");
		// 91
		testSignature
				.put("qwertyuioplkjhgfdsazxcvbnm1234567890QWERTYUIOPLKJHGFDSAZXCVBNM!@#$%^&*()_-+={[]}|:;?/>.<'`~",
						"/?;:|}][{=+-_)(*&^%$#@!MNBVCXZASDFGHJKLPOIUYTREWQ09876543.1mnbvcxzasdfghjklpoiu");
		// 90
		testSignature
				.put("qwertyuioplkjhgfdsazxcvbnm1234567890QWERTYUIOPLKJHGFDSAZXCVBNM!@#$%^&*()_-+={[]}|:;?/>.<'`",
						"mrtyuioplkjhgfdsazxcvbne1234567890QWER[YUIOPLKJHGFDSAZXCVBNM!@#$%^&*()_-+={`]}|");
		// 89
		testSignature
				.put("qwertyuioplkjhgfdsazxcvbnm1234567890QWERTYUIOPLKJHGFDSAZXCVBNM!@#$%^&*()_-+={[]}|:;?/>.<'",
						"/?;:|}<[{=+-_)(*&^%$#@!MqBVCXZASDFGHJKLPOIUYTREWQ0987654321mnbvcxzasdfghjklpoiuyt");
		// 88
		testSignature
				.put("qwertyuioplkjhgfdsazxcvbnm1234567890QWERTYUIOPLKJHGFDSAZXCVBNM!@#$%^&*()_-+={[]}|:;?/>.<",
						"ioplkjhgfdsazxcvbnm12<4567890QWERTYUIOZLKJHGFDSAeXCVBNM!@#$%^&*()_-+={[]}|:;?/>.3");
		// 87
		testSignature
				.put("qwertyuioplkjhgfdsazxcvbnm1234567890QWERTYUIOPLKJHGFDSAZXCVBNM!@#$^&*()_-+={[]}|:;?/>.<",
						"uioplkjhgfdsazxcvbnm1t34567890QWE2TYUIOPLKJHGFDSAZXCVeNM!@#$^&*()_-+={[]}|:;?/>.<");
		// 86
		testSignature
				.put("qwertyuioplkjhgfdsazxcvbnm1234567890QWERTYUIOPLKJHGFDSAZXCVBNM!@#$%^&*()_-+={[|};?/>.<",
						"yuioplkjhgfdsazxcvbnm12345678q0QWrRTYUIOELKJHGFD-AZXCVBNM!@#$%^&*()_<+={[|};?/>.S");
		// 85
		testSignature
				.put("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!\"#$%&\'()*+,-./:;<=>?@[",
						"3456789a0cdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRS[UVWXYZ!\"#$%&\'()*+,-./:;<=>?@");
		// 84
		testSignature
				.put("qwertyuioplkjhgfdsazxcvbnm1234567890QWERTYUIOPLKJHGFDSAZXCVBNM!@#$%^&*()_-+={[};?>.<",
						">?;}[{=+-_)(*&^%$#@!MNBVCXZASDFGHJKLPOIUYTREWq0987654321mnbvcxzasdfghjklpoiuytr");
		// 83
		testSignature
				.put("qwertyuioplkjhgfdsazxcvbnm1234567890QWERTYUIOPLKJHGFDSAZXCVBNM!#$%^&*()_+={[};?/>.<",
						".>/?;}[{=+_)(*&^%<#!MNBVCXZASPFGHJKLwOIUYTREWQ0987654321mnbvcxzasdfghjklpoiuytreq");
		// 82
		testSignature
				.put("qwertyuioplkjhgfdsazxcvbnm1234567890QWERTYUIOPLKHGFDSAZXCVBNM!@#$%^&*(-+={[};?/>.<",
						".>/?;}[<=+-(*&^%$#@!MNBVCXeASDFGHKLPOqUYTREWQ0987654321mnbvcxzasdfghjklpoiuytrIwZ");
		// 81
		testSignature
				.put("qwertyuioplkjhgfdsazxcvbnm1234567890QWERTYUIOPLKHGFDSAZXCVBNM!@#$%^&*(-+={[};?/>.",
						"C>/?;}[{=+-(*&^%$#@!MNBVYXZASDFGHKLPOIU.TREWQ0q87659321mnbvcxzasdfghjkl4oiuytrewp");
		// 80
		testSignature
				.put("qwertyuioplkjhgfdsazxcvbnm1234567890QWERTYUIOPLKHGFDSAZXCVBNM!@#$%^&*(-+={[};?/>",
						"wertyuioplkjhgfdsaqxcvbnm1234567890QWERTYUIOPLKHGFDSAZXCVBNM!@#$%^&z(-+={[};?/>");
		// 79
		testSignature
				.put("qwertyuioplkjhgfdsazxcvbnm1234567890QWERTYUIOPLKHGFDSAZXCVBNM!@#$%^&*(-+={[};?/",
						"Z?;}[{=+-(*&^%$#@!MNBVCXRASDFGHKLPOIUYT/EWQ0q87659321mnbvcxzasdfghjkl4oiuytrewp");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ObjectMapper mapper = new ObjectMapper();
		initSigMap();
		try {
			ArrayList<StringAlgo> decryptSigAlgoList = (ArrayList<StringAlgo>) mapper
					.readValue(
							new File("./json/youtube-dl.json"),
							mapper.getTypeFactory().constructCollectionType(
									ArrayList.class, StringAlgo.class));
			Iterator<Entry<String, String>> iterator = testSignature.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				String uncrypted = entry.getKey();
				String decrypted = entry.getValue();
				int sigLength = uncrypted.length();

				for (StringAlgo dsa : decryptSigAlgoList) {
					if (dsa.stringLengthCondition == sigLength) {

						if (!decrypted.equals(dsa.evaluate(uncrypted))) {
							System.out.println("Error with signature "
									+ sigLength);
							System.out.println("  -uncrypted- " + uncrypted);
							System.out.println("  -decrypted- " + decrypted);
						} else {
							System.out.println("signature " + sigLength
									+ " OK ");
						}

					}
				}
			}

			/*
			 * System.out.println("initial string : "+context);
			 */

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
