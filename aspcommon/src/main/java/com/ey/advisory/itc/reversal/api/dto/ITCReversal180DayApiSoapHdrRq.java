package com.ey.advisory.itc.reversal.api.dto;

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
import lombok.ToString;

/**
 * @author Akhilesh.Yadav
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@ToString
@Getter
@Setter
public class ITCReversal180DayApiSoapHdrRq {
	@XmlTransient
	protected Long id;
	/* this is just added for soap request for vendor */

	@XmlElement(name = "payload-id")
	private String payloadId;
	
	@XmlElement(name = "checksum")
	private String checksum;
	
	@XmlElement(name = "doc-count")
	private String docCount;
	
	@XmlElement(name = "push-type")
	private String pushType;

	@XmlElement(name = "company-code")
	protected String companyCode;

	@XmlElement(name = "source-id")
	protected String sourceId;
	
	@XmlElement(name = "id-token")
	protected String idToken;

	@XmlElementWrapper(name = "lineItems")
	@XmlElement(name = "item")
	protected List<ITCReversal180DayApiTransRq> lineItems = new ArrayList<>();
}
