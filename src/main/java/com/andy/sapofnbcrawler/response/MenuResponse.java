package com.andy.sapofnbcrawler.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

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

