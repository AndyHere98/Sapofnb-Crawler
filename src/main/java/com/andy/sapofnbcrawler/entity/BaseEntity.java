package com.andy.sapofnbcrawler.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public class BaseEntity {

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdDate;
    @Column(insertable = false)
    private LocalDateTime updateDate;
    @Column(length = 50, updatable = false, nullable = false)
    private String createdBy;
    @Column(length = 50, insertable = false)
    private String updatedBy;
}
