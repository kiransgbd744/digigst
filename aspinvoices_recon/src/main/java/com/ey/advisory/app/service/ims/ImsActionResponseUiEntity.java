package com.ey.advisory.app.service.ims;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Entity
@Table(name = "TBL_IMS_ACTION_RESPONSE_UI")
@Setter
@Getter
@ToString

public class ImsActionResponseUiEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BATCH_ID")
    private Long batchId;

    @Column(name = "FILE_ID")
    private Long fileId;

    @Column(name = "IMS_RESPONSE")
    private String imsResponse;

    @Column(name = "RESPONSE_REMARKS")
    private String responseRemarks;

    @Column(name = "IMS_UNIQUE_ID")
    private String imsUniqueId;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY")
    private String createdBy;
		
    @Column(name = "ITC_RED_REQ")
    private String itcRedRequired;
    
    @Column(name = "DECLARED_IGST")
	private BigDecimal declIgst = BigDecimal.ZERO;
    
    @Column(name = "DECLARED_SGST")
	private BigDecimal declSgst = BigDecimal.ZERO;
    
    @Column(name = "DECLARED_CGST")
	private BigDecimal declCgst = BigDecimal.ZERO;
    
    @Column(name = "DECLARED_CESS")
	private BigDecimal declCess = BigDecimal.ZERO;
}
