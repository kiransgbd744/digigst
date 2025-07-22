/**
 * 
 */
package com.ey.advisory.ewb.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ey.advisory.common.EWBLocalDateTimeAdapter;
import com.ey.advisory.common.LocalDateAdapter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Khalid1.Khan
 *
 */
@XmlRootElement(name="item")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString
public class EwbResponseXmlDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2078731855471131574L;
	
	@XmlElement(name = "DOCTYPE")
	private String docType;
	
	@XmlElement(name = "DOCNUM")
	private String docNum;
	
	@XmlJavaTypeAdapter(value = LocalDateAdapter.class)
	@XmlElement(name = "DOCDATE")
	private LocalDate docDate;

	@XmlElement(name = "EWAYBILLNO")
	private String ewayBillNo;
	
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "EWAYBILLDATE")
	private LocalDateTime ewayBillDate;
	
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "VALIDUPTO")
	private LocalDateTime validUpto;
	
	@XmlElement(name = "ALERT")
	private String alert;
	
	@XmlElement(name = "ERRORCODE")
	private String errorCode;
	
	@XmlElement(name = "ERRORMESSAGE")
	private String errorDesc;

}
