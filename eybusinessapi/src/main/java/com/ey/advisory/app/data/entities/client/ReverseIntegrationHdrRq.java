package com.ey.advisory.app.data.entities.client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.ey.advisory.common.EwbLocalDateAdapter;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class ReverseIntegrationHdrRq {

	@XmlTransient
	protected Long id;

	@XmlElement(name = "EntityPan")
	protected String entityPan;

	@XmlElement(name = "EntityName")
	protected String entityName;

	@XmlElement(name = "DocType")
	protected String docType;

	@XmlElement(name = "DocNo")
	protected String docNo;

	@XmlElement(name = "PayloadId")
	protected String payloadId;

	@XmlElement(name = "SupplierGstin")
	protected String supplierGstin;

	@XmlElement(name = "Companycode")
	protected String companycode;

	@XmlElement(name = "Fiscalyear")
	protected Integer fiscalyear;

	@XmlElement(name = "DocDate")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate docDate;

	@XmlElement(name = "ReceivedDate")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate receivedDate;

	@XmlElementWrapper(name = "Errors")
	@XmlElement(name = "item")
	protected List<RevIntgrtionLineItemsRq> errors = new ArrayList<>();

}
