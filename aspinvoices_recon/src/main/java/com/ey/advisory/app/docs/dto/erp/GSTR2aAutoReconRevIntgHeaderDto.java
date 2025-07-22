package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import lombok.Data;

/**
 * @author Jithendra.B
 *
 */
@XmlRootElement(name = "IT_DATA")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Service("GSTR2aAutoReconRevIntgHeaderDto")
public class GSTR2aAutoReconRevIntgHeaderDto implements JaxbXmlFormatter {

	@XmlElement(name = "item")
	private GSTR2aAutoReconRevIntgHeaderItemDto itemHdr;

}
