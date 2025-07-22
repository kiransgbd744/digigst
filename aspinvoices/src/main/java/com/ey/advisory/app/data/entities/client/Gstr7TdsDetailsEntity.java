package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
@Entity
@Setter
@Getter
@ToString
@Table(name = "GETGSTR7_TDS_DETAILS")
public class Gstr7TdsDetailsEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR7_TDS_DETAILS_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "REQ_TIME")
	private String reqTime;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derReturnPeriod;

	@Column(name = "TDS_DEDUCTOR_GSTIN")
	private String tdsDeductorGstin;

	@Column(name = "TDS_DEDUCTEE_GSTIN")
	private String tdsDeducteeGstin;

	@Column(name = "ORG_TDS_DEDUCTEE_GSTIN")
	private String orgTdsGstin;

	@Column(name = "SECTION_NAME")
	private String secName;

	@Column(name = "CHKSUM")
	private String chksum;

	@Column(name = "DEDUCTEE_NAME")
	private String deducteeName;

	@Column(name = "ORG_DEDUCTEE_NAME")
	private String orgDeducteeName;

	@Column(name = "AMT_DEDUCTED")
	private BigDecimal amtDeducted;

	@Column(name = "ORG_AMT_DEDUCTED")
	private BigDecimal orgAmtDeducted;

	@Column(name = "SOURCE")
	private String source;

	@Column(name = "ACTION_TAKEN")
	private String actionTaken;

	@Column(name = "ORG_MONTH")
	private String orgMonth;

	@Column(name = "IGST")
	private BigDecimal igst;

	@Column(name = "CGST")
	private BigDecimal cgst;

	@Column(name = "SGST")
	private BigDecimal sgst;

	@Column(name = "DOC_KEY")
	private String docKey;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

}
