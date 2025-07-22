package com.ey.advisory.app.vendor.service;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@XmlRootElement(name = "VENDORDETAILS")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@ToString
public class VendorValidatorResponseListDto {

	@XmlElement(name = "item")
	private List<VendorDto> items = new ArrayList<>();

	public void addItem(VendorDto itemObj) {

		items.add(itemObj);
	}

	public void addAllItem(List<VendorDto> itemObj) {

		items.addAll(itemObj);
	}

}
