package com.ey.advisory.globaleinv.common;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Party{
	
    @XmlElement(name = "EndpointID") 
    public Long endpointID;
    @XmlElement(name = "PartyIdentification") 
    public PartyIdentification partyIdentification;
    @XmlElement(name = "PartyName") 
    public PartyName partyName;
    @XmlElement(name = "PostalAddress") 
    public PostalAddress postalAddress;
    @XmlElement(name = "PartyTaxScheme") 
    public PartyTaxScheme partyTaxScheme;
    @XmlElement(name = "PartyLegalEntity") 
    public PartyLegalEntity partyLegalEntity;
    @XmlElement(name = "Contact") 
    public Contact contact;
    @XmlElement(name = "Person") 
    public Person person;
}
