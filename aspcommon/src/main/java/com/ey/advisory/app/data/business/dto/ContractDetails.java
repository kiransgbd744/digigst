/**
 * 
 */
package com.ey.advisory.app.data.business.dto;

import java.time.LocalDate;

import com.ey.advisory.common.EwbLocalDateAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Arun.KA
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ContractDetails {
	
	@Expose
	@SerializedName("receiptAdviceRef")
	@XmlElement(name = "recp-adv-refrnc")
	protected String receiptAdviceReference;
	
	@Expose
	@SerializedName("receiptAdviceDate")
	@XmlElement(name = "rcpt-adv-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate receiptAdviceDate;
	
	@Expose
	@SerializedName("tenderRef")
	@XmlElement(name = "tndr-ref")
	protected String tenderReference;
	
	@Expose
	@SerializedName("externalRef")
	@XmlElement(name = "ext-ref")
	protected String externalReference;

	@Expose
	@SerializedName("projectRef")
	@XmlElement(name = "prj-ref")
	protected String projectReference;

	@Expose
	@SerializedName("contractRef")
	@XmlElement(name = "contrct-ref")
	protected String contractReference;

	@Expose
	@SerializedName("custPoRefNo")
	@XmlElement(name = "cst-po-ref-no")
	protected String customerPOReferenceNumber;
	
	@Expose
	@SerializedName("custPoRefDate")
	@XmlElement(name = "cust-po-ref-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate customerPOReferenceDate;


	
	public static boolean isEmpty(ContractDetails contrDtls) {
		ContractDetails contDtls = new ContractDetails();
		return contDtls.hashCode() == contrDtls.hashCode();
	}
}
