package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "urn:ZGST_GSTR_3B")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class Gstrn3BFinalResultDto {
	
	@XmlElement(name="IT_DATA_T1")
	private Gstr3BDashboardSumOuterDto gstrDashboardDto;
	
	@XmlElement(name="IT_DATA_T2")
	private Gstr3BDetailRespOuterDto  gstrRespDetDto;
	
	@XmlElement(name="IT_DATA_T3")
	private Gstr3BPosDetailRespOuterDto gstrPosDetail;
	
}
