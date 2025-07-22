/**
 * 
 */
package com.ey.advisory.vendor.master.api.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Shashikant.Shukla
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class VendorMasterApiSoapHdrRq {
	@XmlTransient
	protected Long id;
	/* this is just added for soap request for vendor */

	@XmlElement(name = "id-token")
	protected String idToken;

	@XmlElement(name = "payload-id")
	private String payloadId;

	@XmlElement(name = "company-cd")
	protected String companyCode;

	@XmlElement(name = "source-id")
	protected String sourceId;

	@XmlElementWrapper(name = "lineItems")
	@XmlElement(name = "item")
	protected List<VendorMasterApiTransRq> lineItems = new ArrayList<>();
}