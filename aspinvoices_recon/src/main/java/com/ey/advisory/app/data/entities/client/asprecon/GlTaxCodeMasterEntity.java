/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.data.entities.client.asprecon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "TBL_GL_TAX_CODE_MASTER")
@Data
@Slf4j
public class GlTaxCodeMasterEntity {

    @Expose
    @Id
    @SequenceGenerator(name = "glTaxCodeSeq", sequenceName = "TBL_GL_TAX_CODE_MASTER_SEQ", allocationSize = 1)
    @GeneratedValue(generator = "glTaxCodeSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Long id;

    @Expose
    @Column(name = "TRANSACTION_TYPE_GL")
    private String transactionTypeGl;

    @Expose
    @Column(name = "TAX_CODE_DESCRIPTION_MS")
    private String taxCodeDescriptionMs;

    @Expose
    @Column(name = "TAX_TYPE_MS")
    private String taxTypeMs;

    @Expose
    @Column(name = "ELIGIBILITY_MS")
    private String eligibilityMs;

    @Expose
    @Column(name = "TAX_RATE_MS", precision = 18, scale = 2)
    private BigDecimal taxRateMs;

    @Expose
    @Column(name = "ENITY_ID")
    private Long entityId;

    @Expose
    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Expose
    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;

    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;
    
    @Column(name = "FILE_TYPE")
    private String fileType; 
    
    @Column(name = "ERROR_CODE")
    private String errorCode;
    
    @Column(name = "ERROR_DESC")
    private String errorDesc;
    
    @Column(name = "FILE_ID")
    private String fileId;
	
}

