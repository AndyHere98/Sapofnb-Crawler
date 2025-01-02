package com.andy.sapofnbcrawler.response;


import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Data;

@Data
@JsonRootName(value = "menu")
public class MenuResponse {

    private String name;
    @JsonProperty("dishes")
    private List<DishResponse> dishes;
    
    @Data
    public static class DishResponse {
        private String     name;
        private BigDecimal price;
    }
    
}

