package com.ey.advisory.globaleinv.common;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Price{
  
	@XmlElement(name = "PriceAmount") 
    public Double priceAmount;
	
    @XmlElement(name = "BaseQuantity") 
    public Integer baseQuantity;
    
    @XmlElement(name = "AllowanceCharge") 
    public AllowanceCharge allowanceCharge;
}
