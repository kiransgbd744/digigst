package com.ey.advisory.app.docs.dto.erp;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * @author Jithendra.B
 *
 */
@XmlRootElement(name = "PAYLOAD")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class GSTR2aAutoReconRevIntgPayloadDto {

	@XmlElement(name = "item")
	private List<GSTR2aAutoReconRevIntgItemDto> itemDtos;

}
