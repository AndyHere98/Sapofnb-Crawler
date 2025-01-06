package com.andy.sapofnbcrawler.request;


import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonRootName("Order")
public class OrderRequest {

	@Schema(
		description = "Mã đơn hàng"
	)
    @JsonProperty("name")
	private String orderSku;

	@Schema(
		description = "Ghi chú đơn hàng"
	)
    private String note;

	@Schema(
		description = "Địa chỉ giao hàng"
	)
    private String fullAddress;

	@Schema(
		description = "Thông tin vận chuyển"
	)
    private Shipment shipment = new Shipment();

	@Schema(
		description = "Trạng thái đơn hàng"
	)
    private String status;

	@Schema(
		description = "Thời gian đặt hàng"
	)
    private long createdOn;

	@Schema(
		description = "Thời gian đơn hàng cập nhật trạng thái lần cuối"
	)
    private long modifiedOn;

	@Schema(
		description = "Tổng giá trị đơn hàng"
	)
    private BigDecimal totalPrice;

	@Schema(
		description = "Tổng hợp các món ăn"
	)
    @JsonProperty("items")
    private List<DishRequest> dishes;

	@Schema(
		description = "Thông tin khách hàng"
	)
    private CustomerInfo customerInfo;

	@Schema(
		description = "Hình thức thanh toán dạng code"
	)
    private String paymentMethodType;
	
	@Schema(
		description = "Hình thức thanh toán dạng chữ"
	)
    private String paymentMethodName;


	@Schema(
		name = "Tổng hợp các món ăn",
		description = "Tổng hợp các món ăn"
	)
    @Data
    public static class DishRequest {

    	@Schema(
			description = "Tên món ăn"
		)
        @JsonProperty("itemName")
    	private String dishName;

    	@Schema(
			description = "Số lượng đã đặt"
		)
    	private int quantity;

    	@Schema(
			description = "Giá món"
		)
    	private BigDecimal money;
    }
    
	@Schema(
		name = "Thông tin khách hàng",
		description = "Thông tin khách hàng"
	)
    @Data
    public static class CustomerInfo {

		@Schema(
			description = "Tên khách hàng"
		)
        @JsonProperty("name")
    	private String customerName;

		@Schema(
			description = "Số điện thoại khách hàng"
		)
        private String phone;
    }

	@Schema(
		name = "Thông tin vận chuyển",
		description = "Thông tin vận chuyển đơn hàng"
	)
    @Data
    public static class Shipment {


		@Schema(
			description = "Thông tin địa chỉ"
		)
        @JsonProperty("shipmentAddress")
    	private ShipmentAddress shipmentAddress = new ShipmentAddress();

		@Schema(
			name = "Thông tin địa chỉ",
			description = "Thông tin địa chỉ khách hàng"
		)
        @Data
        public static class ShipmentAddress {

    		@Schema(
    			description = "Địa chỉ đặt hàng"
    		)
        	private String fullAddress;
        }
    }
}

