package com.andy.sapofnbcrawler.request;


import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Data;

@Data
@JsonRootName("Order")
public class OrderRequest {

    @JsonProperty("name")
	private String orderSku;
    private String note;
    private String fullAddress;
    private Shipment shipment = new Shipment();
    private String status;
    private long createdOn;
    private long modifiedOn;
    private BigDecimal totalPrice;
    @JsonProperty("items")
    private List<DishRequest> dishes;
    private CustomerInfo customerInfo;
    private String paymentMethodType;
    private String paymentMethodName;

    @Data
    public static class DishRequest {
        @JsonProperty("itemName")
    	private String dishName;
    	private int quantity;
    	private BigDecimal money;
    }
    
    @Data
    public static class CustomerInfo {
        @JsonProperty("name")
    	private String customerName;
        private String phone;
    }
    
    @Data
    public static class Shipment {
        @JsonProperty("shipmentAddress")
    	private ShipmentAddress shipmentAddress = new ShipmentAddress();

        @Data
        public static class ShipmentAddress {
        	private String fullAddress;
        }
    }
}

