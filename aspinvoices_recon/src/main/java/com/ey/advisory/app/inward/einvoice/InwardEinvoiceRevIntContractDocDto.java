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
@XmlRootElement(name = "CONTRACT")
public class InwardEinvoiceRevIntContractDocDto {

	@XmlElement(name = "item")
	public List<Contract> contract = new ArrayList<>();

	public void addItem(Contract itemObj) {

		contract.add(itemObj);
	}
	
	public void addAllItem(List<Contract> itemObj) {

		contract.addAll(itemObj);
	}
}
