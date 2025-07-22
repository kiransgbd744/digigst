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
public class TaxTotal{
	
    @XmlElement(name = "TaxAmount") 
    public Double taxAmount;
    
    @XmlElement(name = "RoundingAmount") 
    public Double roundingAmount;
    
    @XmlElement(name = "TaxSubtotal") 
    public List<TaxSubtotal> taxSubtotal;
}
