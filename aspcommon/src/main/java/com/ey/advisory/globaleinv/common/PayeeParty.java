package com.ey.advisory.globaleinv.common;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PayeeParty{
    @XmlElement(name = "PartyIdentification") 
    public PartyIdentification partyIdentification;
    @XmlElement(name = "PartyName") 
    public PartyName partyName;
    @XmlElement(name = "PartyLegalEntity") 
    public PartyLegalEntity partyLegalEntity;
}
