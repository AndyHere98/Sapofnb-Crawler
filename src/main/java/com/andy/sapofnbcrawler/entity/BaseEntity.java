package com.andy.sapofnbcrawler.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @Column(updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;
    
    @Column(insertable = false)
    @LastModifiedDate
    private LocalDateTime updateDate;
    
    @Column(length = 50, updatable = false, nullable = false)
    @CreatedBy
    private String createdBy;
    
    @Column(length = 50)
    @LastModifiedBy
    private String updatedBy;
}
