package com.andy.sapofnbcrawler.request;


import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MemberOrderRequest {

	private String customerName;
    private String customerPhone;
    private String customerEmail;
    private BigDecimal totalPrice;
    @JsonProperty("orderItems")
    private List<DishRequest> dishes;
    @JsonProperty("paymentMethod")
    private String paymentMethodType;

    @Data
    public static class DishRequest {
        @JsonProperty("name")
    	private String dishName;
    	private int quantity;
    	private BigDecimal price;
    }
}

