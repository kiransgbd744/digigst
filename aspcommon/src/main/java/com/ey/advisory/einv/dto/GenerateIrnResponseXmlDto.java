/**
 * 
 */
package com.ey.advisory.einv.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ey.advisory.common.EWBLocalDateTimeAdapter;
import com.ey.advisory.common.LocalDateAdapter;
import com.ey.advisory.common.LocalDateTimeAdapter;

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
@Setter
@Getter
@ToString
public class GenerateIrnResponseXmlDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8670829001117591991L;

	@XmlElement(name = "ACKNO")
	private Long ackNo;
	
	@XmlJavaTypeAdapter(value = LocalDateTimeAdapter.class)
	@XmlElement(name = "ACKDT")
	private LocalDateTime ackDt;
	
	@XmlElement(name = "IRN")
	private String irn;
	
	@XmlElement(name = "DOCTYPE")
	private String docType;
	
	@XmlElement(name = "DOCNUM")
	private String docNum;
	
	@XmlJavaTypeAdapter(value = LocalDateAdapter.class)
	@XmlElement(name = "DOCDATE")
	private LocalDate docDate;
	
	@XmlElement(name = "SELLERGSTIN")
	private String sellerGstin;
	
	@XmlElement(name = "BUYERGSTIN")
	private String buyerGstin;
	
	@XmlElement(name = "SIGNEDINVOICE")
	private String signedInvoice;
	
	@XmlElement(name = "SIGNEDQRCODE")
	private String signedQRCode;
	
	@XmlElement(name = "FORMATTEDQRCODE")
	private String formattedQrCode;
	
	@XmlElement(name = "QRDATA")
	private String qrData;
	
	@XmlElement(name = "STATUS")
	private String status;
	
	@XmlElement(name = "ERRORMESSAGE")
	private String errorMessage;
	
	@XmlElement(name = "ERRORCODE")
	private String errorCode;
	
	@XmlElement(name = "EWBNO")
	public String ewbNo;

	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "EWBDT")
	public LocalDateTime ewbDt;

	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "EWBVALIDDT")
	public LocalDateTime ewbValidTill;

	@XmlElement(name = "INFOERRCD")
	private String infoErrorCode;
	
	@XmlElement(name = "INFOERRMSG")
	private String infoErrorMessage;
	
	@XmlElement(name = "NICDISTANCE")
	private String nicDistance;


}
