package com.ey.advisory.app.gstr2b;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.Cascade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ravindra V S
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GETGSTR2B_ECOM_HEADER")
public class Gstr2GetGstr2BEcomHeaderEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR2B_ECOM_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CHECKSUM")
	private String checksum;

	@Column(name = "RGSTIN")
	private String rGstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "VERSION")
	private String version;

	@Column(name = "GENDT")
	private LocalDate genDate;

	@Column(name = "SGSTIN")
	private String sGstin;

	@Column(name = "SUPPLIER_TRADE_NAME")
	private String supTradeName;

	@Column(name = "SUPPLIER_FILING_DT")
	private LocalDate supFilingDate;

	@Column(name = "SUPPLIER_FILING_PRD")
	private String supFilingPeriod;

	@Column(name = "INV_NUM")
	private String invoiceNumber;

	@Column(name = "INV_DATE")
	private LocalDate invoiceDate;

	@Column(name = "INV_TYPE")
	private String invoiceType;

	@Column(name = "SUPPLIER_INV_VAL")
	private BigDecimal supInvoiceValue;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "POS")
	private String pos;

	@Column(name = "REV")
	private String rev;

	@Column(name = "ITCAVAL")
	private String itcAvailable;

	@Column(name = "RSN")
	private String rsn;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "IS_DELETE")
	private Boolean isDelete;

	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "INVOCATION_ID")
	private Long invocationId;
	
	@Column(name = "IRN_NUMBER")
	private String irnNo;
	
	@Column(name = "IRN_GENERATION_DATE")
	private LocalDate irnGenDate;
	
	@Column(name = "IRN_SOURCE_TYPE")
	private String irnSrcType;
	
	@Column(name = "DOC_KEY")
	private String docKey;
	
	@Column(name = "LINKING_DOC_KEY")
	private String lnkingDocKey;
	
	@OneToMany(mappedBy = "header")
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<Gstr2GetGstr2BEcomItemEntity> lineItems = new ArrayList<>();
}
