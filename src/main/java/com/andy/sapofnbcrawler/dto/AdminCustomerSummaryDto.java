package com.andy.sapofnbcrawler.dto;

import java.util.List;

import com.andy.sapofnbcrawler.object.CustomerRank;

import lombok.Data;


@Data
public class AdminCustomerSummaryDto {

	private List<CustomerRank> topCustomers;
	private List<CustomerInfoDto> customerInfos;
	
}
