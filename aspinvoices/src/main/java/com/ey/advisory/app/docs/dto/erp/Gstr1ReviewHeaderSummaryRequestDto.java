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
 * @author Umesh
 *
 */
@XmlRootElement(name = "ImData")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@Service("Gstr1ReviewHeaderSummaryRequestDto")
public class Gstr1ReviewHeaderSummaryRequestDto implements JaxbXmlFormatter {

	@XmlElement(name = "item")
	private List<Gstr1ProcessAndReviewSummaryDto> processAndReviewSummary;

}
