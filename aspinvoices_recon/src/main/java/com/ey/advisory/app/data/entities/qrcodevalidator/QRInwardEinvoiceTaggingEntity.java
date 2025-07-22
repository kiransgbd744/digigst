package com.ey.advisory.app.data.entities.qrcodevalidator;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "TBL_QR_VALIDATOR_INWARD_EINVOICE_TAGGING_LOG")
@Data
public class QRInwardEinvoiceTaggingEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;
	
    @Column(name = "QR_CODE_VALIDATED")
    private String qrCodeValidated;

    @Column(name = "QR_CODE_VALIDATION_RESULT")
    private String qrCodeValidationResult;

    @Column(name = "QR_CODE_MATCH_COUNT")
    private Integer qrCodeMatchCount;

    @Column(name = "QR_CODE_MISMATCH_COUNT")
    private Integer qrCodeMismatchCount;

    @Column(name = "QR_CODE_MISMATCH_ATTRIBUTES")
    private String qrCodeMismatchAttributes;

    @Column(name = "IS_TAGGED")
    private Boolean isTagged;
    
    @Column(name = "IRN")
    private String irn;

    @Column(name = "IS_DELETE")
    private Boolean isDelete;

    @Column(name = "MODE")
    private String mode;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "MODIFIED_ON")
    private LocalDateTime modifiedOn;

    // getters and setters
}