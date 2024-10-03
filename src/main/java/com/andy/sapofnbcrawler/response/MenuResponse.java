package com.andy.sapofnbcrawler.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.util.List;

@Data
@JsonRootName("menu")
public class MenuResponse {

    private String name;
    @JsonProperty("dishes")
    private List<DishResponse> dishes;
    
    @Data
    public static class DishResponse {
        private String name;
        private int price;
    }
    
}

