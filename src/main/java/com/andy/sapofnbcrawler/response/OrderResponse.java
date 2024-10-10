package com.andy.sapofnbcrawler.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@JsonRootName("menu")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customerInfo", "orderSku", "status", "fullAddress", "note",
    "orderDate", "orderTime", "updatedOrderTime", "totalPrice", "paymentMethodType",
    "paymentMethodName", "dishes"
})
public class OrderResponse {
    
    private String             orderSku;
    private String             note;
    private String             fullAddress;
    private String             status;
    @JsonProperty("orderTime")
    private String          createdOn;
    @JsonProperty("updatedOrderTime")
    private String          modifiedOn;
    private String             orderDate;
    private BigDecimal         totalPrice;
    @JsonProperty("dishes")
    private List<DishResponse> dishes;
    private CustomerInfo       customerInfo;
    private String             paymentMethodType;
    private String             paymentMethodName;
    
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DishResponse {
        private String     dishName;
        private int        quantity;
        private BigDecimal price;
        private String     member;
    }
    
    @Data
    public static class CustomerInfo {
        private String customerName;
        private String phone;
        private String customerEmail;
    }
}

