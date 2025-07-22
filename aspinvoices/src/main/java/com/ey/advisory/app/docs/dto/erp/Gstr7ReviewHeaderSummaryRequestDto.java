/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp;


import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 * @author siva
 *
 */
@XmlRootElement(name="ImData")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@Service("Gstr7ReviewHeaderSummaryRequestDto")
public class Gstr7ReviewHeaderSummaryRequestDto implements JaxbXmlFormatter{
	
	@XmlElement(name="item")
	private List<Gstr7ProcessAndReviewSummaryDto> processAndReviewSummary;
	
	
	

}
