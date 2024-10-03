package com.andy.sapofnbcrawler.service;

import com.andy.sapofnbcrawler.request.MenuRequest;
import com.andy.sapofnbcrawler.response.MenuResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SapoService {
    public Object getMenu() {
        
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://menu.sapofnb.vn/api/menu";
        HttpHeaders httpHeaders = restTemplate.headForHeaders(url);
//        httpHeaders.add("Cookie", "store=bfe11e5ff59711eb80610a75247ce32e");
        
        HttpEntity<String>     httpEntity = new HttpEntity<>("andy", httpHeaders);
        ResponseEntity<String> response2  = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        
        
        System.out.println(response2.getBody());
        JSONArray array = new JSONArray(response2.getBody());
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MenuRequest menuRequest = new MenuRequest();
        String      json        = !array.isEmpty() ? array.getJSONObject(0).toString() : "";
        
        if (json.isEmpty()) return null;
        
        MenuResponse                    menuResponse     = new MenuResponse();
        MenuResponse.DishResponse       dishResponse;
        List<MenuResponse.DishResponse> dishResponseList = new ArrayList<>();
        
        try {
            menuRequest = mapper.readValue(json, MenuRequest.class);
            for (int i = 0; i < menuRequest.getDishes().size(); i++) {
                dishResponse = new MenuResponse.DishResponse();
                dishResponse.setName(menuRequest.getDishes().get(i).getName());
                dishResponse.setPrice(menuRequest.getDishes().get(i).getDishDetail().get(0).getPrice());
                dishResponseList.add(dishResponse);
            }
            menuResponse.setName(menuRequest.getName());
            menuResponse.setDishes(dishResponseList);
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        return menuResponse;
    }
}
