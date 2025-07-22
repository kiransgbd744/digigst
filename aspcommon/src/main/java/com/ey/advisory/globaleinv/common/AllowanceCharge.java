package com.ey.advisory.globaleinv.common;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AllowanceCharge{
	
    @XmlElement(name = "ChargeIndicator") 
    public Boolean chargeIndicator;
    
    @XmlElement(name = "AllowanceChargeReason") 
    public String allowanceChargeReason;
    
    @XmlElement(name = "Amount") 
    public Integer amount;
    
    @XmlElement(name = "MultiplierFactorNumeric") 
    public Double multiplierFactorNumeric;
    
    @XmlElement(name = "BaseAmount") 
    public Double baseAmount;
}
