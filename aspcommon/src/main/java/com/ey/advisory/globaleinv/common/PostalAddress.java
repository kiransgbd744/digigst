package com.ey.advisory.globaleinv.common;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class PostalAddress {
	@XmlElement(name = "ID")
	public Long iD;
	@XmlElement(name = "Postbox")
	public Integer postbox;
	@XmlElement(name = "StreetName")
	public String streetName;
	@XmlElement(name = "AdditionalStreetName")
	public String additionalStreetName;
	@XmlElement(name = "BuildingNumber")
	public String buildingNumber;
	@XmlElement(name = "Department")
	public String department;
	@XmlElement(name = "CityName")
	public String cityName;
	@XmlElement(name = "PostalZone")
	public String postalZone;
	@XmlElement(name = "CountrySubentityCode")
	public String countrySubentityCode;
	@XmlElement(name = "Country")
	public Country country;
	@XmlElement(name = "CountrySubentity")
	public String countrySubentity;
}
