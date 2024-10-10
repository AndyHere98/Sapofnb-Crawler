package com.andy.sapofnbcrawler.common;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SapoUtils {

	public static String getJsonData(String resData) {
		JSONArray array = new JSONArray(resData);
		String json = !array.isEmpty() ? array.getJSONObject(0).toString() : "";
		return json;
	}

	public static Object convertJsonToObject(String json, Class clazz) {
		ObjectMapper mapper = new ObjectMapper();
		Object obj = null;
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			obj = mapper.readValue(json, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return obj;
	}
	
	public static String getPayment(String type) {
		
		Map<String, String> paymentMap = new HashMap<>();
		paymentMap.put("momo", "Momo");
		paymentMap.put("bank", "Ngân Hàng");
		paymentMap.put("cash", "Tiền mặt");
	
		return paymentMap.get(type);
	}
}
