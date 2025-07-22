package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * @author Jithendra.B
 *
 */
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class GSTR2aAutoReconRevIntgHeaderItemDto {

	
	@XmlElement(name = "CONTROL_ID")
	public String controlId;
	
	@XmlElement(name = "GSTIN")
	public String gstin;
	
	@XmlElement(name = "TOT_CONTROL_REC")
	public Long totalCntrlRec;

	@XmlElement(name = "CHUNK_ID")
	public String chunkId;

	@XmlElement(name = "TOT_CHUNK_REC")
	public Long totalChunkRec;
	
	@XmlElement(name = "PAYLOAD")
	private GSTR2aAutoReconRevIntgPayloadDto payload;

}
