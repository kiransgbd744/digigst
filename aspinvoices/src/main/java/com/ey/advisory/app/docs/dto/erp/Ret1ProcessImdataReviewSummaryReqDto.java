package com.ey.advisory.app.docs.dto.erp;

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
 * @author Mahesh.Golla
 *
 */
@XmlRootElement(name = "ImData")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@ToString
public class Ret1ProcessImdataReviewSummaryReqDto {

	@XmlElement(name = "Item")
	private List<Ret1HeaderItemProcessReviewSummaryReqDto> item;
}
