package com.andy.sapofnbcrawler.request;


import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MenuRequest {

    private String name;
    @JsonProperty("objects")
    private List<DishRequest> dishes;
    
    @Data
    public static class DishRequest {
        private String name;
        @JsonProperty("variants")
        private List<DishDetailRequest> dishDetail;
        
        @Data
        public static class DishDetailRequest {
            private BigDecimal price;
        }
    }
    
}

