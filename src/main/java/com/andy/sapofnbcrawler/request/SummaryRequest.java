package com.andy.sapofnbcrawler.request;

import lombok.Data;

@Data
public class SummaryRequest {

    private String customerName;
    private String unit;
    private int quantity;
}
