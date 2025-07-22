/**
 * 
 */
package com.ey.advisory.services.days180.api.push;

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

@XmlRootElement(name = "ERRORDETAILS")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class PayloadMetaDataErrorItemDto {
	
	@XmlElement(name="item")
	private List<ErrorItem> errorItems =  new ArrayList<>();
	
	public void addItem(ErrorItem item) {
		errorItems.add(item);

}
}
