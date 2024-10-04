package com.andy.sapofnbcrawler.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@JsonRootName("menu")
public class MemberOrderResponse {

	private String orderSku;
    private String status;
    private Date   createdOn;
    private Date   modifiedOn;
    private BigDecimal totalPrice;
    @JsonProperty("dishes")
    private List<DishResponse> dishes;
    private CustomerInfo customerInfo;
    private String paymentMethodType;
    private String paymentMethodName;

    @Data
    public static class DishResponse {
    	private String dishName;
    	private int quantity;
    	private BigDecimal price;
    }
    
    @Data
    public static class CustomerInfo {
    	private String customerName;
        private String customerPhone;
        private String customerEmail;
    }
}

