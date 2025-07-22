package com.ey.advisory.app.docs.dto.erp;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@XmlRootElement(name = "Inward")
@XmlAccessorType(XmlAccessType.FIELD)
public class Gstr6RevIntRevSumInwardDto {

	@XmlElement(name = "item")
	private List<Gstr6RevIntRevSumInwardItemDto> items;
	
}
