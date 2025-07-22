/**
 * 
 */
package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "item")
public class ItemDetails {

	@XmlElement(name = "DOCUMENTTYPE")
	private String documentType;
	
	@XmlElement(name = "SUPPLIERGSTIN")
	private String supplierGSTIN;
	
	@XmlElement(name = "DOCUMENTNUMBER")
	private String documentNumber;
	
	@XmlElement(name = "DOCUMENTDATE")
	private String documentDate;
	
	@XmlElement(name = "IRN")
	private String IRN;
	
	@XmlElement(name = "LINENUMBER")
	private String lineNumber;
	
	@XmlElement(name = "PRODUCTSERIALNO")
	private String productSerialNumber;
	
	@XmlElement(name = "PRODUCT_DESC")
	private String productDescription;
	
	@XmlElement(name = "ISSERVICE")
	private String isService;
	
	@XmlElement(name = "HSN")
	private String HSN;
	
	@XmlElement(name = "BARCODE")
	private String barcode;
	
	@XmlElement(name = "BATCHNAME")
	private String batchName;
	
	@XmlElement(name = "BATCHEXPIRYDATE")
	private String batchExpiryDate;
	
	@XmlElement(name = "WARRANTYDATE")
	private String warrantyDate;
	
	@XmlElement(name = "ORDERLINEREFER")
	private String orderLineReference;
	
	@XmlElement(name = "ORIGINCOUNTRY")
	private String originCountry;
	
	@XmlElement(name = "UQC")
	private String UQC;
	
	@XmlElement(name = "QUANTITY")
	private BigDecimal quantity;
	
	@XmlElement(name = "FREEQUANTITY")
	private BigDecimal freeQuantity;
	
	@XmlElement(name = "UNITPRICE")
	private BigDecimal unitPrice;
	
	@XmlElement(name = "ITEMAMOUNT")
	private BigDecimal itemAmount;
	
	@XmlElement(name = "ITEMDISCOUNT")
	private BigDecimal itemDiscount;
	
	@XmlElement(name = "PRETAXAMOUNT")
	private BigDecimal preTaxAmount;
	
	@XmlElement(name = "ITEMASSESABLEAMT")
	private BigDecimal itemAssessableAmount;
	
	@XmlElement(name = "IGSTRATE")
	private BigDecimal igstRate;
	
	@XmlElement(name = "IGSTAMOUNT")
	private BigDecimal igstAmount;
	
	@XmlElement(name = "CGSTRATE")
	private BigDecimal cgstRate;
	
	@XmlElement(name = "CGSTAMOUNT")
	private BigDecimal cgstAmount;
	
	@XmlElement(name = "SGSTRATE")
	private BigDecimal sgstRate;
	
	@XmlElement(name = "SGSTAMOUNT")
	private BigDecimal sgstAmount;
	
	@XmlElement(name = "CESSADVALOREMRT")
	private BigDecimal cessAdValoremRate;
	
	@XmlElement(name = "CESSADVALOREMAMT")
	private BigDecimal cessAdValoremAmount;
	
	@XmlElement(name = "CESSSPECIFICAMT")
	private BigDecimal cessSpecificAmount;
	
	@XmlElement(name = "STCESSADVALORERT")
	private BigDecimal stateCessAdValoremRate;
	
	@XmlElement(name = "STCESSADVALORAMT")
	private BigDecimal stateCessAdValoremAmount;
	
	@XmlElement(name = "STATECESSAMOUNT")
	private BigDecimal stateCessAmount;
	
	@XmlElement(name = "ITEMOTHERCHARGES")
	private BigDecimal itemOtherCharges;
	
	@XmlElement(name = "TOTALITEMAMOUNT")
	private BigDecimal totalItemAmount;
	
	@XmlElement(name = "PAIDAMOUNT")
	private BigDecimal paidAmount;
	
	@XmlElement(name = "BALANCEAMOUNT")
	private BigDecimal balanceAmount;
	
	@XmlElement(name = "EXPORTDUTY")
	private BigDecimal exportDuty;

}

