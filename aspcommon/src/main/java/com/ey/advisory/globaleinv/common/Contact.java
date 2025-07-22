package com.ey.advisory.globaleinv.common;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Contact{
    @XmlElement(name = "Telephone") 
    public int telephone;
    @XmlElement(name = "Telefax") 
    public int telefax;
    @XmlElement(name = "ElectronicMail") 
    public String electronicMail;
}
