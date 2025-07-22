/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Mahesh.Golla
 *
 */
@XmlRootElement(name="item")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class Ret1ProcessReviewSummItemRequestDto {
	
	@XmlElement(name = "item")
	private Ret1ReviewSummaryRequestItemDto processData;
	
}