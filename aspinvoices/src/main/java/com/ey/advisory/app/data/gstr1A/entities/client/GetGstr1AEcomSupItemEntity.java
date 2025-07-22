package com.ey.advisory.app.data.gstr1A.entities.client;

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

/**
 * 
 * @author Siva.Reddy
 *
 */
@Data
@Entity
@Table(name = "GETGSTR1A_ECOMSUP_ITEM")
public class GetGstr1AEcomSupItemEntity {

	@Id
//	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR1_B2B_ITEM_SEQ", allocationSize = 100)
//	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Column(name = "TAXABLE_VALUE")
	protected BigDecimal taxValue;

	@Column(name = "TAX_RATE")
	protected BigDecimal taxRate;

	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmt;

	@Column(name = "CESS_AMT")
	protected BigDecimal cessAmt;

	@Column(name = "INV_VALUE")
	protected BigDecimal invValue;

	@Column(name = "SERIAL_NUM")
	protected Integer serialNum;

	@ManyToOne
	@JoinColumn(name = "HEADER_ID", referencedColumnName = "ID", nullable = false)
	private GetGstr1AEcomSupHeaderEntity document;



}
