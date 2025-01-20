package com.andy.sapofnbcrawler.common;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import com.andy.sapofnbcrawler.exception.OrderPlaceTimeUpException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@PropertySource("classpath:application.properties")
public class SapoUtils {
	
	private SapoUtils() {}
	
	@Value("${sapo.order-time}")
	private static String timeUp;

	public static String getJsonData(String resData) {
		JSONArray array = new JSONArray(resData);
		String json = !array.isEmpty() ? array.getJSONObject(0).toString() : "";
		return json;
	}

	public static Object convertJsonToObject(String json, Class<?> clazz) {
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
	
	public static boolean checkingTimeUp () {
		Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date timeup = sdf.parse(sdf.format(currentDate).substring(0,10) + " " + timeUp);
            if (currentDate.after(timeup)) 
            	throw new OrderPlaceTimeUpException("Không thể thao tác đơn hàng sau " + timeUp);
        } catch (ParseException e) {
            throw new RuntimeException(sdf.format(currentDate).substring(0,10) + " " + timeUp);
        }
        return true;
	}
	
	public static Date parseDate (String format, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		
		try {
			
			date = sdf.parse(sdf.format(date));
		} catch (ParseException exception) {
			throw new RuntimeException("Date " + date.toString() + " parse to format " + format + " failed");
		}
		
		return date;
	}
	
	public List<String> extractDishNameFromMenuList () {
		return null;
	}
}
