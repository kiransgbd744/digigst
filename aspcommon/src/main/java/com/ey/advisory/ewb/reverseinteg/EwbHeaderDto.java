package com.ey.advisory.ewb.reverseinteg;

import java.util.List;

import com.ey.advisory.ewb.client.domain.EWBHeader;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "IT_DATA")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class EwbHeaderDto {
	
	@XmlElement(name= "item")
	List<EWBHeader> ewbHeaderList ;

}
