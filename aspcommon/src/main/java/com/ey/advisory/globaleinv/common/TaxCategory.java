package com.ey.advisory.globaleinv.common;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TaxCategory{
	
    @XmlElement(name = "ID") 
    public String iD;
    
    @XmlElement(name = "Percent") 
    public int percent;
    
    @XmlElement(name = "TaxScheme") 
    public TaxScheme taxScheme;
    
    @XmlElement(name = "TaxExemptionReasonCode") 
    public String taxExemptionReasonCode;
    
    @XmlElement(name = "TaxExemptionReason") 
    public String taxExemptionReason;
}
