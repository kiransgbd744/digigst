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
public class Item{
	
    @XmlElement(name = "Description") 
    public String description;
    
    @XmlElement(name = "Name") 
    public String name;
    
    @XmlElement(name = "SellersItemIdentification") 
    public SellersItemIdentification sellersItemIdentification;
    
    @XmlElement(name = "StandardItemIdentification") 
    public StandardItemIdentification standardItemIdentification;
    
    @XmlElement(name = "CommodityClassification") 
    public List<CommodityClassification> commodityClassification;
    
    @XmlElement(name = "ClassifiedTaxCategory") 
    public ClassifiedTaxCategory classifiedTaxCategory;
    
    @XmlElement(name = "AdditionalItemProperty") 
    public AdditionalItemProperty additionalItemProperty;
    
}
