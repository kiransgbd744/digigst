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
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class DocErrorsDto {

	@XmlElement(name = "Id")
	private Long id;

	@XmlElement(name = "SupplierGstin")
	private String sgstin;

	@XmlElement(name = "DocNo")
	private String docNo;

	@XmlElement(name = "DocType")
	private String docType;

	@XmlElement(name = "DocDate")
	private String docDate;

	@XmlElement(name = "DocStatus")
	private String docStatus;

	@XmlElement(name = "GstinError")
	private String gstinError;

	@XmlElement(name = "InwdError")
	private String inwdError;

	@XmlElement(name = "Companycode")
	private String companycode;

	@XmlElement(name = "Fiscalyear")
	private String fiscalyear;

	@XmlElement(name = "AccountVoucherNo")
	private String accountVoucherNo;
	
	@XmlElement(name = "EntityName")
	private String entityName;
	
	@XmlElement(name = "EntityPan")
	private String entityPan;
	
	@XmlElement(name = "PayloadId")
	private String payloadId;
	
	@XmlElement(name = "ReceivedDate")
	private String receivedDate;
	
	@XmlElement(name="Errors")
	private OutwardErrorItemsDto errors;

}
