/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Hemasundar.J
 *
 */
@XmlRootElement(name = "ImData")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@Service("Anx1ReviewSummaryRequestDto")
public class Anx1ReviewSummaryRequestDto implements JaxbXmlFormatter {

	@XmlElement(name = "item")
	private List<Anx1ReviewSummaryRequestItemDto> imItem;

}
