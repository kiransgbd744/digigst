package com.ey.advisory.globaleinv.common;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceLine {
	
	@XmlElement(name = "ID")
	public int iD;
	
	@XmlElement(name = "Note")
	public String note;
	
	@XmlElement(name = "InvoicedQuantity")
	public Integer invoicedQuantity;
	
	@XmlElement(name = "LineExtensionAmount")
	public Double lineExtensionAmount;
	
	@XmlElement(name = "AccountingCost")
	public String accountingCost;
	
	@XmlElement(name = "OrderLineReference")
	public OrderLineReference orderLineReference;
	
	@XmlElement(name = "AllowanceCharge")
	public List<AllowanceCharge> allowanceCharge;
	
	@XmlElement(name = "TaxTotal")
	public TaxTotal taxTotal;
	
	@XmlElement(name = "Item")
	public Item item;
	
	@XmlElement(name = "Price")
	public Price price;

}
