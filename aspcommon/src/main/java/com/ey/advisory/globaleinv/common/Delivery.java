package com.ey.advisory.globaleinv.common;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Delivery{
    @XmlElement(name = "ActualDeliveryDate") 
    public String actualDeliveryDate;
    @XmlElement(name = "DeliveryLocation") 
    public DeliveryLocation deliveryLocation;
}
