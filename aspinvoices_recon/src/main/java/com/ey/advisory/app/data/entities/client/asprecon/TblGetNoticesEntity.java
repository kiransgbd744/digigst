/**
 * 
 */
package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author sakshi.jain
 *
 */

@Entity
@Data
@Table(name = "TBL_NOTICE_DETAILS")
public class TblGetNoticesEntity {

    @Id
	@SerializedName("id")
    @SequenceGenerator(name = "sequence", sequenceName = "TBL_NOTICE_DETAILS_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
    private Long id;

    @Column(name = "GSTIN")
    private String gstin;

    @Column(name = "BATCH_ID")
    private Long batchId;

    @Column(name = "REFERENCE_ID", length = 15)
    private String referenceId;

    @Column(name = "ARN")
    private String arn;

    @Column(name = "MODULE_CODE", length = 6)
    private String moduleCode;

    @Column(name = "ALERT_CODE", length = 25)
    private String alertCode;

    @Column(name = "DATE_OF_ISSUE", length = 10)
    private LocalDate dateOfIssue;

    @Column(name = "DATE_OF_RESPONSE")
    private LocalDate dateOfResponse;

    @Column(name = "NOTICE_TYPE", length = 50)
    private String noticeType;

    @Column(name = "DERIVED_FROM_TAX_PERIOD")
    private Integer derivedFromTaxPeriod;

    @Column(name = "DERIVED_TO_TAX_PERIOD")
    private Integer derivedToTaxPeriod;

    @Column(name = "DUE_DATE_OF_REPLY")
    private LocalDate dueDateOfReply;

    @Column(name = "IS_RESPONDED")
    private Boolean isResponded;
    
    @Column(name = "IS_DELETE")
    private Boolean isDelete;
    
    @Column(name = "CREATED_BY")
    private String createdBy;
    
    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;
    
    @Column(name = "MODIFIED_ON")
    private LocalDateTime modifiedOn;
    
}