package com.ey.advisory.app.data.entities.gstr6;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Shashikant Shukla
 *
 */

@Data
@Entity
@Table(name = "TBL_GSTR6_DIGI_COMPUTE_DISTRIBUTION")
public class Gstr6DigiComputeDistributionEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	protected Long id;

	@Column(name = "ID_ASP", nullable = false)
	protected Long aspId;

	@Column(name = "IGST_AS_IGST_APS")
	private BigDecimal igstAsIgstAps;

	@Column(name = "IGST_AS_SGST_APS")
	private BigDecimal igstAsSgstAps;

	@Column(name = "IGST_AS_CGST_APS")
	private BigDecimal igstAsCgstAps;

	@Column(name = "SGST_AS_SGST_APS")
	private BigDecimal sgstAsSgstAps;

	@Column(name = "SGST_AS_IGST_APS")
	private BigDecimal sgstAsIgstAps;

	@Column(name = "CGST_AS_CGST_APS")
	private BigDecimal cgstAsCgstAps;

	@Column(name = "CGST_AS_IGST_APS")
	private BigDecimal cgstAsIgstAps;

	@Column(name = "CESS_APS")
	private BigDecimal cessAps;
	
	@Expose
	@Column(name = "ELIG_INDICATOR")
	private String eligibiltyIndicator;

	@Expose
	@Column(name = "DOC_TYPE_ASP")
	private String docTypeAsp;

	@Expose
	@Column(name = "ISD_GSTIN_ASP")
	private String isdGstinAsp;

	@Expose
	@Column(name = "TAX_PERIOD_ASP")
	private String taxPeriodAsp;

	@Expose
	@Column(name = "ID_GSTN")
	private Long idGstn;

	@Column(name = "IGST_AS_IGST_GSTN")
	private BigDecimal igstAsIgstGstn;

	@Column(name = "IGST_AS_SGST_GSTN")
	private BigDecimal igstAsSgstGstn;

	@Column(name = "IGST_AS_CGST_GSTN")
	private BigDecimal igstAsCgstGstn;

	@Column(name = "SGST_AS_SGST_GSTN")
	private BigDecimal sgstAsSgstGstn;

	@Column(name = "SGST_AS_IGST_GSTN")
	private BigDecimal sgstAsIgstGstn;

	@Column(name = "CGST_AS_CGST_GSTN")
	private BigDecimal cgstAsCgstGstn;

	@Column(name = "CGST_AS_IGST_GSTN")
	private BigDecimal cgstAsIgstGstn;

	@Column(name = "CESS_GSTN")
	private BigDecimal cessGSTN;

	@Expose
	@Column(name = "IS_DELETE")
	protected boolean isDelete;

	@Expose
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Expose
	@Column(name = "CREATED_BY")
	private String createdBy;

}
