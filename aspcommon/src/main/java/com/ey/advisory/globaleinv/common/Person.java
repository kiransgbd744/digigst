package com.ey.advisory.globaleinv.common;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Person{
    @XmlElement(name = "FirstName") 
    public String firstName;
    @XmlElement(name = "FamilyName") 
    public String familyName;
    @XmlElement(name = "MiddleName") 
    public String middleName;
    @XmlElement(name = "JobTitle") 
    public String jobTitle;
}
