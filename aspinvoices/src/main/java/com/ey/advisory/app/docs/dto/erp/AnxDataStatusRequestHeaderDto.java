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
 * @author Hemasundar.J
 *
 */
@XmlRootElement(name = "urn:ZGST_DATA_STATUS")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class AnxDataStatusRequestHeaderDto {

	@XmlElement(name = "IT_DATA_T1")
	private AnxDataStatusRequestDataHeaderDto dataHeaderDto;

	@XmlElement(name = "IT_DATA_T2")
	private AnxDataStatusRequestDataSummaryHeaderDto dataSummaryHeaderDto;
}
