package com.ey.advisory.core.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * @author Jithendra.B
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "RESPONSE", namespace="urn:sap-com:document:sap:rfc:functions")
@Data
public class GSTR2aAutoReconRevIntgResponseDto {

	@XmlElement(name = "CONTROL_ID")
	public String controlId;
	
	@XmlElement(name = "GSTIN")
	public String gstin;
	
	@XmlElement(name = "CHUNK_ID")
	public String chunkId;
	
	@XmlElement(name = "RECEIVED_CHUNK_RECORD")
	public Long receivedChnkRec;

	@XmlElement(name = "TOTAL_CHUNK_RECORD")
	public Long totalCntrlRec;

}
