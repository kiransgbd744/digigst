/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.data.entities.client.asprecon;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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
@Entity
@Table(name = "TBL_GL_CODE_MASTER")
@Data
@Slf4j
public class GlCodeMasterEntity {


    @Expose
    @Id
    @SequenceGenerator(name = "glGlCodeMasterSeq", sequenceName = "TBL_GL_CODE_MASTER_SEQ", allocationSize = 1)
    @GeneratedValue(generator = "glGlCodeMasterSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Long id;


    @Expose
    @Column(name = "CGST_TAX_GL_CODE")
    private BigDecimal cgstTaxGlCode;

    @Expose
    @Column(name = "SGST_TAX_GL_CODE")
    private BigDecimal sgstTaxGlCode;

    @Expose
    @Column(name = "IGST_TAX_GL_CODE")
    private BigDecimal igstTaxGlCode;

    @Expose
    @Column(name = "UGST_TAX_GL_CODE")
    private BigDecimal ugstTaxGlCode;

    @Expose
    @Column(name = "COMPENSATION_CESS_GL_CODE")
    private BigDecimal compensationCessGlCode;

    @Expose
    @Column(name = "KERALA_CESS_GL_CODE")
    private BigDecimal keralaCessGlCode;

    @Expose
    @Column(name = "REVENUE_GLS")
    private BigDecimal revenueGls;

    @Expose
    @Column(name = "EXPENCE_GLS")
    private BigDecimal expenceGls;

    @Expose
    @Column(name = "EXCHANGE_RATE")
    private BigDecimal exchangeRate;

    @Expose
    @Column(name = "DIFF_GL")
    private BigDecimal diffGl;

    @Expose
    @Column(name = "EXPORT_GL")
    private BigDecimal exportGl;

    @Expose
    @Column(name = "FOREX_GLS_POR")
    private BigDecimal forexGlsPor;

    @Expose
    @Column(name = "TAXABLE_ADVANCE_LIABILITY_GLS")
    private BigDecimal taxableAdvanceLiabilityGls;

    @Expose
    @Column(name = "NON_TAXABLE_ADVANCE_LIABILITY_GLS")
    private BigDecimal nonTaxableAdvanceLiabilityGls;

    @Expose
    @Column(name = "CC_AND_ST_GLS")
    private BigDecimal ccAndStGls;

    @Expose
    @Column(name = "UNBILLED_REVENUE_GLS")
    private BigDecimal unbilledRevenueGls;

    @Expose
    @Column(name = "BANK_ACC_GLS")
    private BigDecimal bankAccGls;

    @Expose
    @Column(name = "INPUT_TAX_GLS")
    private BigDecimal inputTaxGls;

    @Expose
    @Column(name = "FIXED_ASSET_GLS")
    private BigDecimal fixedAssetGls;

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
    
    @Expose
    @Column(name = "FILE_TYPE")
    private String fileType;
    
    @Column(name = "ERROR_CODE")
    private String errorCode;
    
    @Column(name = "ERROR_DESC")
    private String errorDesc;
    
    @Column(name = "FILE_ID")
    private String fileId;

}
