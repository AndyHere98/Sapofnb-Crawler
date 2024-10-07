package com.andy.sapofnbcrawler.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonRootName("menu")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {

	private String orderSku;
    private String note;
    private String fullAddress;
    private String status;
    private long createdOn;
    private long modifiedOn;
    private String orderDate;
    private BigDecimal totalPrice;
    @JsonProperty("dishes")
    private List<DishResponse> dishes;
    private CustomerInfo customerInfo;
    private String paymentMethodType;
    private String paymentMethodName;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class DishResponse {
    	private String dishName;
    	private int quantity;
    	private BigDecimal money;
        private String member;
    }
    
    @Data
    public class CustomerInfo {
    	private String customerName;
        private String phone;
    }
}

