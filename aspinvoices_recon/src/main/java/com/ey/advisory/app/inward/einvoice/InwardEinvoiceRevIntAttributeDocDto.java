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
@XmlRootElement(name = "ATTRIBUTES")
public class InwardEinvoiceRevIntAttributeDocDto {

	@XmlElement(name = "item")
	public List<Attribute> attributes = new ArrayList<>();

	public void addItem(Attribute itemObj) {

		attributes.add(itemObj);
	}
	
	public void addAllItem(List<Attribute> itemObj) {

		attributes.addAll(itemObj);
	}
}
