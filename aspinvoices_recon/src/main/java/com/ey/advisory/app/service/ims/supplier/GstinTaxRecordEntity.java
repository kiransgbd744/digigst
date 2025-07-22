package com.ey.advisory.app.service.ims.supplier;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "TBL_SUPPLIER_IMS_GSTIN_TAXPERIOD")
@Data
public class GstinTaxRecordEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriods;

	@Column(name = "REPORT_ID")
	private Long reportId;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;
   
    @Column(name = "CREATED_BY")
    private String createdBy;

}
