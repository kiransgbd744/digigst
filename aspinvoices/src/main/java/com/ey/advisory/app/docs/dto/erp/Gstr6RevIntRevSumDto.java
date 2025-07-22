package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@XmlRootElement(name = "ReviewSummary")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@ToString
public class Gstr6RevIntRevSumDto {

	@XmlElement(name = "Inward")
	private Gstr6RevIntRevSumInwardDto inwardDto;

	@XmlElement(name = "Distribution")
	private Gstr6RevIntRevSumDistributionDto distributionDto;

}
