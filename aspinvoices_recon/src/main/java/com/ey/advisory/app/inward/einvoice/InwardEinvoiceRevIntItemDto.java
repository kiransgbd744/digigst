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
@XmlRootElement(name = "ITEM")
@XmlAccessorType(XmlAccessType.FIELD)
public class InwardEinvoiceRevIntItemDto {

	@XmlElement(name = "item")
	public List<ItemDetails> item = new ArrayList<>();
	
	public void addItem(ItemDetails itemObj) {
		
		item.add(itemObj);
	}
}
