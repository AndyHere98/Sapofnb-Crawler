package com.andy.sapofnbcrawler.request;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(name = "Thông tin đơn hàng", description = "Thông tin chi tiết về đơn hàng")
@Data
public class OrderRequest {

	@Schema(description = "Mã đơn hàng")
	@NotEmpty(message = "Tên món ăn không được để trống")
	@Size(max = 36)
	@JsonProperty("name")
	private String orderSku;

	@Schema(description = "Ghi chú đơn hàng")
	private String note;

	@Schema(description = "Địa chỉ giao hàng")
	private String fullAddress;

	@Schema(description = "Thông tin vận chuyển")
	private Shipment shipment = new Shipment();

	@Schema(description = "Trạng thái đơn hàng")
	private String status;

	@Schema(description = "Thời gian đặt hàng")
	@NotEmpty(message = "Thời gian đặt hàng không được để trống")
	@Pattern(regexp = "^([1-9]{1})+([0-9]{9})?$")
	private long createdOn;

	@Schema(description = "Thời gian đơn hàng cập nhật trạng thái lần cuối")
	@NotEmpty(message = "Thời gian đặt hàng không được để trống")
	@Pattern(regexp = "^([1-9]{1})+([0-9]{9})?$")
	private long modifiedOn;

	@Schema(description = "Tổng giá trị đơn hàng")
	@NotEmpty(message = "Giá trị đơn hàng không được để trống")
	@Pattern(regexp = "^([1-9]{1})+([0-9]{1,2})+([0]{3})+(\\.[0]{2})?$", message = "Định dạng tổng giá món: 10000.00 đến 999000.00 hoặc 10000 đến 999000")
	private BigDecimal totalPrice;

	@Schema(description = "Tổng hợp các món ăn")
	@NotEmpty(message = "Thông tin đặt món ăn không được trống")
	@JsonProperty("items")
	private List<DishRequest> dishes;

	@Schema(description = "Thông tin khách hàng")
	@NotEmpty(message = "Thông tin khách hàng không được để trống")
	private CustomerInfo customerInfo;

	@Schema(description = "Hình thức thanh toán")
	@NotEmpty(message = "Vui lòng chọn hình thức thanh toán")
	@Size(max = 10)
	private String paymentMethod;

	@Schema(description = "Kiểu thanh toán (trả trước / trả sau)")
	@NotEmpty(message = "Vui lòng chọn loại hình thanh toán")
	private String paymentType;

	@Schema(name = "Tổng hợp các món ăn", description = "Tổng hợp các món ăn")
	@Data
	public static class DishRequest {

		@Schema(description = "Tên món ăn")
		@NotEmpty(message = "Tên món ăn không được để trống")
		@Size(max = 200)
		@JsonProperty("itemName")
		private String dishName;

		@Schema(description = "Số lượng đã đặt")
		@Pattern(regexp = "^([0-9]{1,2})+(\\.[0]{2})?$", message = "Số lượng nhập vào cần đúng kiểu dữ liệu. Ví dụ: 1 hoặc 1.00, tối đa 99.00")
		@Positive(message = "Số lượng phải lớn hơn 0")
		private int quantity;

		@Schema(description = "Giá món")
		@Pattern(regexp = "^([1-9]{2})+([0]{3})+(\\.[0]{2})?$", message = "Định dạng giá món: 11000.00 đến 99000.00 hoặc 11000 đến 99000")
		@Positive(message = "Giá món phải lớn hơn 0")
		private BigDecimal money;
	}

	@Schema(name = "Thông tin khách hàng", description = "Thông tin khách hàng")
	@Data
	public static class CustomerInfo {

		@Schema(description = "Tên khách hàng")
		@NotEmpty(message = "Tên khách hàng không được để trống")
		@Size(max = 200)
		@JsonProperty("name")
		private String customerName;

		@Schema(description = "Số điện thoại khách hàng")
		@NotEmpty(message = "Số điện thoại khách hàng không được để trống")
		@Pattern(regexp = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$")
		private String phone;
	}

	@Schema(name = "Thông tin vận chuyển", description = "Thông tin vận chuyển đơn hàng")
	@Data
	public static class Shipment {

		@Schema(description = "Thông tin địa chỉ")
		@JsonProperty("shipmentAddress")
		private ShipmentAddress shipmentAddress = new ShipmentAddress();

		@Schema(name = "Thông tin địa chỉ", description = "Thông tin địa chỉ khách hàng")
		@Data
		public static class ShipmentAddress {

			@Schema(description = "Địa chỉ đặt hàng")
			private String fullAddress;
		}
	}
}
