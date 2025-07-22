/**
 * 
 */
package com.ey.advisory.app.inward.einvoice;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "PRECEEDING")
public class InwardEinvoiceRevIntPrecedingDocDto {

	@XmlElement(name = "item")
	public List<PreceedingXmlDto> preceeding = new ArrayList<>();

	public void addItem(PreceedingXmlDto itemObj) {

		preceeding.add(itemObj);
	}
	
	public void addAllItem(List<PreceedingXmlDto> itemObj) {

		preceeding.addAll(itemObj);
	}
}
