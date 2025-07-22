package com.ey.advisory.app.services.jobs.erp.processedrecords;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="IM_DATA")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class Anx1ProcessedRevRecordsDto {

		@XmlElement(name="item")
		private List<ProcessedRecDetForGstinTaxPeriodDto> IM_DATA;

}
