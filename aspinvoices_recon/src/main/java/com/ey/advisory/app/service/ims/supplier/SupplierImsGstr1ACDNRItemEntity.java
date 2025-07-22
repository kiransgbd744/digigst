package com.ey.advisory.app.service.ims.supplier;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Entity
@Table(name = "TBL_GETIMS_GSTR1A_CDNR_ITEM")
@Data
public class SupplierImsGstr1ACDNRItemEntity {
	
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_GETIMS_GSTR1A_CDNR_ITEM_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
    private Long id;

	@Column(name = "HEADER_ID")
	private Long headerId;
	
	@Column(name = "DERIVED_RET_PERIOD")
    private Long derivedRetPeriod;
	
	@Column(name = "ITEM_NUMBER")
	private Long itemNo;
	
	@Column(name = "TAX_RATE")
	private BigDecimal taxRate;
	
	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxableValue;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;
	
	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;
	
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "HEADER_ID", insertable = false, updatable = false)
	protected SupplierImsGstr1ACDNRHeaderEntity header;

}
