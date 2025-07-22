package com.ey.advisory.service.gstr1.sales.register;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author Shashikant.Shukla 
 *
 */

@Data
@Entity
@Table(name = "TBL_STG_SALES_REGISTER")
public class StagingSalesRegisterEntity {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "SEQ_STG_SALES_REGISTER", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "FILEID")
	private Long fileId;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "BUSINESS_PLACE")
	private String businessPlace;
	
	@Column(name = "CUST_GSTIN")
	private String custGstin;

	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "SUPPLY_TYPE")
	private String supplyType;

	@Column(name = "DOC_NUM")
	private String docNum;

	@Column(name = "DOC_DATE")
	private String docDate;

	@Column(name = "ITEM_SERIAL_NUMBER")
	private String itemSerialNumber;

	@Column(name = "HSNSAC")
	private String hsnSac;
	
	@Column(name = "TAXRATE")
	private String taxRate;

	@Column(name = "TAXABLE_VALUE")
	private String taxableValue;

	@Column(name = "IGST")
	private String igst;

	@Column(name = "CGST")
	private String cgst;

	@Column(name = "SGST")
	private String sgst;

	@Column(name = "CESS_AMT_ADVALOREM")
	private String cessAmountAdvalorem;
	
	@Column(name = "CESS_AMT_SPECIFIC")
	private String cessAmountSpecific;
	
	@Column(name = "INVOICE_VALUE")
	private String invoiceValue;

	@Column(name = "POS")
	private String pos;

	@Column(name = "TRANS_TYPE")
	private String transType;
	
	@Column(name = "IS_ERROR")
	private Boolean isError;
	
	@Column(name = "ERR_DESC")
	private String errDesc;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
}
