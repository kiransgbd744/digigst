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
public class PreDocDetails {
	
	@Expose
	@SerializedName("preceedingInvNo")
	@XmlElement(name = "prec-inv-no")
	private String preceedingInvoiceNumber;

	@Expose
	@SerializedName("preceedingInvDate")
	@XmlElement(name = "prec-inv-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	private LocalDate preceedingInvoiceDate;
	
	@Expose
	@SerializedName("invRef")
	@XmlElement(name = "inv-ref")
	private String invoiceReference;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((invoiceReference == null) ? 0
				: invoiceReference.hashCode());
		result = prime * result + ((preceedingInvoiceDate == null) ? 0
				: preceedingInvoiceDate.hashCode());
		result = prime * result + ((preceedingInvoiceNumber == null) ? 0
				: preceedingInvoiceNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PreDocDetails other = (PreDocDetails) obj;
		if (invoiceReference == null) {
			if (other.invoiceReference != null)
				return false;
		} else if (!invoiceReference.equals(other.invoiceReference))
			return false;
		if (preceedingInvoiceDate == null) {
			if (other.preceedingInvoiceDate != null)
				return false;
		} else if (!preceedingInvoiceDate.equals(other.preceedingInvoiceDate))
			return false;
		if (preceedingInvoiceNumber == null) {
			if (other.preceedingInvoiceNumber != null)
				return false;
		} else if (!preceedingInvoiceNumber
				.equals(other.preceedingInvoiceNumber))
			return false;
		return true;
	}

	
	public static boolean isEmpty(PreDocDetails predtls) {
		PreDocDetails predocDtls = new PreDocDetails();
		return predocDtls.hashCode() == predtls.hashCode();
	}

	
}
