package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public class BCAPIOutwardPayloadErrorItemDto {

	@XmlElement(name = "EntityPan")
	private String entityPan;

	@XmlElement(name = "EntityName")
	private String entityName;

	@XmlElement(name = "DocType")
	private String docType;

	@XmlElement(name = "DocNo")
	private String docNo;

	@XmlElement(name = "PayloadId")
	private String payloadId;

	@XmlElement(name = "SupplierGstin")
	private String supplierGstin;

	@XmlElement(name = "Companycode")
	private String companycode;

	@XmlElement(name = "Fiscalyear")
	private String fiscalyear;

	@XmlElement(name = "AccountVoucherNo")
	private String accVcherNo;

	@XmlElement(name = "DocDate")
	private String docDate;

	@XmlElement(name = "ReceivedDate")
	private String receivedDate;

	@XmlElement(name = "Errors")
	private OutwardDocErrorsDto outwardDocErrorsDto;
	
	@XmlElement(name = "entityId")
	private String entityId;

}
