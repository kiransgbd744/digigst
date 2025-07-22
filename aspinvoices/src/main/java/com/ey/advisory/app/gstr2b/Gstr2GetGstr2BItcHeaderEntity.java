package com.ey.advisory.app.gstr2b;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Hema G M
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GETGSTR2B_ITC_HDR")
public class Gstr2GetGstr2BItcHeaderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "RGSTIN")
	private String rGstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "VERSION")
	private String version;
	
	@Column(name = "GENDT")
	private LocalDateTime genDate;

	@Column(name = "ITC_SMRY")
	private String itcSummary;
	
	@Column(name = "ITC_DTL")
	private String itcDetails;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;
	
	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;
	
	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;
	
	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
	@Column(name = "IS_DELETE")
	private Boolean isDelete;
	
	//@OneToMany(mappedBy = "header", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	//protected List<Gstr2GetGstr2BItcItemEntity> lineItems = new ArrayList<>();
}
