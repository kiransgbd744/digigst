/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Umesh
 *
 */
@XmlRootElement(name="ImIData")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class Gstr1ReviewLineSummaryRequestDto {
	
	@XmlElement(name="item")
	private List<Gstr1ReviewSummaryRequestItemDto> imItem;

}
