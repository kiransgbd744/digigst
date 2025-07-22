/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Hemasundar.J
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@ToString
public class OutwardErrorItemsDto {

	@XmlElement(name="item")
	private List<OutwardErrorItemDto> errors;
}
