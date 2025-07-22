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
@XmlRootElement(name = "ADDITIONAL")
public class InwardEinvoiceRevIntAdditionalDocDto {

	@XmlElement(name = "item")
	public List<Additional> additional = new ArrayList<>();

	public void addItem(Additional itemObj) {

		additional.add(itemObj);
	}
	
	public void addAllItem(List<Additional> itemObj) {

		additional.addAll(itemObj);
	}
}
