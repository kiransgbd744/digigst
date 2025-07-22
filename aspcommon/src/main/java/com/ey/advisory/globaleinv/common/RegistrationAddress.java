package com.ey.advisory.globaleinv.common;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistrationAddress{
	
    @XmlElement(name = "CityName") 
    public String cityName;
    @XmlElement(name = "CountrySubentity") 
    public String countrySubentity;
    @XmlElement(name = "Country") 
    public Country country;
}
