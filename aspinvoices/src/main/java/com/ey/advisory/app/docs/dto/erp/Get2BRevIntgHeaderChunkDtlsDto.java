package com.ey.advisory.app.docs.dto.erp;


import java.time.LocalDateTime;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import lombok.Data;


@XmlRootElement(name = "BUSINESSMESSAGE")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Service("Get2BRevIntgHeaderChunkDtlsDto")
public class Get2BRevIntgHeaderChunkDtlsDto implements JaxbXmlFormatter {

	@XmlElement(name = "CURRENT_CHUNK")
	private int currentChunk;
	
	@XmlElement(name = "TOTALCHUNK")
	private int totalChunk;
	
	@XmlElement(name = "TIMESTAMP")
	private LocalDateTime timestamp;
	
}
