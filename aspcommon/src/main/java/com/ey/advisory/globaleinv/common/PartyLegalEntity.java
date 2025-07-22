package com.ey.advisory.globaleinv.common;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PartyLegalEntity{
    @XmlElement(name = "RegistrationName") 
    public String registrationName;
    @XmlElement(name = "CompanyID") 
    public long companyID;
    @XmlElement(name = "RegistrationAddress") 
    public RegistrationAddress registrationAddress;
}
