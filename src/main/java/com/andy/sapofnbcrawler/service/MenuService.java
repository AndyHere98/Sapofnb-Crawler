package com.andy.sapofnbcrawler.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.andy.sapofnbcrawler.common.SapoConstants;
import com.andy.sapofnbcrawler.common.SapoUtils;
import com.andy.sapofnbcrawler.dto.MenuDto;
import com.andy.sapofnbcrawler.dto.OrderDetailDto;
import com.andy.sapofnbcrawler.dto.SapoMenuDto;
import com.andy.sapofnbcrawler.exception.ResourceNotFoundException;

@Service
public class MenuService {

	private final String URI = SapoConstants.URI;
	private final String COOKIE = SapoConstants.COOKIE;

	public MenuDto getMenu() {

		RestTemplate restTemplate = new RestTemplate();
		StringBuilder sUrl = new StringBuilder();
		sUrl.append(URI);
		sUrl.append("/menu");
		HttpHeaders httpHeaders = restTemplate.headForHeaders(sUrl.toString());
		httpHeaders.add("Cookie", COOKIE);

		HttpEntity<String> httpEntity = new HttpEntity<>("Andy", httpHeaders);
		ResponseEntity<String> response = restTemplate.exchange(sUrl.toString(), HttpMethod.GET, httpEntity,
				String.class);

		// System.out.println(response.getBody());
		String json = SapoUtils.getJsonData(response.getBody());

		DateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
		String weekDay = "";

		switch (dateFormat.format(new Date())) {
			case "MONDAY":
				weekDay = "Thứ hai";
				break;
			case "TUESDAY":
				weekDay = "Thứ ba";
				break;
			case "WEDNESDAY":
				weekDay = "Thứ tư";
				break;
			case "THURSDAY":
				weekDay = "Thứ năm";
				break;
			case "FRIDAY":
				weekDay = "Thứ sáu";
				break;
			case "SATURDAY":
				weekDay = "Thứ bảy";
				break;
			case "SUNDAY":
				weekDay = "Chủ nhật";
				break;
			default:
				break;
		}

		MenuDto menuResponse = new MenuDto();
		if (json.isEmpty()) {
			menuResponse.setName(weekDay);
			return menuResponse;
		}
		// throw new ResourceNotFoundException("Dữ liệu phản hồi", "khi lấy thông tin
		// menu", null);

		SapoMenuDto sapoMenu = (SapoMenuDto) SapoUtils.convertJsonToObject(json, SapoMenuDto.class);

		if (sapoMenu.getName().toLowerCase().contains(weekDay.toLowerCase())) {
			menuResponse = mappingMenuDto(sapoMenu);
		} else {
			menuResponse.setName(weekDay);
			return menuResponse;
		}
		return menuResponse;
	}

	private MenuDto mappingMenuDto(SapoMenuDto sapoMenu) {
		MenuDto menuResponse = new MenuDto();
		OrderDetailDto dishResponse;
		List<OrderDetailDto> dishResponseList = new ArrayList<>();

		for (int i = 0; i < sapoMenu.getDishes().size(); i++) {
			dishResponse = new OrderDetailDto();
			dishResponse.setId(i);
			dishResponse.setName(sapoMenu.getDishes().get(i).getName());
			dishResponse.setPrice(sapoMenu.getDishes().get(i).getDishDetail().get(0).getPrice());
			dishResponseList.add(dishResponse);
		}

		menuResponse.setName(sapoMenu.getName());
		menuResponse.setDishes(dishResponseList);
		return menuResponse;
	}
}
