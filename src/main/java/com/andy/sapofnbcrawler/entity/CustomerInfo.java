package com.andy.sapofnbcrawler.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Nationalized;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Tag(name = "Thông tin khách hàng", description = "Nơi lưu trữ thông tin khách hàng")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, length = 100)
	@Nationalized
	private String customerName;
	@Column(nullable = false, length = 20)
	private String customerPhone;
	@Column(nullable = false, length = 100)
	private String customerEmail;
	@Column(nullable = false, unique = true, length = 30)
	private String ipAddress;
	@Column(name = "host_name")
	private String pcHostName;

	@Column(name = "is_admin", length = 1, nullable = false, columnDefinition = "NUMBER(1,0) DEFAULT 0")
	private int isAdmin;

	@Column(updatable = false, nullable = false)
	private LocalDateTime createdDate;

	@Column(insertable = false)
	private LocalDateTime updatedDate;

	@Column(length = 50, updatable = false, nullable = false)
	private String createdBy;

	@Column(length = 50)
	private String updatedBy;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "customerId", orphanRemoval = true)
	@JsonManagedReference
	private List<Order> orders = new ArrayList<>();
}
