package com.ey.advisory.app.docs.dto.erp;

import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class DocPopupVerticalItemDto {

	@XmlElement(name = "SeriesFor")
	private Long seriesFor;

	@XmlElement(name = "SeriesFrom")
	private String seriesFrom;

	@XmlElement(name = "SeriesTo")
	private String seriesTo;

	@XmlElement(name = "TotalNumber")
	private BigInteger totalNumber;

	@XmlElement(name = "Cancelled")
	private BigInteger cancelled;

	@XmlElement(name = "NetIssued")
	private BigInteger netIssued;
}
