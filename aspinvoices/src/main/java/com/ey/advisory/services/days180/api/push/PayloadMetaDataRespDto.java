/**
 * 
 */
package com.ey.advisory.services.days180.api.push;

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

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "RESP")
@Data
public class PayloadMetaDataRespDto {

	@XmlElement(name = "item")
	private List<PayloadMetaDataItemDto> dockeyItems;

	public void addItem(PayloadMetaDataItemDto item) {
		dockeyItems.add(item);
	}
}