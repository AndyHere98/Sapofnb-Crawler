package com.andy.sapofnbcrawler.service;

import com.andy.sapofnbcrawler.common.SapoConstants;
import com.andy.sapofnbcrawler.common.SapoUtils;
import com.andy.sapofnbcrawler.request.MenuRequest;
import com.andy.sapofnbcrawler.response.MenuResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

	private final String URI = SapoConstants.URI;
	private final String COOKIE = SapoConstants.COOKIE;

	@Transactional(value = Transactional.TxType.REQUIRED)
	public MenuResponse getMenu() {

		RestTemplate restTemplate = new RestTemplate();
		StringBuilder sUrl = new StringBuilder();
		sUrl.append(URI);
		sUrl.append("/menu");
		HttpHeaders httpHeaders = restTemplate.headForHeaders(sUrl.toString());
		httpHeaders.add("Cookie", COOKIE);

		HttpEntity<String> httpEntity = new HttpEntity<>("Andy", httpHeaders);
		ResponseEntity<String> response = restTemplate.exchange(sUrl.toString(), HttpMethod.GET, httpEntity,
				String.class);

		System.out.println(response.getBody());
		String json = SapoUtils.getJsonData(response.getBody());

		if (json.isEmpty())
			return null;
		
		MenuRequest menuRequest = new MenuRequest();
		MenuResponse menuResponse = new MenuResponse();

		menuRequest = (MenuRequest) SapoUtils.convertJsonToObject(json, MenuRequest.class);
		menuResponse = mappingMenuResponse(menuRequest);

		return menuResponse;
	}

	private MenuResponse mappingMenuResponse(MenuRequest menuRequest) {
		MenuResponse menuResponse = new MenuResponse();
		MenuResponse.DishResponse dishResponse;
		List<MenuResponse.DishResponse> dishResponseList = new ArrayList<>();

		for (int i = 0; i < menuRequest.getDishes().size(); i++) {
			dishResponse = new MenuResponse.DishResponse();
			dishResponse.setName(menuRequest.getDishes().get(i).getName());
			dishResponse.setPrice(menuRequest.getDishes().get(i).getDishDetail().get(0).getPrice());
			dishResponseList.add(dishResponse);
		}

		menuResponse.setName(menuRequest.getName());
		menuResponse.setDishes(dishResponseList);
		return menuResponse;
	}
}
