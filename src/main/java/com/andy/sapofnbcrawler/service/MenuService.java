package com.andy.sapofnbcrawler.service;

import java.util.ArrayList;
import java.util.List;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
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

//		System.out.println(response.getBody());
		String json = SapoUtils.getJsonData(response.getBody());

		if (json.isEmpty())
			throw new ResourceNotFoundException("Dữ liệu phản hồi", "khi lấy thông tin menu", null);
		
		MenuDto menuResponse = new MenuDto();

		SapoMenuDto sapoMenu = (SapoMenuDto) SapoUtils.convertJsonToObject(json, SapoMenuDto.class);
		menuResponse = mappingMenuDto(sapoMenu);

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
