/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="IT_DATA_T1")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class AnxDataStatusRequestDataHeaderDto {

	@XmlElement(name="item")
	private List<AnxDataStatusRequestItemDto> ImItem = new ArrayList<>();
}
