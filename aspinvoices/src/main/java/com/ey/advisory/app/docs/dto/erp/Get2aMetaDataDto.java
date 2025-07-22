package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "ImData")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@Service("Get2aMetaDataDto")
public class Get2aMetaDataDto implements JaxbXmlFormatter {

	@XmlElement(name = "Gstin")
	private String gstin;

	@XmlElement(name = "TaxPeriod")
	private String taxPeriod;
	
	@XmlElement(name = "SectionName")
	private String section;

	@XmlElement(name = "GetBatchId")
	private Long getBatchId;

	@XmlElement(name = "TotalCount")
	private Integer totalCount;

	@XmlElement(name = "DeltaNewCount")
	private Integer deltaNewCount;

	@XmlElement(name = "DeltaModCount")
	private Integer deltaModCount;
	
	@XmlElement(name = "DeltaDelCount")
	private Integer deltaDelCount;
	
	@XmlElement(name = "DeltaTotCount")
	private Integer deltaTotCount;

}
