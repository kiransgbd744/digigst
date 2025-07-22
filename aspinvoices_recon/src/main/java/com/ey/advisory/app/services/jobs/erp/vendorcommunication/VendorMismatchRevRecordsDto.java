package com.ey.advisory.app.services.jobs.erp.vendorcommunication;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author vishal.verma
 *
 */

@XmlRootElement(name="IT_DATA")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString
public class VendorMismatchRevRecordsDto {
	
	@XmlElement(name="item")
	private List<VendorMismatchDto> item;

}
 
 
